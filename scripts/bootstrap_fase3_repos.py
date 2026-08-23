from pathlib import Path

API = Path(__file__).resolve().parents[1]
ROOT = API.parent
DB = ROOT / "Fiap-Pos-Tech-Challenge-infra-db"
K8S = ROOT / "Fiap-Pos-Tech-Challenge-infra-k8s"
LAMBDA = ROOT / "Fiap-Pos-Tech-Challenge-auth-lambda"


def write(base: Path, relative: str, content: str) -> None:
    path = base / relative
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content.replace("\r\n", "\n"), encoding="utf-8")
    print(f"wrote {path}")


def main() -> None:
    write_db()
    write_k8s()
    write_lambda()
    print("done")


def write_db() -> None:
    write(
        DB,
        "main.tf",
        r'''provider "kubernetes" {
  config_path = var.kubeconfig_path
}

resource "kubernetes_namespace" "db" {
  metadata {
    name = var.namespace
    labels = {
      "app.kubernetes.io/part-of"   = "oficina-mecanica"
      "app.kubernetes.io/component" = "database"
      environment                   = var.environment
    }
  }
}

resource "kubernetes_secret" "postgres" {
  metadata {
    name      = "postgres-credentials"
    namespace = kubernetes_namespace.db.metadata[0].name
    labels = {
      app                           = "postgres"
      "app.kubernetes.io/component" = "database"
    }
  }

  data = {
    POSTGRES_DB       = var.postgres_db
    POSTGRES_USER     = var.postgres_user
    POSTGRES_PASSWORD = var.postgres_password
    DATABASE_URL      = "jdbc:postgresql://postgres.${var.namespace}.svc.cluster.local:5432/${var.postgres_db}"
  }

  type = "Opaque"
}

resource "kubernetes_persistent_volume_claim" "postgres" {
  metadata {
    name      = "postgres-data"
    namespace = kubernetes_namespace.db.metadata[0].name
    labels = {
      app = "postgres"
    }
  }

  spec {
    access_modes = ["ReadWriteOnce"]
    resources {
      requests = {
        storage = var.storage_size
      }
    }
  }

  wait_until_bound = false
}

resource "kubernetes_deployment" "postgres" {
  metadata {
    name      = "postgres"
    namespace = kubernetes_namespace.db.metadata[0].name
    labels = {
      app                           = "postgres"
      "app.kubernetes.io/name"      = "postgres"
      "app.kubernetes.io/component" = "managed-database"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "postgres"
      }
    }

    strategy {
      type = "Recreate"
    }

    template {
      metadata {
        labels = {
          app = "postgres"
        }
      }

      spec {
        container {
          name  = "postgres"
          image = "postgres:15-alpine"

          port {
            container_port = 5432
            name           = "postgres"
          }

          env {
            name = "POSTGRES_DB"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.postgres.metadata[0].name
                key  = "POSTGRES_DB"
              }
            }
          }

          env {
            name = "POSTGRES_USER"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.postgres.metadata[0].name
                key  = "POSTGRES_USER"
              }
            }
          }

          env {
            name = "POSTGRES_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.postgres.metadata[0].name
                key  = "POSTGRES_PASSWORD"
              }
            }
          }

          env {
            name  = "PGDATA"
            value = "/var/lib/postgresql/data/pgdata"
          }

          volume_mount {
            name       = "postgres-data"
            mount_path = "/var/lib/postgresql/data"
          }

          readiness_probe {
            exec {
              command = ["sh", "-c", "pg_isready -U $POSTGRES_USER -d $POSTGRES_DB"]
            }
            initial_delay_seconds = 10
            period_seconds        = 10
          }

          liveness_probe {
            exec {
              command = ["sh", "-c", "pg_isready -U $POSTGRES_USER -d $POSTGRES_DB"]
            }
            initial_delay_seconds = 20
            period_seconds        = 15
          }

          resources {
            requests = {
              cpu    = "100m"
              memory = "256Mi"
            }
            limits = {
              cpu    = "500m"
              memory = "512Mi"
            }
          }
        }

        volume {
          name = "postgres-data"
          persistent_volume_claim {
            claim_name = kubernetes_persistent_volume_claim.postgres.metadata[0].name
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "postgres" {
  metadata {
    name      = "postgres"
    namespace = kubernetes_namespace.db.metadata[0].name
    labels = {
      app = "postgres"
    }
  }

  spec {
    selector = {
      app = "postgres"
    }

    port {
      name        = "postgres"
      port        = 5432
      target_port = 5432
    }

    type = "ClusterIP"
  }
}
''',
    )
    write(
        DB,
        "README.md",
        """# Oficina — Infrastructure (Managed Database)

Terraform that provisions a dedicated PostgreSQL service simulating a managed database
(RDS-like) on a local Kubernetes cluster. The instance lives in the `oficina-db` namespace,
with PVC, probes, resource limits and credentials stored in a Kubernetes Secret.

## Purpose

Independent database lifecycle, consumed by `oficina-api` and `auth-lambda`.

## Technologies

Terraform, Kubernetes provider, PostgreSQL 15, GitHub Actions.

## Architecture

```mermaid
flowchart LR
  TF[Terraform] --> NS[Namespace oficina-db]
  TF --> SEC[Secret]
  TF --> PVC[PVC]
  TF --> DEP[PostgreSQL 15]
  DEP --> SVC[ClusterIP :5432]
  API[oficina-api] --> SVC
  FN[auth-lambda] --> SVC
```

## Deploy

Requires the kind cluster from `Fiap-Pos-Tech-Challenge-infra-k8s`.

```bash
cp terraform.tfvars.example terraform.tfvars
terraform init
terraform apply
```

Without Kubernetes:

```bash
docker compose up -d
```

Host: `postgres.oficina-db.svc.cluster.local:5432` (in-cluster) or `localhost:5432` (Compose).

## CI/CD

PR: `terraform fmt` + `validate`. Push to `homolog`/`main`: kind smoke test.

Protect `main` with required pull requests.

## Related repositories

- https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-infra-k8s
- https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-auth-lambda
- https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-1
""",
    )

    write_k8s()
    write_lambda()


def write_k8s() -> None:
    write(
        K8S,
        ".gitignore",
        """.terraform/
.terraform.lock.hcl
*.tfstate
*.tfstate.*
*.tfplan
crash.log
kubeconfig
*.kubeconfig
.idea/
.vscode/
.env
""",
    )
    write(
        K8S,
        "versions.tf",
        """terraform {
  required_version = ">= 1.5.0"

  required_providers {
    kind = {
      source  = "tehcyx/kind"
      version = "~> 0.9"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.31"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.15"
    }
  }
}
""",
    )
    write(
        K8S,
        "variables.tf",
        """variable "cluster_name" {
  description = "Name of the local kind cluster"
  type        = string
  default     = "oficina-cluster"
}

variable "app_namespace" {
  description = "Namespace for the workshop application and serverless functions"
  type        = string
  default     = "oficina"
}

variable "kong_namespace" {
  description = "Namespace for the Kong API Gateway"
  type        = string
  default     = "kong"
}

variable "observability_namespace" {
  description = "Namespace for Jaeger and optional Datadog agent"
  type        = string
  default     = "observability"
}

variable "kong_proxy_host_port" {
  description = "Host port mapped to Kong proxy NodePort"
  type        = number
  default     = 8000
}

variable "kong_admin_host_port" {
  description = "Host port mapped to Kong admin NodePort"
  type        = number
  default     = 8001
}

variable "jaeger_host_port" {
  description = "Host port mapped to Jaeger UI"
  type        = number
  default     = 16686
}

variable "mailhog_host_port" {
  description = "Host port mapped to MailHog UI"
  type        = number
  default     = 8025
}

variable "jwt_secret" {
  description = "HS512 secret shared by Kong, auth-lambda and oficina-api"
  type        = string
  default     = "localDevelopmentSecretChangeMeInProductionEnvironments123456"
  sensitive   = true
}

variable "jwt_issuer" {
  description = "JWT issuer / Kong key claim"
  type        = string
  default     = "oficina-auth"
}

variable "environment" {
  description = "Target environment"
  type        = string
  default     = "homolog"
}

variable "datadog_api_key" {
  description = "Optional Datadog API key. Empty string skips the agent."
  type        = string
  default     = ""
  sensitive   = true
}
""",
    )
    write(
        K8S,
        "kong.yml.tftpl",
        """_format_version: "3.0"

consumers:
  - username: oficina-client
    jwt_secrets:
      - key: ${jwt_issuer}
        algorithm: HS512
        secret: "${jwt_secret}"

services:
  - name: auth-lambda
    url: http://auth-lambda.${app_namespace}.svc.cluster.local:8080
    routes:
      - name: auth-cpf
        paths:
          - /auth/cpf
        methods:
          - POST
        strip_path: false

  - name: notify-lambda
    url: http://notify-lambda.${app_namespace}.svc.cluster.local:8080
    routes:
      - name: notify
        paths:
          - /notify
        methods:
          - POST
        strip_path: false

  - name: oficina-api-public
    url: http://oficina-api.${app_namespace}.svc.cluster.local:80
    routes:
      - name: api-auth
        paths:
          - /api/auth
        strip_path: false
      - name: swagger
        paths:
          - /swagger-ui
          - /swagger-ui.html
          - /v3/api-docs
        strip_path: false
      - name: actuator-health
        paths:
          - /actuator/health
        strip_path: false

  - name: oficina-api-protected
    url: http://oficina-api.${app_namespace}.svc.cluster.local:80
    routes:
      - name: api-protected
        paths:
          - /api
        strip_path: false
    plugins:
      - name: jwt
        config:
          key_claim_name: iss
          claims_to_verify:
            - exp
          header_names:
            - Authorization
      - name: correlation-id
        config:
          header_name: X-Correlation-Id
          generator: uuid
          echo_downstream: true
      - name: prometheus
""",
    )
    write(
        K8S,
        "main.tf",
        r'''resource "kind_cluster" "this" {
  name           = var.cluster_name
  wait_for_ready = true

  kind_config {
    kind        = "Cluster"
    api_version = "kind.x-k8s.io/v1alpha4"

    node {
      role = "control-plane"

      extra_port_mappings {
        container_port = 30080
        host_port      = var.kong_proxy_host_port
        protocol       = "TCP"
      }

      extra_port_mappings {
        container_port = 30081
        host_port      = var.kong_admin_host_port
        protocol       = "TCP"
      }

      extra_port_mappings {
        container_port = 31686
        host_port      = var.jaeger_host_port
        protocol       = "TCP"
      }

      extra_port_mappings {
        container_port = 30025
        host_port      = var.mailhog_host_port
        protocol       = "TCP"
      }
    }
  }
}

provider "kubernetes" {
  host                   = kind_cluster.this.endpoint
  client_certificate     = kind_cluster.this.client_certificate
  client_key             = kind_cluster.this.client_key
  cluster_ca_certificate = kind_cluster.this.cluster_ca_certificate
}

provider "helm" {
  kubernetes {
    host                   = kind_cluster.this.endpoint
    client_certificate     = kind_cluster.this.client_certificate
    client_key             = kind_cluster.this.client_key
    cluster_ca_certificate = kind_cluster.this.cluster_ca_certificate
  }
}

resource "kubernetes_namespace" "oficina" {
  metadata {
    name = var.app_namespace
    labels = {
      "app.kubernetes.io/part-of" = "oficina-mecanica"
      environment                 = var.environment
    }
  }
}

resource "kubernetes_namespace" "kong" {
  metadata {
    name = var.kong_namespace
    labels = {
      "app.kubernetes.io/part-of"   = "oficina-mecanica"
      "app.kubernetes.io/component" = "api-gateway"
    }
  }
}

resource "kubernetes_namespace" "observability" {
  metadata {
    name = var.observability_namespace
    labels = {
      "app.kubernetes.io/part-of"   = "oficina-mecanica"
      "app.kubernetes.io/component" = "observability"
    }
  }
}

resource "helm_release" "metrics_server" {
  name       = "metrics-server"
  repository = "https://kubernetes-sigs.github.io/metrics-server/"
  chart      = "metrics-server"
  namespace  = "kube-system"

  set {
    name  = "args[0]"
    value = "--kubelet-insecure-tls"
  }

  depends_on = [kind_cluster.this]
}

resource "kubernetes_config_map" "kong_declarative" {
  metadata {
    name      = "kong-declarative"
    namespace = kubernetes_namespace.kong.metadata[0].name
  }

  data = {
    "kong.yml" = templatefile("${path.module}/kong.yml.tftpl", {
      jwt_secret  = var.jwt_secret
      jwt_issuer  = var.jwt_issuer
      app_namespace = var.app_namespace
    })
  }
}

resource "kubernetes_deployment" "kong" {
  metadata {
    name      = "kong"
    namespace = kubernetes_namespace.kong.metadata[0].name
    labels = {
      app = "kong"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "kong"
      }
    }

    template {
      metadata {
        labels = {
          app = "kong"
        }
      }

      spec {
        container {
          name  = "kong"
          image = "kong:3.8"

          port {
            name           = "proxy"
            container_port = 8000
          }

          port {
            name           = "admin"
            container_port = 8001
          }

          env {
            name  = "KONG_DATABASE"
            value = "off"
          }

          env {
            name  = "KONG_DECLARATIVE_CONFIG"
            value = "/kong/declarative/kong.yml"
          }

          env {
            name  = "KONG_PROXY_LISTEN"
            value = "0.0.0.0:8000"
          }

          env {
            name  = "KONG_ADMIN_LISTEN"
            value = "0.0.0.0:8001"
          }

          env {
            name  = "KONG_NGINX_DAEMON"
            value = "off"
          }

          volume_mount {
            name       = "kong-declarative"
            mount_path = "/kong/declarative"
            read_only  = true
          }

          readiness_probe {
            http_get {
              path = "/status"
              port = 8001
            }
            initial_delay_seconds = 10
            period_seconds        = 10
          }

          resources {
            requests = {
              cpu    = "100m"
              memory = "256Mi"
            }
            limits = {
              cpu    = "500m"
              memory = "512Mi"
            }
          }
        }

        volume {
          name = "kong-declarative"
          config_map {
            name = kubernetes_config_map.kong_declarative.metadata[0].name
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "kong_proxy" {
  metadata {
    name      = "kong-proxy"
    namespace = kubernetes_namespace.kong.metadata[0].name
  }

  spec {
    selector = {
      app = "kong"
    }

    port {
      name        = "proxy"
      port        = 8000
      target_port = 8000
      node_port   = 30080
    }

    type = "NodePort"
  }
}

resource "kubernetes_service" "kong_admin" {
  metadata {
    name      = "kong-admin"
    namespace = kubernetes_namespace.kong.metadata[0].name
  }

  spec {
    selector = {
      app = "kong"
    }

    port {
      name        = "admin"
      port        = 8001
      target_port = 8001
      node_port   = 30081
    }

    type = "NodePort"
  }
}

resource "kubernetes_deployment" "jaeger" {
  metadata {
    name      = "jaeger"
    namespace = kubernetes_namespace.observability.metadata[0].name
    labels = {
      app = "jaeger"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "jaeger"
      }
    }

    template {
      metadata {
        labels = {
          app = "jaeger"
        }
      }

      spec {
        container {
          name  = "jaeger"
          image = "jaegertracing/all-in-one:1.62"

          env {
            name  = "COLLECTOR_ZIPKIN_HOST_PORT"
            value = ":9411"
          }

          port {
            name           = "ui"
            container_port = 16686
          }

          port {
            name           = "zipkin"
            container_port = 9411
          }

          port {
            name           = "otlp-grpc"
            container_port = 4317
          }

          resources {
            requests = {
              cpu    = "50m"
              memory = "128Mi"
            }
            limits = {
              cpu    = "300m"
              memory = "256Mi"
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "jaeger" {
  metadata {
    name      = "jaeger"
    namespace = kubernetes_namespace.observability.metadata[0].name
  }

  spec {
    selector = {
      app = "jaeger"
    }

    port {
      name        = "ui"
      port        = 16686
      target_port = 16686
      node_port   = 31686
    }

    port {
      name        = "zipkin"
      port        = 9411
      target_port = 9411
    }

    type = "NodePort"
  }
}

resource "kubernetes_deployment" "mailhog" {
  metadata {
    name      = "mailhog"
    namespace = kubernetes_namespace.oficina.metadata[0].name
    labels = {
      app = "mailhog"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "mailhog"
      }
    }

    template {
      metadata {
        labels = {
          app = "mailhog"
        }
      }

      spec {
        container {
          name  = "mailhog"
          image = "mailhog/mailhog:v1.0.1"

          port {
            name           = "smtp"
            container_port = 1025
          }

          port {
            name           = "ui"
            container_port = 8025
          }

          resources {
            requests = {
              cpu    = "25m"
              memory = "32Mi"
            }
            limits = {
              cpu    = "100m"
              memory = "64Mi"
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "mailhog" {
  metadata {
    name      = "mailhog"
    namespace = kubernetes_namespace.oficina.metadata[0].name
  }

  spec {
    selector = {
      app = "mailhog"
    }

    port {
      name        = "smtp"
      port        = 1025
      target_port = 1025
    }

    port {
      name        = "ui"
      port        = 8025
      target_port = 8025
      node_port   = 30025
    }

    type = "NodePort"
  }
}

resource "helm_release" "datadog" {
  count = var.datadog_api_key == "" ? 0 : 1

  name       = "datadog"
  repository = "https://helm.datadoghq.com"
  chart      = "datadog"
  namespace  = kubernetes_namespace.observability.metadata[0].name

  set_sensitive {
    name  = "datadog.apiKey"
    value = var.datadog_api_key
  }

  set {
    name  = "datadog.site"
    value = "datadoghq.com"
  }

  set {
    name  = "datadog.logs.enabled"
    value = "true"
  }

  set {
    name  = "datadog.apm.portEnabled"
    value = "true"
  }

  set {
    name  = "clusterAgent.enabled"
    value = "true"
  }

  depends_on = [kubernetes_namespace.observability]
}
''',
    )
    write(
        K8S,
        "outputs.tf",
        """output "cluster_name" {
  value = kind_cluster.this.name
}

output "kubeconfig_path" {
  value = kind_cluster.this.kubeconfig_path
}

output "app_namespace" {
  value = kubernetes_namespace.oficina.metadata[0].name
}

output "gateway_url" {
  value = "http://localhost:${var.kong_proxy_host_port}"
}

output "kong_admin_url" {
  value = "http://localhost:${var.kong_admin_host_port}"
}

output "jaeger_url" {
  value = "http://localhost:${var.jaeger_host_port}"
}

output "mailhog_url" {
  value = "http://localhost:${var.mailhog_host_port}"
}
""",
    )
    write(
        K8S,
        "terraform.tfvars.example",
        """cluster_name          = "oficina-cluster"
environment           = "homolog"
jwt_issuer            = "oficina-auth"
kong_proxy_host_port  = 8000
kong_admin_host_port  = 8001
jaeger_host_port      = 16686
mailhog_host_port     = 8025
datadog_api_key       = ""
""",
    )
    write(
        K8S,
        "datadog/dashboard-oficina.json",
        """{
  "title": "Oficina - Operational dashboard",
  "description": "Daily work-order volume, average time per status and integration errors.",
  "widgets": [
    {
      "definition": {
        "type": "timeseries",
        "title": "Work orders created per day",
        "requests": [{ "q": "sum:oficina.os.created{*}.as_count()" }]
      }
    },
    {
      "definition": {
        "type": "query_value",
        "title": "Average minutes in DIAGNOSTICO",
        "requests": [{ "q": "avg:oficina.os.status.duration{status:em_diagnostico}" }]
      }
    },
    {
      "definition": {
        "type": "query_value",
        "title": "Average minutes in EXECUCAO",
        "requests": [{ "q": "avg:oficina.os.status.duration{status:em_execucao}" }]
      }
    },
    {
      "definition": {
        "type": "query_value",
        "title": "Average minutes in FINALIZADA",
        "requests": [{ "q": "avg:oficina.os.status.duration{status:finalizada}" }]
      }
    },
    {
      "definition": {
        "type": "timeseries",
        "title": "Integration errors",
        "requests": [{ "q": "sum:oficina.integration.errors{*}.as_count()" }]
      }
    },
    {
      "definition": {
        "type": "timeseries",
        "title": "API latency p95",
        "requests": [{ "q": "p95:trace.servlet.request{service:oficina-api}" }]
      }
    }
  ]
}
""",
    )
    write(
        K8S,
        ".github/workflows/ci-cd.yml",
        """name: CI/CD

on:
  pull_request:
    branches: [main, homolog]
  push:
    branches: [main, homolog]

permissions:
  contents: read

jobs:
  validate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: hashicorp/setup-terraform@v3
        with:
          terraform_version: 1.9.8
      - run: terraform fmt -check -recursive
      - run: terraform init -backend=false
      - run: terraform validate

  deploy:
    needs: validate
    if: github.event_name == 'push'
    runs-on: ubuntu-latest
    env:
      TF_VAR_environment: ${{ github.ref_name == 'main' && 'production' || 'homolog' }}
      TF_VAR_jwt_secret: ${{ secrets.JWT_SECRET || 'localDevelopmentSecretChangeMeInProductionEnvironments123456' }}
    steps:
      - uses: actions/checkout@v4
      - uses: hashicorp/setup-terraform@v3
        with:
          terraform_version: 1.9.8
      - name: Terraform apply
        run: |
          terraform init -backend=false
          terraform apply -auto-approve
      - name: Show cluster
        if: always()
        run: |
          terraform output
          kubectl get ns
          kubectl -n kong get all
          kubectl -n observability get all
      - name: Terraform destroy
        if: always()
        run: terraform destroy -auto-approve
""",
    )
    write(
        K8S,
        "README.md",
        """# Oficina — Infrastructure (Kubernetes)

Terraform that provisions the local Kubernetes cluster (kind), Kong API Gateway,
metrics-server (HPA), Jaeger, MailHog and an optional Datadog agent.

## Purpose

Platform layer: ingress, routing, JWT verification at the edge, observability and scale.

## Technologies

kind, Terraform, Kong 3.8 (DB-less), metrics-server, Jaeger, MailHog, Datadog (optional).

## Architecture

```mermaid
flowchart TB
  Client --> Kong[Kong :8000]
  Kong -->|POST /auth/cpf| Auth[auth-lambda]
  Kong -->|JWT /api/*| API[oficina-api]
  Kong -->|/notify| Notify[notify-lambda]
  API --> Jaeger
  Kong --> Prometheus[Kong prometheus plugin]
  subgraph cluster [kind oficina-cluster]
    Kong
    Auth
    Notify
    API
    HPA[HPA oficina-api]
    Jaeger
    MailHog
  end
```

## Deploy

```bash
cp terraform.tfvars.example terraform.tfvars
terraform init
terraform apply
```

| URL | Service |
|---|---|
| http://localhost:8000 | Kong proxy (public entry) |
| http://localhost:8001 | Kong admin |
| http://localhost:16686 | Jaeger UI |
| http://localhost:8025 | MailHog UI |

Then apply the database repo, load images and apply API / Lambda manifests.

## CI/CD

PR: fmt + validate. Push to `homolog`/`main`: apply on ephemeral kind, then destroy.

Protect `main` with required pull requests.

## Datadog

Set `datadog_api_key` (or `TF_VAR_datadog_api_key`) to deploy the official agent.
Import `datadog/dashboard-oficina.json` in the Datadog UI. Without a key, use Jaeger
plus Spring `/actuator/prometheus` and `/api/monitoramento/dashboard`.

## Related repositories

- https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-infra-db
- https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-auth-lambda
- https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-1
""",
    )


def write_lambda() -> None:
    write(
        LAMBDA,
        ".gitignore",
        """__pycache__/
.pytest_cache/
.venv/
*.pyc
.env
.idea/
.vscode/
dist/
*.egg-info/
""",
    )
    write(
        LAMBDA,
        "requirements.txt",
        """fastapi==0.115.6
uvicorn[standard]==0.34.0
psycopg[binary]==3.2.4
PyJWT==2.10.1
pydantic==2.10.4
pydantic-settings==2.7.0
""",
    )
    write(
        LAMBDA,
        "requirements-dev.txt",
        """-r requirements.txt
pytest==8.3.4
httpx==0.28.1
""",
    )
    write(
        LAMBDA,
        "src/cpf_auth/__init__.py",
        "",
    )
    write(
        LAMBDA,
        "src/cpf_auth/validator.py",
        '''def normalize(value: str | None) -> str:
    if not value:
        return ""
    return "".join(ch for ch in value if ch.isdigit())


def is_valid_cpf(value: str | None) -> bool:
    cpf = normalize(value)
    if len(cpf) != 11 or len(set(cpf)) == 1:
        return False
    return _cpf_digit(cpf, 10) == int(cpf[9]) and _cpf_digit(cpf, 11) == int(cpf[10])


def _cpf_digit(cpf: str, weight_start: int) -> int:
    total = sum(int(cpf[i]) * (weight_start - i) for i in range(weight_start - 1))
    remainder = total % 11
    return 0 if remainder < 2 else 11 - remainder
''',
    )
    write(
        LAMBDA,
        "src/cpf_auth/repository.py",
        '''import os

import psycopg

from cpf_auth.validator import normalize


def _dsn() -> str:
    host = os.getenv("POSTGRES_HOST", "localhost")
    port = os.getenv("POSTGRES_PORT", "5432")
    name = os.getenv("POSTGRES_DB", "oficina_mec_db")
    user = os.getenv("POSTGRES_USER", "admin")
    password = os.getenv("POSTGRES_PASSWORD", "admin")
    return f"host={host} port={port} dbname={name} user={user} password={password}"


def find_client_by_cpf(cpf: str) -> dict | None:
    digits = normalize(cpf)
    sql = """
        SELECT id::text, nome, cpf_cnpj, COALESCE(status, 'ATIVO') AS status
        FROM tb_cliente
        WHERE regexp_replace(cpf_cnpj, '[^0-9]', '', 'g') = %s
        LIMIT 1
    """
    with psycopg.connect(_dsn()) as conn:
        with conn.cursor() as cur:
            cur.execute(sql, (digits,))
            row = cur.fetchone()
            if row is None:
                return None
            return {"id": row[0], "nome": row[1], "cpf": normalize(row[2]), "status": row[3]}
''',
    )
    write(
        LAMBDA,
        "src/cpf_auth/tokens.py",
        '''import os
from datetime import datetime, timedelta, timezone

import jwt

ISSUER = os.getenv("JWT_ISSUER", "oficina-auth")
ALGORITHM = "HS512"


def _secret() -> str:
    secret = os.getenv("JWT_SECRET")
    if not secret:
        raise RuntimeError("JWT_SECRET is required")
    return secret


def issue_access_token(client: dict) -> str:
    now = datetime.now(timezone.utc)
    expires_minutes = int(os.getenv("JWT_ACCESS_EXPIRATION_MINUTES", "60"))
    payload = {
        "sub": client["cpf"],
        "userId": client["id"],
        "profile": "CLIENTE",
        "tokenType": "ACCESS",
        "iss": ISSUER,
        "cpf": client["cpf"],
        "clientStatus": client["status"],
        "iat": now,
        "exp": now + timedelta(minutes=expires_minutes),
    }
    return jwt.encode(payload, _secret(), algorithm=ALGORITHM)
''',
    )
    write(
        LAMBDA,
        "src/cpf_auth/handler.py",
        '''import json

from cpf_auth.repository import find_client_by_cpf
from cpf_auth.tokens import issue_access_token
from cpf_auth.validator import is_valid_cpf, normalize


def _body(event: dict) -> dict:
    raw = event.get("body") or "{}"
    if event.get("isBase64Encoded"):
        import base64

        raw = base64.b64decode(raw).decode("utf-8")
    if isinstance(raw, dict):
        return raw
    return json.loads(raw or "{}")


def _response(status: int, payload: dict) -> dict:
    return {
        "statusCode": status,
        "headers": {"Content-Type": "application/json"},
        "body": json.dumps(payload),
    }


def handler(event, _context):
    try:
        body = _body(event)
    except json.JSONDecodeError:
        return _response(400, {"message": "Invalid JSON body"})

    cpf = normalize(body.get("cpf") or body.get("cpfCnpj"))
    if not is_valid_cpf(cpf):
        return _response(400, {"message": "Invalid CPF"})

    client = find_client_by_cpf(cpf)
    if client is None:
        return _response(404, {"message": "Client not found"})
    if client["status"] != "ATIVO":
        return _response(403, {"message": "Client is not active", "status": client["status"]})

    token = issue_access_token(client)
    return _response(
        200,
        {
            "accessToken": token,
            "tokenType": "Bearer",
            "profile": "CLIENTE",
            "cpf": client["cpf"],
            "clientId": client["id"],
            "name": client["nome"],
        },
    )
''',
    )
    write(
        LAMBDA,
        "src/cpf_auth/http_app.py",
        '''from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

from cpf_auth.handler import handler

app = FastAPI(title="Oficina CPF Auth Function", version="1.0.0")


@app.get("/health")
def health():
    return {"status": "UP"}


@app.post("/auth/cpf")
async def authenticate(request: Request):
    body = await request.body()
    event = {"body": body.decode("utf-8")}
    result = handler(event, None)
    return JSONResponse(status_code=result["statusCode"], content=__import__("json").loads(result["body"]))
''',
    )
    write(
        LAMBDA,
        "src/notify/__init__.py",
        "",
    )
    write(
        LAMBDA,
        "src/notify/handler.py",
        '''import json
import logging
import os
import smtplib
from email.message import EmailMessage

LOGGER = logging.getLogger("notify-lambda")
logging.basicConfig(level=logging.INFO)


def _body(event: dict) -> dict:
    raw = event.get("body") or "{}"
    if isinstance(raw, dict):
        return raw
    return json.loads(raw or "{}")


def _response(status: int, payload: dict) -> dict:
    return {
        "statusCode": status,
        "headers": {"Content-Type": "application/json"},
        "body": json.dumps(payload),
    }


def handler(event, _context):
    try:
        payload = _body(event)
    except json.JSONDecodeError:
        return _response(400, {"message": "Invalid JSON body"})

    order_id = payload.get("orderId")
    status = payload.get("status")
    cpf = payload.get("cpfCnpj")
    failed = bool(payload.get("failed"))

    log = {
        "event": "work_order_notification",
        "orderId": order_id,
        "status": status,
        "cpfCnpj": cpf,
        "failed": failed,
        "correlationId": payload.get("correlationId"),
    }
    LOGGER.info(json.dumps(log))

    if failed:
        LOGGER.error(json.dumps({**log, "alert": "work_order_processing_failure"}))

    _send_mail(payload)
    return _response(202, {"accepted": True, "orderId": order_id})


def _send_mail(payload: dict) -> None:
    host = os.getenv("MAIL_HOST", "localhost")
    port = int(os.getenv("MAIL_PORT", "1025"))
    sender = os.getenv("MAIL_FROM", "oficina@oficina.com")
    to = os.getenv("MAIL_TO", "notificacoes@oficina.com")
    message = EmailMessage()
    message["From"] = sender
    message["To"] = to
    message["Subject"] = f"Work order {payload.get('orderId')} -> {payload.get('status')}"
    message.set_content(json.dumps(payload, indent=2, default=str))
    try:
        with smtplib.SMTP(host, port, timeout=5) as smtp:
            smtp.send_message(message)
    except OSError as exc:
        LOGGER.warning(json.dumps({"event": "smtp_failure", "reason": str(exc)}))
''',
    )
    write(
        LAMBDA,
        "src/notify/http_app.py",
        '''import json

from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

from notify.handler import handler

app = FastAPI(title="Oficina Notify Function", version="1.0.0")


@app.get("/health")
def health():
    return {"status": "UP"}


@app.post("/notify")
async def notify(request: Request):
    body = await request.body()
    result = handler({"body": body.decode("utf-8")}, None)
    return JSONResponse(status_code=result["statusCode"], content=json.loads(result["body"]))
''',
    )
    write(
        LAMBDA,
        "tests/test_validator.py",
        '''from cpf_auth.validator import is_valid_cpf, normalize


def test_normalize_strips_mask():
    assert normalize("529.982.247-25") == "52998224725"


def test_valid_cpf():
    assert is_valid_cpf("529.982.247-25")
    assert is_valid_cpf("39053344705")


def test_invalid_cpf():
    assert not is_valid_cpf("111.111.111-11")
    assert not is_valid_cpf("12345678900")
    assert not is_valid_cpf("")
''',
    )
    write(
        LAMBDA,
        "tests/test_handler.py",
        '''import json
from unittest.mock import patch

from cpf_auth.handler import handler


def test_invalid_cpf_returns_400():
    result = handler({"body": json.dumps({"cpf": "111.111.111-11"})}, None)
    assert result["statusCode"] == 400


@patch("cpf_auth.handler.find_client_by_cpf", return_value=None)
def test_unknown_client_returns_404(_mock):
    result = handler({"body": json.dumps({"cpf": "52998224725"})}, None)
    assert result["statusCode"] == 404


@patch(
    "cpf_auth.handler.find_client_by_cpf",
    return_value={"id": "id-1", "nome": "Joao", "cpf": "52998224725", "status": "INATIVO"},
)
def test_inactive_client_returns_403(_mock):
    result = handler({"body": json.dumps({"cpf": "52998224725"})}, None)
    assert result["statusCode"] == 403


@patch(
    "cpf_auth.handler.find_client_by_cpf",
    return_value={"id": "id-1", "nome": "Joao", "cpf": "52998224725", "status": "ATIVO"},
)
@patch("cpf_auth.handler.issue_access_token", return_value="jwt-token")
def test_active_client_returns_token(_token, _repo):
    result = handler({"body": json.dumps({"cpf": "529.982.247-25"})}, None)
    body = json.loads(result["body"])
    assert result["statusCode"] == 200
    assert body["accessToken"] == "jwt-token"
    assert body["profile"] == "CLIENTE"
''',
    )
    write(
        LAMBDA,
        "tests/test_notify.py",
        '''import json
from unittest.mock import patch

from notify.handler import handler


@patch("notify.handler._send_mail")
def test_notify_accepts_payload(send_mail):
    payload = {"orderId": "abc", "status": "EM_EXECUCAO", "cpfCnpj": "52998224725"}
    result = handler({"body": json.dumps(payload)}, None)
    assert result["statusCode"] == 202
    send_mail.assert_called_once()


@patch("notify.handler._send_mail")
def test_notify_flags_processing_failure(send_mail):
    payload = {"orderId": "abc", "status": "RECEBIDA", "failed": True}
    result = handler({"body": json.dumps(payload)}, None)
    assert result["statusCode"] == 202
    send_mail.assert_called_once()
''',
    )
    write(
        LAMBDA,
        "Dockerfile.auth",
        """FROM python:3.12-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY src ./src
ENV PYTHONPATH=/app/src
EXPOSE 8080
CMD ["uvicorn", "cpf_auth.http_app:app", "--host", "0.0.0.0", "--port", "8080"]
""",
    )
    write(
        LAMBDA,
        "Dockerfile.notify",
        """FROM python:3.12-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY src ./src
ENV PYTHONPATH=/app/src
EXPOSE 8080
CMD ["uvicorn", "notify.http_app:app", "--host", "0.0.0.0", "--port", "8080"]
""",
    )
    write(
        LAMBDA,
        "k8s/auth-lambda.yaml",
        """apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-lambda
  namespace: oficina
  labels:
    app: auth-lambda
spec:
  replicas: 1
  selector:
    matchLabels:
      app: auth-lambda
  template:
    metadata:
      labels:
        app: auth-lambda
    spec:
      containers:
        - name: auth-lambda
          image: oficina-auth-lambda:latest
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
          env:
            - name: POSTGRES_HOST
              value: postgres.oficina-db.svc.cluster.local
            - name: POSTGRES_PORT
              value: "5432"
            - name: POSTGRES_DB
              valueFrom:
                secretKeyRef:
                  name: postgres-credentials
                  key: POSTGRES_DB
                  optional: true
            - name: POSTGRES_USER
              valueFrom:
                secretKeyRef:
                  name: oficina-secret
                  key: POSTGRES_USER
            - name: POSTGRES_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: oficina-secret
                  key: POSTGRES_PASSWORD
            - name: JWT_SECRET
              valueFrom:
                secretKeyRef:
                  name: oficina-secret
                  key: JWT_SECRET
            - name: JWT_ISSUER
              value: oficina-auth
          readinessProbe:
            httpGet:
              path: /health
              port: 8080
            initialDelaySeconds: 5
            periodSeconds: 10
          resources:
            requests:
              cpu: 50m
              memory: 64Mi
            limits:
              cpu: 200m
              memory: 128Mi
---
apiVersion: v1
kind: Service
metadata:
  name: auth-lambda
  namespace: oficina
spec:
  selector:
    app: auth-lambda
  ports:
    - port: 8080
      targetPort: 8080
""",
    )
    write(
        LAMBDA,
        "k8s/notify-lambda.yaml",
        """apiVersion: apps/v1
kind: Deployment
metadata:
  name: notify-lambda
  namespace: oficina
  labels:
    app: notify-lambda
spec:
  replicas: 1
  selector:
    matchLabels:
      app: notify-lambda
  template:
    metadata:
      labels:
        app: notify-lambda
    spec:
      containers:
        - name: notify-lambda
          image: oficina-notify-lambda:latest
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
          env:
            - name: MAIL_HOST
              value: mailhog
            - name: MAIL_PORT
              value: "1025"
          readinessProbe:
            httpGet:
              path: /health
              port: 8080
          resources:
            requests:
              cpu: 25m
              memory: 64Mi
            limits:
              cpu: 150m
              memory: 128Mi
---
apiVersion: v1
kind: Service
metadata:
  name: notify-lambda
  namespace: oficina
spec:
  selector:
    app: notify-lambda
  ports:
    - port: 8080
      targetPort: 8080
""",
    )
    write(
        LAMBDA,
        "pytest.ini",
        """[pytest]
pythonpath = src
""",
    )
    write(
        LAMBDA,
        ".github/workflows/ci-cd.yml",
        """name: CI/CD

on:
  pull_request:
    branches: [main, homolog]
  push:
    branches: [main, homolog]

permissions:
  contents: read

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-python@v5
        with:
          python-version: "3.12"
      - run: pip install -r requirements-dev.txt
      - run: pytest -q

  image:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: docker/setup-buildx-action@v3
      - name: Build auth image
        run: docker build -f Dockerfile.auth -t oficina-auth-lambda:latest .
      - name: Build notify image
        run: docker build -f Dockerfile.notify -t oficina-notify-lambda:latest .

  deploy:
    needs: image
    if: github.event_name == 'push'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: helm/kind-action@v1
        with:
          cluster_name: oficina-cluster
          install_only: true
      - name: Create kind cluster and load images
        run: |
          kind create cluster --name oficina-cluster
          docker build -f Dockerfile.auth -t oficina-auth-lambda:latest .
          docker build -f Dockerfile.notify -t oficina-notify-lambda:latest .
          kind load docker-image oficina-auth-lambda:latest --name oficina-cluster
          kind load docker-image oficina-notify-lambda:latest --name oficina-cluster
          kubectl create namespace oficina --dry-run=client -o yaml | kubectl apply -f -
          kubectl -n oficina create secret generic oficina-secret \\
            --from-literal=POSTGRES_USER=admin \\
            --from-literal=POSTGRES_PASSWORD=admin \\
            --from-literal=JWT_SECRET=localDevelopmentSecretChangeMeInProductionEnvironments123456 \\
            --dry-run=client -o yaml | kubectl apply -f -
          kubectl apply -f k8s/auth-lambda.yaml
          kubectl apply -f k8s/notify-lambda.yaml
          kubectl -n oficina rollout status deployment/auth-lambda --timeout=180s
          kubectl -n oficina rollout status deployment/notify-lambda --timeout=180s
""",
    )
    write(
        LAMBDA,
        "README.md",
        """# Oficina — Serverless Functions (Auth + Notifications)

Python functions with an AWS Lambda handler contract, executed locally as containers
behind Kong. The same `handler(event, context)` can be deployed to AWS Lambda later.

## Purpose

1. Authenticate a customer by CPF, check existence/status, issue a JWT
2. Receive work-order events and send email (MailHog locally, SES in the cloud)

## Technologies

Python 3.12, FastAPI, PyJWT (HS512), psycopg, pytest, Docker, GitHub Actions.

## Architecture

```mermaid
sequenceDiagram
  participant C as Customer
  participant K as Kong
  participant L as auth-lambda
  participant DB as PostgreSQL
  C->>K: POST /auth/cpf {cpf}
  K->>L: proxy
  L->>L: validate CPF digits
  L->>DB: select client by CPF
  alt active
    L-->>C: 200 JWT (profile=CLIENTE, iss=oficina-auth)
  else inactive / missing
    L-->>C: 403 / 404
  end
```

## Local run

```bash
python -m venv .venv
.venv/Scripts/activate
pip install -r requirements-dev.txt
pytest -q
docker build -f Dockerfile.auth -t oficina-auth-lambda:latest .
docker build -f Dockerfile.notify -t oficina-notify-lambda:latest .
```

Compose (with the API stack) or Kubernetes:

```bash
kind load docker-image oficina-auth-lambda:latest --name oficina-cluster
kind load docker-image oficina-notify-lambda:latest --name oficina-cluster
kubectl apply -f k8s/
```

## API contract

`POST /auth/cpf`

```json
{ "cpf": "529.982.247-25" }
```

Successful response: `accessToken`, `profile=CLIENTE`, `clientId`.

Use the token on protected `/api/*` routes through Kong (`Authorization: Bearer`).

Swagger of the main API: http://localhost:8000/swagger-ui.html (via Kong) or
http://localhost:8080/swagger-ui.html (direct).

## CI/CD

PR and push: pytest + Docker build. Push to `homolog`/`main` also loads images into kind
and applies manifests.

Protect `main` with required pull requests.

## Related repositories

- https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-infra-k8s
- https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-infra-db
- https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-1
""",
    )


if __name__ == "__main__":
    write_db()
    print("all repositories generated")
