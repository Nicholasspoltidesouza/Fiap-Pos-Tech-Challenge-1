# Diagrama de Arquitetura — Tech Challenge Fase 2

Documento visual da arquitetura da solução de gestão de oficina mecânica, contemplando **componentes da aplicação**, **infraestrutura provisionada** e **fluxo de deploy**, conforme exigido na entrega da Fase 2.

Documentos complementares:

- `DOCUMENTACAO_ARQUITETURA.md` — detalhamento textual das camadas e decisões
- `docs/DOMAIN_STORYTELLING.md` — narrativa de domínio
- `docs/EVENT_STORMING.md` — eventos, comandos e agregados
- `infra/README.md` — recursos Terraform e comandos de provisionamento

---

## 1) Visão geral da solução

```mermaid
flowchart TB
    subgraph Usuarios
        ATD[Atendente / Mecânico / Admin]
        CLI[Cliente final]
        EXT[Sistema externo de aprovação]
    end

    subgraph Plataforma["Plataforma Oficina Mecânica"]
        API[API REST — Spring Boot 4]
        SWG[Swagger / OpenAPI]
        API --- SWG
    end

    subgraph Dados
        PG[(PostgreSQL 15)]
    end

    subgraph Notificacoes
        MH[MailHog / SMTP]
    end

    ATD -->|JWT| API
    EXT -->|JWT| API
    CLI -->|endpoint público| API
    API --> PG
    API -->|mudança de status| MH

    subgraph Operacao
        DEV[Desenvolvedor]
        GHA[GitHub Actions CI/CD]
        TF[Terraform]
        K8S[Kubernetes kind]
    end

    DEV -->|git push| GHA
    GHA --> TF
    GHA --> K8S
    TF --> K8S
    K8S --> API
    K8S --> PG
    K8S --> MH
```

**Resumo:** API stateless com autenticação JWT, persistência em PostgreSQL e notificações por e-mail. A infraestrutura local é provisionada com Terraform (cluster + banco) e complementada com manifestos Kubernetes; o deploy é automatizado via GitHub Actions.

---

## 2) Arquitetura de software (Clean Architecture)

```mermaid
flowchart TB
    subgraph Presentation["Presentation Layer"]
        CTRL[Controllers REST]
        DOC[OpenAPI Docs]
        EH[Exception Handler]
    end

    subgraph Application["Application Layer"]
        UC[Use Cases]
        DTO[DTOs]
        MAP[Mappers]
        VAL[Validators]
        GW_PORT[Gateway Ports]
        REPO_PORT[Repository Ports]
    end

    subgraph Domain["Domain Layer — núcleo puro"]
        AGG[Agregado OrdemServico]
        ENUM[StatusOrdemServico]
        VO[Value Objects — CpfCnpj, Placa]
        DOM_EX[Domain Exceptions]
    end

    subgraph Infrastructure["Infrastructure Layer"]
        JPA[JPA Entities + Repositories]
        SEC[Security — JWT, Filters]
        MAIL[EmailNotificacaoGatewayImpl]
        CFG[Spring Config / Actuator]
    end

    CTRL --> UC
    UC --> AGG
    UC --> REPO_PORT
    UC --> GW_PORT
    REPO_PORT -.->|implementa| JPA
    GW_PORT -.->|implementa| MAIL
    JPA --> PG[(PostgreSQL)]
    MAIL --> SMTP[MailHog SMTP]
    SEC -.->|cross-cutting| CTRL
    SEC -.->|cross-cutting| UC
```

### Regra de dependência

```mermaid
flowchart LR
    P[Presentation] --> A[Application]
    A --> D[Domain]
    I[Infrastructure] -.->|implementa ports| A
    I --> D
```

| Camada | Pacote | Responsabilidade |
|--------|--------|------------------|
| Presentation | `presentation.api` | Endpoints HTTP, contratos Swagger |
| Application | `application.usecase` | Orquestração de casos de uso |
| Domain | `domain.model` | Regras de negócio e máquina de estados |
| Infrastructure | `infrastructure.*` | JPA, JWT, e-mail, configuração |

---

## 3) Componentes da aplicação

```mermaid
flowchart LR
    subgraph Modulos["Módulos da API"]
        AUTH[Auth — login, register, refresh]
        CLI_MOD[Clientes]
        VEI[Veículos]
        SRV[Serviços]
        PEC[Peças + estoque]
        INS[Insumos + estoque]
        OS[Ordens de Serviço]
        ACOMP[Acompanhamento público]
        MON[Monitoramento — tempo médio]
    end

    subgraph OS_Fluxo["Ciclo de vida da OS"]
        C1[POST abertura]
        C2[PATCH diagnóstico]
        C3[PATCH enviar orçamento]
        C4[PATCH aprovação]
        C5[PATCH finalizar / entregar]
        C6[GET listagem ativas]
        C7[GET acompanhamento]
        C1 --> C2 --> C3 --> C4 --> C5
        C6 --- OS
        C7 --- OS
    end

    OS --> OS_Fluxo
    AUTH -->|JWT Bearer| OS
```

### Endpoints principais (requisitos da Fase 2)

| Requisito | Endpoint | Autenticação |
|-----------|----------|--------------|
| Abertura de OS | `POST /api/ordens-servico` ou `/cliente` | JWT |
| Consulta de status | `GET /api/public/ordens-servico/{id}/acompanhamento` | Pública (CPF/CNPJ) |
| Aprovação de orçamento | `PATCH /api/ordens-servico/{id}/orcamento/aprovacao` | JWT |
| Listagem ordenada | `GET /api/ordens-servico/ativas` | JWT |
| Notificação por e-mail | Disparada nas transições de status | — |

### Máquina de estados — Ordem de Serviço

```mermaid
stateDiagram-v2
    [*] --> RECEBIDA : abertura
    RECEBIDA --> EM_DIAGNOSTICO : iniciarDiagnostico
    EM_DIAGNOSTICO --> AGUARDANDO_APROVACAO : enviarOrcamento
    AGUARDANDO_APROVACAO --> EM_EXECUCAO : aprovar (true)
    AGUARDANDO_APROVACAO --> RECEBIDA : aprovar (false)
    EM_EXECUCAO --> FINALIZADA : finalizar
    FINALIZADA --> ENTREGUE : entregar
    ENTREGUE --> [*]
    FINALIZADA --> [*]
```

> A lógica de transição reside no agregado `OrdemServico` (`domain.model`). Cada transição válida pode disparar e-mail via `NotificacaoOrdemServicoGateway`.

---

## 4) Infraestrutura provisionada

### 4.1 Ambiente local — Docker Compose

```mermaid
flowchart TB
    subgraph Host["Máquina do desenvolvedor"]
        subgraph Compose["docker-compose.yml"]
            APP_C[oficina-api :8080]
            DB_C[postgres :5432]
            MH_C[mailhog :1025 / :8025]
        end
        DEV_U[Desenvolvedor / Avaliador]
    end

    DEV_U -->|HTTP| APP_C
    DEV_U -->|UI e-mails| MH_C
    APP_C --> DB_C
    APP_C -->|SMTP| MH_C
```

| Serviço | Imagem / Build | Porta |
|---------|----------------|-------|
| `app` | `Dockerfile` (multi-stage Maven + JRE) | 8080 |
| `db` | `postgres:15-alpine` | 5432 |
| `mailhog` | `mailhog/mailhog` | 1025 (SMTP), 8025 (UI) |

---

### 4.2 Ambiente Kubernetes — recursos

```mermaid
flowchart TB
    subgraph TF["Terraform — infra/"]
        KIND[kind_cluster.oficina-cluster]
        NS[kubernetes_namespace.oficina]
        MS[helm_release.metrics-server]
        DB_SEC[kubernetes_secret.db]
        DB_PVC[kubernetes_persistent_volume_claim.db]
        DB_DEP[kubernetes_deployment.postgres]
        DB_SVC[kubernetes_service.postgres]
    end

    subgraph K8S["Manifestos — k8s/"]
        CM[01-configmap.yaml]
        SEC[02-secret.yaml]
        MH[04-mailhog.yaml]
        APP[05-app.yaml — Deployment + Service NodePort]
        HPA[06-hpa.yaml]
    end

    subgraph Cluster["Cluster Kubernetes (namespace oficina)"]
        POD_APP[Pod oficina-api]
        POD_DB[Pod postgres]
        POD_MH[Pod mailhog]
        SVC_APP[Service NodePort :30080]
        HPA_OBJ[HPA 1–5 réplicas]
    end

    TF --> Cluster
    K8S --> Cluster
    KIND --> NS
    NS --> DB_DEP
    NS --> POD_APP
    CM --> POD_APP
    SEC --> POD_APP
    HPA --> HPA_OBJ
    HPA_OBJ --> POD_APP
    POD_APP --> POD_DB
    POD_APP --> POD_MH
    SVC_APP --> POD_APP
    MS -.->|métricas CPU/RAM| HPA_OBJ
```

### Inventário de recursos

| Origem | Recurso | Finalidade |
|--------|---------|------------|
| Terraform | Cluster kind | Kubernetes local com NodePort 30080 exposto no host |
| Terraform | Namespace `oficina` | Isolamento dos recursos da aplicação |
| Terraform | metrics-server (Helm) | Métricas para o HorizontalPodAutoscaler |
| Terraform | PostgreSQL (Deploy + SVC + Secret + PVC) | Banco de dados persistente |
| k8s | ConfigMap `oficina-config` | Variáveis não sensíveis (host DB, mail, JPA) |
| k8s | Secret `oficina-secret` | JWT, credenciais do banco |
| k8s | Deployment + Service MailHog | Captura de e-mails em ambiente local |
| k8s | Deployment + Service `oficina-api` | API com probes e resource limits |
| k8s | HorizontalPodAutoscaler | Escala automática por CPU (70%) e memória (80%) |

> Quando o deploy usa Terraform, **não** aplicar `k8s/00-namespace.yaml` nem `k8s/03-postgres.yaml` (já provisionados pelo Terraform).

---

## 5) Fluxo de deploy (CI/CD)

```mermaid
sequenceDiagram
    participant Dev as Desenvolvedor
    participant GH as GitHub
    participant CI as GitHub Actions
    participant MVN as Maven / Testcontainers
    participant DK as Docker
    participant TF as Terraform
    participant K8S as Kubernetes kind
    participant PG as PostgreSQL
    participant API as oficina-api

    Dev->>GH: git push / pull_request (main)
    GH->>CI: dispara workflow ci-cd.yml

    rect rgb(240, 248, 255)
        Note over CI,MVN: Job 1 — build-and-test
        CI->>MVN: ./mvnw verify
        MVN-->>CI: testes + JaCoCo OK
    end

    rect rgb(240, 255, 240)
        Note over CI,DK: Job 2 — docker-image
        CI->>DK: docker build oficina-api:latest
        DK-->>CI: artefato da imagem
    end

    rect rgb(255, 248, 240)
        Note over CI,API: Job 3 — deploy-terraform-k8s
        CI->>TF: terraform init + apply (infra/)
        TF->>K8S: cria cluster kind + namespace + DB + metrics-server
        CI->>K8S: kind load docker-image
        CI->>K8S: kubectl apply (configmap, secret, mailhog, app, hpa)
        K8S->>PG: rollout postgres
        K8S->>API: rollout oficina-api
        API->>PG: conexão JDBC
        CI->>TF: terraform destroy (limpeza)
    end
```

### Pipeline resumido

```mermaid
flowchart LR
    A[Push main] --> B[mvn verify]
    B --> C[Docker build]
    C --> D[Terraform apply]
    D --> E[kind load image]
    E --> F[kubectl apply k8s/]
    F --> G[Rollout status]
    G --> H[Terraform destroy]
```

| Etapa | Ferramenta | Artefato / resultado |
|-------|------------|----------------------|
| Build & testes | Maven, JUnit, Testcontainers, JaCoCo | JAR validado + relatório de cobertura |
| Imagem | Docker multi-stage | `oficina-api:latest` |
| Infraestrutura | Terraform (`infra/`) | Cluster kind, DB, metrics-server |
| Deploy app | kubectl (`k8s/`) | API, MailHog, ConfigMap, Secret, HPA |
| Validação | kubectl rollout | Pods `Running` |
| Limpeza | terraform destroy | Cluster efêmero removido no CI |

---

## 6) Fluxo de uma requisição (exemplo: transição de status)

```mermaid
sequenceDiagram
    participant C as Cliente HTTP
    participant API as OrdemServicoControllerApi
    participant UC as OrdemServicoServiceUsecaseImpl
    participant DOM as OrdemServico (domain)
    participant REPO as OrdemServicoRepository
    participant PG as PostgreSQL
    participant GW as EmailNotificacaoGatewayImpl
    participant MH as MailHog

    C->>API: PATCH /api/ordens-servico/{id}/orcamento/aprovacao
    API->>UC: aprovarOrcamento(id, aprovado)
    UC->>REPO: findById(id)
    REPO->>PG: SELECT
    PG-->>REPO: OrdemServicoEntity
    REPO-->>UC: entity
    UC->>DOM: reconstituir + aprovarOrcamento()
    DOM-->>UC: novo estado validado
    UC->>REPO: save(entity)
    REPO->>PG: UPDATE
    UC->>GW: notificarAtualizacaoStatus()
    GW->>MH: SMTP send
    UC-->>API: OrdemServicoResponseDTO
    API-->>C: 200 OK
```

---

## 7) Segurança e configuração externa

```mermaid
flowchart LR
    subgraph Entrada
        REQ[Requisição HTTP]
    end

    subgraph Security
        JWT_F[JwtAuthenticationFilter]
        EP[JwtAuthenticationEntryPoint]
        RBAC[Roles — ADMIN, ATENDENTE, MECANICO]
    end

    subgraph Config
        ENV[.env / variáveis de ambiente]
        CM_K8S[ConfigMap k8s]
        SEC_K8S[Secret k8s]
    end

    REQ --> JWT_F
    JWT_F -->|token válido| RBAC
    JWT_F -->|token inválido| EP
    ENV --> APP_CFG[Spring Boot]
    CM_K8S --> APP_CFG
    SEC_K8S --> APP_CFG
```

| Segredo / configuração | Onde é definido |
|------------------------|-----------------|
| `JWT_SECRET` | `.env`, Secret k8s, GitHub Secret (CI) |
| Credenciais PostgreSQL | `.env`, Secret k8s, Terraform `kubernetes_secret.db` |
| Host SMTP / MailHog | ConfigMap k8s, `.env` |

---

## 8) Mapa de diretórios da solução

```mermaid
flowchart TB
    ROOT[Repositório raiz]

    ROOT --> SRC[src/ — código Java]
    ROOT --> K8S[k8s/ — manifestos Kubernetes]
    ROOT --> INFRA[infra/ — Terraform IaC]
    ROOT --> GHA[.github/workflows/ — CI/CD]
    ROOT --> DOCS[docs/ — modelagem e diagramas]
    ROOT --> DC[docker-compose.yml + Dockerfile]

    SRC --> DOMAIN[domain/]
    SRC --> APP[application/]
    SRC --> INF[infrastructure/]
    SRC --> PRES[presentation/]
    SRC --> TEST[src/test/ — testes unitários e integração]
```

---

## 9) Escalabilidade (HPA)

```mermaid
flowchart LR
    subgraph Monitoramento
        MS[metrics-server]
        HPA[HorizontalPodAutoscaler]
    end

    subgraph Workload
        DEP[Deployment oficina-api]
        P1[Pod 1]
        P2[Pod 2]
        PN[Pod N — máx. 5]
    end

    MS -->|CPU / memória| HPA
    HPA -->|scale up/down| DEP
    DEP --> P1
    DEP --> P2
    DEP --> PN
```

| Parâmetro HPA | Valor |
|---------------|-------|
| `minReplicas` | 1 |
| `maxReplicas` | 5 |
| CPU target | 70% |
| Memory target | 80% |

Sob carga, o HPA solicita réplicas adicionais ao Deployment; o Service `NodePort` distribui o tráfego entre os pods disponíveis.

---

## 10) Legenda

| Símbolo | Significado |
|---------|-------------|
| Retângulo | Componente ou serviço |
| Cilindro `[( )]` | Banco de dados / persistência |
| Seta sólida | Fluxo de dados ou chamada |
| Seta tracejada | Implementação de interface / cross-cutting |
| Subgrafo | Agrupamento lógico ou físico |

---

*Tech Challenge — Pós-Graduação FIAP · Fase 2 · Gestão de Oficina Mecânica*
