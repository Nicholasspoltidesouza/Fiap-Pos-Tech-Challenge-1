# Roteiro do Vídeo Demonstrativo — Tech Challenge Fase 2

**Vídeo publicado:** https://youtu.be/bpC1dvo5epg

**Duração alvo:** ~15 minutos  
**Público:** avaliadores SOAT / FIAP  
**Repositório:** compartilhar com o usuário `soat-architecture`

---

## Checklist antes de gravar

- [ ] Docker Desktop em execução
- [ ] Terraform, kind e kubectl instalados e no `PATH`
- [ ] Repositório clonado e na branch `main` atualizada
- [ ] Terminal com fonte legível (zoom 110–125%)
- [ ] Navegador com abas preparadas: Swagger, MailHog, GitHub Actions
- [ ] Desativar notificações do sistema
- [ ] Ter o `accessToken` JWT copiado após login (facilita as chamadas)

**Credenciais padrão**

| Item | Valor |
|------|-------|
| Admin | `admin@oficina.com` / `admin123` |
| Swagger (Docker) | http://localhost:8080/swagger-ui.html |
| MailHog (Docker) | http://localhost:8025 |
| API no Kubernetes | http://localhost:30080/swagger-ui.html |

---

## Estrutura do vídeo (timeline sugerida)

| Bloco | Tempo | O que mostrar |
|-------|-------|---------------|
| 1. Abertura | 0:00 – 1:30 | Problema, objetivo da Fase 2, visão geral da solução |
| 2. Arquitetura e código | 1:30 – 4:00 | Clean Architecture, domínio rico, estrutura de pastas |
| 3. Testes automatizados | 4:00 – 5:30 | `mvn verify`, JaCoCo, Testcontainers |
| 4. Execução local (Docker) | 5:30 – 9:30 | `docker-compose`, Swagger, fluxo completo de OS |
| 5. Infraestrutura (Terraform + K8s) | 9:30 – 12:30 | Provisionamento, deploy, pods rodando |
| 6. CI/CD | 12:30 – 13:30 | Pipeline no GitHub Actions |
| 7. Escalabilidade (HPA) | 13:30 – 14:30 | HPA configurado + simulação de carga |
| 8. Encerramento | 14:30 – 15:00 | Resumo dos entregáveis e link do repositório |

---

## Bloco 1 — Abertura (0:00 – 1:30)

### O que falar

> "Este vídeo apresenta a evolução do sistema de gestão de oficina mecânica para a Fase 2 do Tech Challenge. O foco é qualidade de código, resiliência e escalabilidade, com Clean Architecture, testes automatizados, containerização, Kubernetes, Terraform e pipeline de CI/CD."

**Pontos-chave a mencionar:**

- Evolução da Fase 1 para suportar maior volume e múltiplas unidades
- Stack: Java 25, Spring Boot 4, PostgreSQL, Docker, Kubernetes (kind), Terraform, GitHub Actions
- APIs obrigatórias: abertura de OS, consulta de status, aprovação de orçamento, listagem ordenada, notificação por e-mail

### O que mostrar na tela

- README.md (seção inicial)
- Diagrama de arquitetura no README (seção 10.1) ou `DOCUMENTACAO_ARQUITETURA.md`

---

## Bloco 2 — Arquitetura e código (1:30 – 4:00)

### O que falar

> "A aplicação segue Clean Architecture com separação em domain, application, infrastructure e presentation. As regras de transição de status da Ordem de Serviço ficam no agregado de domínio, não espalhadas nos controllers."

### O que mostrar na tela

1. Estrutura de pastas em `src/main/java/com/postech/challenge/`
2. `domain/model/OrdemServico.java` — máquina de estados
3. `domain/model/StatusOrdemServico.java` — prioridade de listagem
4. `application/gateway/NotificacaoOrdemServicoGateway.java` — port para e-mail
5. `infrastructure/notification/EmailNotificacaoGatewayImpl.java` — adapter
6. Pastas `k8s/`, `infra/`, `.github/workflows/`

**Frase de impacto:**

> "A dependência aponta para dentro: o domínio não conhece Spring, JPA nem Kubernetes."

---

## Bloco 3 — Testes automatizados (4:00 – 5:30)

### Comandos (Windows PowerShell)

```powershell
# Na raiz do projeto
.\mvnw.cmd verify
```

```powershell
# Abrir relatório de cobertura JaCoCo
start target\site\jacoco\index.html
```

### O que falar

> "O pipeline e o desenvolvimento local executam testes unitários e de integração com Testcontainers. O JaCoCo valida a cobertura mínima configurada no pom.xml."

### O que mostrar na tela

- Saída do `verify` com `BUILD SUCCESS`
- Relatório JaCoCo no navegador
- (Opcional) `src/test/.../OrdemServicoServiceUsecaseIntegrationTest.java` — fluxo completo de OS

---

## Bloco 4 — Execução local com Docker + APIs (5:30 – 9:30)

### Comandos — subir ambiente

```powershell
# Na raiz do projeto
copy .env.example .env
docker-compose up --build
```

Aguarde até ver logs de `Started ChallengeApplication`.

### URLs para abrir

| Serviço | URL |
|---------|-----|
| Swagger | http://localhost:8080/swagger-ui.html |
| MailHog | http://localhost:8025 |
| Health | http://localhost:8080/actuator/health |

---

### Demo 4.1 — Autenticação

**No Swagger:** `POST /api/auth/login`

```json
{
  "email": "admin@oficina.com",
  "password": "admin123"
}
```

Copie o `accessToken` → clique em **Authorize** → cole `Bearer <token>`.

---

### Demo 4.2 — Abertura de Ordem de Serviço

**Opção recomendada (dados do seed):** `POST /api/ordens-servico`

```json
{
  "clienteId": "11111111-1111-1111-1111-111111111111",
  "veiculoId": "22222222-2222-2222-2222-222222222221",
  "servicosSolicitadosIds": [
    "33333333-3333-3333-3333-333333333331"
  ],
  "insumosSolicitadosIds": [
    "66666666-6666-6666-6666-666666666662"
  ],
  "pecasSolicitadasIds": []
}
```

**Alternativa (por CPF/CNPJ do cliente):** `POST /api/ordens-servico/cliente`

```json
{
  "clienteCpfCnpj": "52998224725",
  "veiculoPlaca": "NOVA1234",
  "veiculoMarca": "Fiat",
  "veiculoModelo": "Argo",
  "veiculoAno": 2023,
  "servicosSolicitadosIds": [
    "33333333-3333-3333-3333-333333333331"
  ],
  "insumosSolicitadosIds": [],
  "pecasSolicitadasIds": []
}
```

> **Dica:** antes de gravar, crie o cliente com CPF `52998224725` via `POST /api/clientes` se ainda não existir, ou use a opção com UUIDs acima (mais previsível com o `data.sql`).

**Atalho para o ciclo de vida:** o seed já cria OS em `RECEBIDA` — use o ID `44444444-4444-4444-4444-444444444441` se quiser pular a criação e ir direto às transições.

**O que destacar:** resposta retorna o `id` (UUID) da OS criada com status `RECEBIDA`.

---

### Demo 4.3 — Ciclo de vida da OS

Substitua `{id}` pelo UUID retornado na criação.

| Ação | Método | Endpoint |
|------|--------|----------|
| Iniciar diagnóstico | PATCH | `/api/ordens-servico/{id}/diagnostico/iniciar` |
| Enviar orçamento | PATCH | `/api/ordens-servico/{id}/orcamento/enviar` |
| Aprovar orçamento | PATCH | `/api/ordens-servico/{id}/orcamento/aprovacao` |
| Finalizar | PATCH | `/api/ordens-servico/{id}/finalizar` |
| Entregar | PATCH | `/api/ordens-servico/{id}/entregar` |

**Body da aprovação:**

```json
{
  "aprovado": true
}
```

**O que falar em cada transição:**

> "Cada mudança de status passa pelo agregado `OrdemServico`, que valida se a transição é permitida."

---

### Demo 4.4 — Consulta de status (público, sem JWT)

**Endpoint:** `GET /api/public/ordens-servico/{id}/acompanhamento?cpfCnpj=52998224725`

Use o `{id}` da OS criada no passo anterior (ou `44444444-4444-4444-4444-444444444441` do seed) e o CPF do cliente dono da OS.

**Status possíveis:** `RECEBIDA`, `EM_DIAGNOSTICO`, `AGUARDANDO_APROVACAO`, `EM_EXECUCAO`, `FINALIZADA`, `ENTREGUE`.

---

### Demo 4.5 — Listagem de OS ativas (requisito de ordenação)

**Endpoint:** `GET /api/ordens-servico/ativas`

**O que explicar:**

- Ordenação: **Em Execução → Aguardando Aprovação → Em Diagnóstico → Recebida**
- Dentro de cada status: **mais antigas primeiro**
- **Exclui logicamente** `FINALIZADA` e `ENTREGUE` (o seed em `data.sql` já tem OS em vários status para demonstrar)

---

### Demo 4.6 — Notificação por e-mail

Após qualquer transição de status (ex.: aprovar orçamento), abra o **MailHog** em http://localhost:8025.

> "A atualização de status dispara um e-mail via `EmailNotificacaoGatewayImpl`, usando MailHog no ambiente local."

---

### Parar Docker (após a demo)

```powershell
docker-compose down
```

---

## Bloco 5 — Terraform + Kubernetes (9:30 – 12:30)

### Pré-requisitos (mencionar rapidamente)

```powershell
terraform -version
kubectl version --client
kind version
docker version
```

---

### Passo 1 — Provisionar infraestrutura com Terraform

```powershell
cd infra
terraform init
terraform apply -auto-approve
```

**O que falar enquanto roda:**

> "O Terraform cria o cluster kind local, o namespace `oficina`, o metrics-server para o HPA e o PostgreSQL com Secret e PVC."

**Recursos criados (mostrar `infra/README.md` ou output):**

- Cluster kind (`oficina-cluster`)
- Namespace `oficina`
- metrics-server (Helm)
- PostgreSQL (Deployment + Service + Secret + PVC)

```powershell
terraform output
```

---

### Passo 2 — Configurar kubectl

```powershell
$env:KUBECONFIG = terraform output -raw kubeconfig_path
kubectl cluster-info
kubectl get nodes
```

---

### Passo 3 — Build e deploy da aplicação

```powershell
# Voltar para a raiz do projeto
cd ..

docker build -t oficina-api:latest .

kind load docker-image oficina-api:latest --name oficina-cluster

kubectl apply -f k8s/01-configmap.yaml
kubectl apply -f k8s/02-secret.yaml
kubectl apply -f k8s/04-mailhog.yaml
kubectl apply -f k8s/05-app.yaml
kubectl apply -f k8s/06-hpa.yaml
```

> **Importante:** com Terraform, **não** aplique `00-namespace.yaml` nem `03-postgres.yaml` (já provisionados pelo Terraform).

---

### Passo 4 — Validar deploy

```powershell
kubectl -n oficina get all
kubectl -n oficina rollout status deployment/postgres --timeout=180s
kubectl -n oficina rollout status deployment/oficina-api --timeout=240s
kubectl -n oficina get hpa
```

**Acessar aplicação:**

- API: http://localhost:30080/swagger-ui.html
- MailHog no cluster: `kubectl -n oficina port-forward svc/mailhog 8025:8025` (se quiser mostrar e-mails no K8s)

Repita uma chamada rápida no Swagger (login + listar OS ativas) na porta **30080**.

---

### Limpeza (após gravar o bloco K8s)

```powershell
cd infra
terraform destroy -auto-approve
```

---

## Bloco 6 — CI/CD no GitHub Actions (12:30 – 13:30)

### O que mostrar

1. Arquivo `.github/workflows/ci-cd.yml` no repositório
2. Aba **Actions** no GitHub com um workflow concluído com sucesso

### O que falar

> "A cada push ou PR na branch main, o pipeline executa três estágios: build e testes com `mvn verify`, build da imagem Docker e deploy automatizado com Terraform + Kubernetes."

**Jobs do pipeline:**

| Job | O que faz |
|-----|-----------|
| `build-and-test` | Compila, roda testes (Testcontainers) e publica JaCoCo |
| `docker-image` | Gera a imagem `oficina-api:latest` |
| `deploy-terraform-k8s` | Terraform apply → load image no kind → kubectl apply → valida rollout → destroy |

**Dica de gravação:** se o pipeline demorar ao vivo, grave essa parte mostrando um run já concluído no GitHub e comente os logs dos três jobs.

---

## Bloco 7 — Escalabilidade com HPA (13:30 – 14:30)

### Mostrar configuração

Arquivo `k8s/06-hpa.yaml`:

- Mínimo: 1 réplica | Máximo: 5 réplicas
- Escala por CPU (70%) e memória (80%)

### Comandos

```powershell
kubectl -n oficina get hpa oficina-api-hpa -w
kubectl -n oficina describe hpa oficina-api-hpa
kubectl -n oficina get pods -l app=oficina-api -w
```

### Simular carga (escolha uma opção)

**Opção A — PowerShell (sem instalar nada extra):**

```powershell
# Gera tráfego na API por ~2 minutos (ajuste o tempo se necessário)
1..500 | ForEach-Object -Parallel {
    try {
        Invoke-WebRequest -Uri "http://localhost:30080/actuator/health" -UseBasicParsing | Out-Null
    } catch {}
} -ThrottleLimit 20
```

**Opção B — com hey (se tiver instalado):**

```powershell
hey -z 120s -c 50 http://localhost:30080/actuator/health
```

### O que falar

> "O HorizontalPodAutoscaler monitora CPU e memória via metrics-server — instalado pelo Terraform — e aumenta as réplicas automaticamente sob carga."

**Se o scale-up não aparecer ao vivo:** explique que em cluster kind local o scale pode demorar ou ser limitado por recursos do Docker; mostre o HPA configurado e os `resources.requests/limits` no `k8s/05-app.yaml`.

---

## Bloco 8 — Encerramento (14:30 – 15:00)

### Resumo dos entregáveis (falar olhando para o README)

- Código refatorado com Clean Architecture e domínio rico
- Testes automatizados com cobertura JaCoCo
- Dockerfile e docker-compose
- Manifestos Kubernetes em `/k8s` (Deployment, Service, ConfigMap, Secret, HPA)
- Terraform em `/infra` (cluster + banco + metrics-server)
- Pipeline CI/CD em `.github/workflows/ci-cd.yml`
- Documentação: README, `DOCUMENTACAO_ARQUITETURA.md`, Domain Storytelling, Event Storming
- Collection de APIs: Swagger em `/swagger-ui.html`

### Frase de fechamento sugerida

> "Com isso, a oficina passa a ter uma aplicação escalável, testada e com deploy automatizado — pronta para evoluir para ambientes de produção em cloud."

---

## Referência rápida — todos os comandos

### Ambiente local (Docker)

```powershell
copy .env.example .env
docker-compose up --build
docker-compose down
docker-compose down -v
```

### Testes

```powershell
.\mvnw.cmd test
.\mvnw.cmd verify
start target\site\jacoco\index.html
```

### Terraform + Kubernetes (deploy completo)

```powershell
# 1. Infraestrutura
cd infra
terraform init
terraform apply -auto-approve
$env:KUBECONFIG = terraform output -raw kubeconfig_path
cd ..

# 2. Aplicação
docker build -t oficina-api:latest .
kind load docker-image oficina-api:latest --name oficina-cluster
kubectl apply -f k8s/01-configmap.yaml
kubectl apply -f k8s/02-secret.yaml
kubectl apply -f k8s/04-mailhog.yaml
kubectl apply -f k8s/05-app.yaml
kubectl apply -f k8s/06-hpa.yaml

# 3. Verificação
kubectl -n oficina get all
kubectl -n oficina get hpa
kubectl -n oficina rollout status deployment/oficina-api

# 4. Limpeza
cd infra
terraform destroy -auto-approve
```

### Kubernetes — somente manifestos (sem Terraform)

Use quando quiser um cluster kind criado manualmente:

```powershell
kind create cluster --name oficina-cluster
docker build -t oficina-api:latest .
kind load docker-image oficina-api:latest --name oficina-cluster
kubectl apply -f k8s/
kubectl -n oficina get pods
kind delete cluster --name oficina-cluster
```

---

## Dicas de gravação

1. **Grave em duas partes** se preferir: (A) código + Docker + APIs e (B) infra + CI/CD + HPA — depois edite junto.
2. **Acelere trechos longos** (`terraform apply`, `docker-compose up`, pipeline) na edição com time-lapse.
3. **Mostre o resultado**, não só o comando: Swagger respondendo, e-mail no MailHog, pods `Running`, pipeline verde.
4. **Mencione o repositório** e que está compartilhado com `soat-architecture`.
5. Vídeo publicado em: https://youtu.be/bpC1dvo5epg (link também no `README.md`).

---

## Mapa: requisito do PDF → onde demonstrar

| Requisito da avaliação | Onde mostrar no vídeo |
|------------------------|----------------------|
| Desenho da arquitetura | `docs/DIAGRAMA_ARQUITETURA.md` — seções 1, 4 e 5 |
| Clean Code / Clean Architecture | Bloco 2 — estrutura de pastas e `OrdemServico` |
| Testes automatizados | Bloco 3 — `mvn verify` + JaCoCo |
| Abertura de OS | Bloco 4.2 — `POST /api/ordens-servico/cliente` |
| Consulta de status | Bloco 4.4 — endpoint público de acompanhamento |
| Aprovação de orçamento | Bloco 4.3 — `PATCH .../orcamento/aprovacao` |
| Listagem ordenada | Bloco 4.5 — `GET /api/ordens-servico/ativas` |
| Notificação por e-mail | Bloco 4.6 — MailHog |
| Docker / docker-compose | Bloco 4 |
| Kubernetes (Deploy, Service, ConfigMap, Secret, HPA) | Bloco 5 + arquivos em `k8s/` |
| Terraform | Bloco 5 — `infra/` |
| CI/CD | Bloco 6 — GitHub Actions |
| Escalabilidade automática | Bloco 7 — HPA |
