# Infraestrutura (Terraform + kind)

Provisiona um cluster Kubernetes local com [kind](https://kind.sigs.k8s.io/) e os recursos de base
da aplicação usando Terraform.

## Recursos criados

| Recurso | Tipo | Descrição |
|---|---|---|
| `kind_cluster.this` | Cluster kind | Cluster Kubernetes local de 1 nó, com mapeamento da porta NodePort `30080` para o host. |
| `kubernetes_namespace.oficina` | Namespace | Namespace `oficina` onde a aplicação é implantada. |
| `helm_release.metrics_server` | Helm release | metrics-server (necessário para o HorizontalPodAutoscaler). Usa `--kubelet-insecure-tls` por ser kind. |
| `kubernetes_secret.db` | Secret | Credenciais do banco (`POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB`). |
| `kubernetes_persistent_volume_claim.db` | PVC | Volume persistente do PostgreSQL (1Gi). |
| `kubernetes_deployment.db` | Deployment | Banco de dados PostgreSQL 15. |
| `kubernetes_service.db` | Service | Service `postgres` (ClusterIP) na porta 5432. |

> O **banco de dados** é provisionado pelo Terraform. A **aplicação**, ConfigMap, Service e HPA
> ficam nos manifestos de `../k8s` e são aplicados com `kubectl` (ou pelo pipeline de CI/CD).

## Pré-requisitos

- [Docker](https://www.docker.com/)
- [Terraform >= 1.5](https://developer.hashicorp.com/terraform/downloads)
- [kubectl](https://kubernetes.io/docs/tasks/tools/)
- [kind](https://kind.sigs.k8s.io/docs/user/quick-start/#installation)

## Como aplicar

```bash
cd infra
terraform init
terraform apply -auto-approve
```

Ao final, o Terraform exibe a URL local da aplicação e o caminho do kubeconfig.

### Implantar a aplicação

```bash
# 1. Build da imagem (na raiz do projeto)
docker build -t oficina-api:latest .

# 2. Carregar a imagem no cluster kind
kind load docker-image oficina-api:latest --name oficina-cluster

# 3. Aplicar os manifestos restantes (app, configmap, secret, hpa, mailhog)
kubectl apply -f ../k8s/01-configmap.yaml
kubectl apply -f ../k8s/02-secret.yaml
kubectl apply -f ../k8s/04-mailhog.yaml
kubectl apply -f ../k8s/05-app.yaml
kubectl apply -f ../k8s/06-hpa.yaml

# A aplicação fica disponível em http://localhost:30080
kubectl -n oficina get pods
```

> O PostgreSQL e o namespace já são criados pelo Terraform; por isso não é necessário aplicar
> `00-namespace.yaml` nem `03-postgres.yaml` quando se usa esta stack.
>
> O pipeline de CI/CD (`.github/workflows/ci-cd.yml`) segue o mesmo fluxo: `terraform apply` em `infra/`
> provisiona cluster, namespace, metrics-server e banco; em seguida aplica os manifestos `01`, `02`, `04`,
> `05` e `06` de `k8s/`.

## Destruir

```bash
terraform destroy -auto-approve
```

## Variáveis principais

| Variável | Default | Descrição |
|---|---|---|
| `cluster_name` | `oficina-cluster` | Nome do cluster kind. |
| `namespace` | `oficina` | Namespace da aplicação. |
| `node_http_port` | `30080` | Porta do host mapeada para o NodePort. |
| `postgres_user` / `postgres_password` / `postgres_db` | `admin` / `admin` / `oficina_mec_db` | Credenciais do banco. |
| `jwt_secret` | (valor de desenvolvimento) | Segredo de assinatura JWT. |
