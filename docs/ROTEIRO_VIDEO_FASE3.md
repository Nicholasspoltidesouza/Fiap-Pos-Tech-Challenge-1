# Roteiro do Vídeo — Tech Challenge Fase 3 (fonte: PDF oficial)

**Duração máxima:** 15 minutos  
**Público:** avaliadores SOAT / FIAP  
**Fonte de verdade:** `13SOAT - Fase 3 - Tech Challenge.pdf`  
**Estratégia:** simulação local de nuvem (Kong + functions Lambda-contract + Postgres gerenciado + kind/Terraform + Grafana/Prometheus/Jaeger)

---

## Mapa PDF → o que mostrar no vídeo

| Exigência do PDF (vídeo) | Onde mostrar | Tempo sugerido |
|---|---|---|
| Autenticação com CPF | Kong `POST /auth/cpf` + JWT no Swagger | ~2 min |
| Execução da pipeline CI/CD | GitHub Actions dos 4 repos | ~2 min |
| Deploy automatizado | Run verde em `main`/`homolog` + containers/pods | ~1,5 min |
| Consumo das APIs protegidas | Abrir OS com token CLIENTE via Kong | ~2,5 min |
| Dashboard ao vivo | Grafana + `/api/monitoramento/dashboard` | ~2 min |
| Logs e traces | Log JSON da API + Jaeger | ~1,5 min |
| Contexto (4 repos, gateway, docs) | README + diagramas | ~2,5 min |
| Encerramento + links | Repos + `soat-architecture` | ~1 min |

---

## Checklist antes de gravar (faça 1x)

### 1) Pré-requisitos
- [ ] Docker Desktop **ligado**
- [ ] Zoom do terminal/navegador ~110–125%
- [ ] Notificações desativadas
- [ ] Microfone testado; cronômetro em 15:00

### 2) Subir a stack (Compose = demo principal)

No PowerShell:

```powershell
cd "C:\Users\Nicholas\OneDrive\Área de Trabalho\POS-GRADUACAO\Fiap-Pos-Tech-Challenge-1"
docker compose ps
# Se não estiver UP:
# docker compose up --build -d
```

Confirme: `oficina-api` **healthy**, `oficina-kong` **healthy**.

### 3) Abrir abas na ordem (deixe prontas)

1. https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-1  
2. https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-auth-lambda  
3. https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-infra-k8s  
4. https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-infra-db  
5. Actions (um de cada repo) — preferir runs **success** em `main`  
6. Swagger Kong: http://localhost:8000/swagger-ui/index.html  
7. Grafana: http://localhost:3000 (`admin` / `admin`)  
8. Prometheus alerts: http://localhost:9090/alerts  
9. Jaeger: http://localhost:16686  
10. MailHog: http://localhost:8025  
11. Docs locais (VS Code): `docs/DIAGRAMA_ARQUITETURA.md`, `docs/DIAGRAMA_SEQUENCIA.md`, `docs/rfc/`, `docs/adr/`

### Credenciais

| Uso | Valor |
|---|---|
| Staff admin | `admin@oficina.com` / `admin123` |
| CPF cliente ATIVO (demo) | `52998224725` |
| JWT issuer | `oficina-auth` (HS512) |

---

## Como usar os 4 repositórios (falar + mostrar)

| # | Repo | Papel no vídeo | Como “usar” na prática |
|---|---|---|---|
| 1 | [auth-lambda](https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-auth-lambda) | Function serverless (CPF + notify) | Já sobe no Compose; CI em Actions; código `src/cpf_auth` |
| 2 | [infra-k8s](https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-infra-k8s) | Terraform: kind, Kong, HPA, Jaeger/Grafana | Mostrar `main.tf` + Actions; opcional `terraform apply` |
| 3 | [infra-db](https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-infra-db) | Terraform: Postgres gerenciado (simulação RDS) | Mostrar namespace `oficina-db` no README + Actions |
| 4 | [oficina-api](https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-1) | App no K8s + Compose full stack | `docker compose up`; Swagger; dashboard |

**Frase pronta:**  
> “Escolhemos simulação local equivalente à nuvem: Kong no lugar de API Gateway, functions Python com contrato Lambda, Postgres provisionado por Terraform em namespace isolado, e kind no lugar de EKS — sem custo de AWS, mesma arquitetura.”

---

## Timeline do vídeo (≤ 15 min)

| Bloco | Tempo | Título |
|-------|-------|--------|
| 0 | 0:00–0:40 | Abertura + objetivo |
| 1 | 0:40–2:10 | Quatro repositórios + CI/CD |
| 2 | 2:10–3:20 | Arquitetura (gateway, serverless, DB, K8s) |
| 3 | 3:20–5:20 | Auth CPF + JWT |
| 4 | 5:20–8:00 | APIs protegidas (abrir OS + acompanhamento) |
| 5 | 8:00–10:00 | Deploy automatizado + pipeline ao vivo |
| 6 | 10:00–12:20 | Dashboard + alertas |
| 7 | 12:20–14:00 | Logs JSON + traces Jaeger |
| 8 | 14:00–15:00 | Docs (RFC/ADR/ER) + encerramento |

---

## Bloco 0 — Abertura (0:00–0:40)

### Falar
> “Este é o Tech Challenge da Fase 3. A oficina precisa de autenticação segura por CPF, API Gateway, functions serverless, quatro repositórios com CI/CD, banco gerenciado, Kubernetes e observabilidade com dashboards e traces. Vamos demonstrar tudo isso em ambiente local que simula a nuvem.”

### Mostrar
- Tela com os 4 repos no GitHub (lista)  
- Diagrama mermaid do README da API (Client → Kong → auth/API → DB)

---

## Bloco 1 — Quatro repositórios + CI/CD (0:40–2:10)

### Falar
> “O PDF exige quatro repositórios: Lambda, infra Kubernetes, infra de banco e aplicação. Cada um tem GitHub Actions com deploy automático em `homolog` e `main`.”

### Mostrar (rápido, 15–20 s por repo)
1. Abrir cada repo → README (propósito + tecnologias)  
2. Aba **Actions** → filtrar `main` → run **verde** recente  
3. Citar branches `main` / `homolog`

### Links prontos (Actions)
- API: https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-1/actions  
- Auth: https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-auth-lambda/actions  
- K8s: https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-infra-k8s/actions  
- DB: https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-infra-db/actions  

### Opcional (se der tempo, 20 s)
Mostrar um workflow YAML: `.github/workflows/ci-cd.yml` (jobs `test` / `deploy`).

---

## Bloco 2 — Arquitetura (2:10–3:20)

### Falar
> “Tráfego norte-sul passa pelo Kong. `POST /auth/cpf` vai para a function. `/api/*` exige JWT. A API fala com Postgres e notifica a function de notify. Observabilidade: Prometheus, Grafana, Jaeger e logs JSON com correlation id.”

### Mostrar
1. `docs/DIAGRAMA_ARQUITETURA.md` (visão Fase 3)  
2. `docs/DIAGRAMA_SEQUENCIA.md` (fluxo CPF)  
3. Terminal: `docker compose ps` (Kong, auth, notify, api, db, grafana, jaeger)

---

## Bloco 3 — Autenticação com CPF (3:20–5:20) ★ obrigatório

### Preparação
Swagger: http://localhost:8000/swagger-ui/index.html  
**Servers:** `Current host (/)` ou `Kong API Gateway`

### Passo a passo na tela

**A) Auth CPF via function (pelo Kong)**  
Opção Swagger não tem `/auth/cpf` (é da lambda). Use PowerShell ou curl na mesma tela:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8000/auth/cpf `
  -ContentType 'application/json' `
  -Body '{"cpf":"52998224725"}'
```

Copie o `accessToken`.  
Falar: “Validamos dígitos do CPF, consultamos o cliente ATIVO no banco e emitimos JWT HS512 com issuer `oficina-auth` e profile CLIENTE.”

**B) Contraste rápido (opcional 15 s)**  
Tente CPF inexistente ou cliente INATIVO se souber um no seed (`11144477735` / Bruna) — mostrar 403/404.

**C) Login staff (Swagger)**  
`POST /api/auth/login`:

```json
{
  "email": "admin@oficina.com",
  "password": "admin123"
}
```

Falar: “Staff continua com e-mail/senha; cliente usa CPF na function.”

---

## Bloco 4 — APIs protegidas (5:20–8:00) ★ obrigatório

### Falar
> “Rotas sensíveis passam pelo Kong com plugin JWT e pela Security do Spring com ROLE_CLIENTE.”

### Passo a passo

1. **Authorize** no Swagger com o token do CPF (`Bearer …`)  
2. `POST /api/ordens-servico/cliente` — body validado (seed: João `52998224725`, placa `ABC1D23`):

```json
{
  "clienteCpfCnpj": "52998224725",
  "veiculoPlaca": "ABC1D23",
  "servicosSolicitadosIds": ["33333333-3333-3333-3333-333333333331"],
  "insumosSolicitadosIds": [],
  "pecasSolicitadasIds": []
}
```

> **Dica de gravação:** rode esse `POST` uma vez antes do vídeo e confirme **201**. Se a placa já tiver OS aberta e houver regra de negócio, use placa nova com marca/modelo/ano (`veiculoMarca`, `veiculoModelo`, `veiculoAno`).

3. Copie o `id` da OS criada.  
4. `GET /api/ordens-servico/{id}/acompanhamento?cpfCnpj=52998224725`  
5. Mostre **401** sem token (Execute sem Authorize) — prova proteção.  
6. Abra MailHog http://localhost:8025 — falar que status dispara notify-lambda / e-mail.

### Alternativa estável (PowerShell) se o body do Swagger falhar

```powershell
$cpf = Invoke-RestMethod -Method Post -Uri http://localhost:8000/auth/cpf `
  -ContentType 'application/json' -Body '{"cpf":"52998224725"}'
$h = @{ Authorization = "Bearer $($cpf.accessToken)" }
# Ajuste o body conforme o schema atual do OpenAPI
```

---

## Bloco 5 — Pipeline + deploy automatizado (8:00–10:00) ★ obrigatório

### Falar
> “Push em `homolog` e `main` dispara CI/CD: build, testes e deploy em cluster kind efêmero no runner — equivalente ao deploy automático exigido.”

### Mostrar
1. Actions **auth-lambda** — jobs `test` → `image` → `deploy` (verde)  
2. Actions **infra-k8s** — `validate` + `deploy`  
3. Actions **infra-db** — apply Postgres  
4. Actions **oficina-api** — Maven verify + Docker + Terraform/kind  

### Frase sobre branch protection (se ainda não estiver ativa)
> “Os repositórios usam fluxo por feature branch e merge em main/homolog; a proteção de main com PR obrigatório e o colaborador soat-architecture fazem parte da entrega do portal.”

*(Se `soat-architecture` e branch protection já estiverem ok, diga “já configurados” e mostre Settings → Collaborators / Branch protection.)*

---

## Bloco 6 — Dashboard ao vivo (10:00–12:20) ★ obrigatório

### Falar
> “Dashboard com volume diário de OS, tempo médio em Diagnóstico, Execução e Finalização, e erros de integração — via Grafana e endpoint da API.”

### Mostrar
1. **Grafana** http://localhost:3000 → dashboard **Oficina operational**  
   - Volume diário  
   - Minutos em diagnosis / execution / finalization  
   - Failures / integration errors  

2. **API** (token staff no Swagger):  
   `GET /api/monitoramento/dashboard`

3. **Prometheus** http://localhost:9090/alerts  
   - Mencionar alerta `WorkOrderProcessingFailure`  
   - “Datadog é opcional: agent + JSON de dashboard/monitor no repo infra-k8s; localmente usamos Grafana/Prometheus como stack equivalente.”

4. (Opcional 20 s) Actuator: http://localhost:8080/actuator/prometheus — buscar `oficina_os_`

---

## Bloco 7 — Logs e traces (12:20–14:00) ★ obrigatório

### Logs
```powershell
docker compose logs app --tail 30
```
Mostrar JSON com `correlationId` / `traceId` / `spanId`.

### Traces
1. Faça de novo um login ou `POST` de OS.  
2. Jaeger http://localhost:16686 → service `oficina-api` → Find Traces.  
3. Abrir um span e falar: “correlação ponta a ponta da requisição.”

### Health / uptime (10 s)
http://localhost:8080/actuator/health → `UP`

---

## Bloco 8 — Documentação + encerramento (14:00–15:00)

### Mostrar rápido
- RFC: `docs/rfc/RFC-001`, `RFC-002`, `RFC-003`  
- ADR: `docs/adr/ADR-001` (REST/Gateway), `ADR-002` (HPA), `ADR-003` (observabilidade)  
- ER: `docs/DIAGRAMA_ER.md` — “PostgreSQL relacional, status do cliente, histórico de status da OS”

### Falar (fechamento)
> “Entregamos os quatro repositórios com CI/CD, API Gateway Kong, auth serverless por CPF, Postgres gerenciado via Terraform, Kubernetes com HPA, dashboards, logs e traces. O PDF de entrega do portal terá os links dos repos, deste vídeo e a confirmação do usuário soat-architecture.”

Colar na tela (slide ou bloco de texto) os 4 links GitHub + URL do YouTube (após upload).

---

## Script curto (teleprompter ~14 min)

1. “Fase 3: segurança, serverless, quatro repos, observabilidade.”  
2. “Quatro GitHubs, cada um com Actions verde em main/homolog.”  
3. “Kong na frente; auth-lambda no CPF; API Spring; Postgres; Grafana/Jaeger.”  
4. “POST /auth/cpf → JWT CLIENTE.”  
5. “Authorize + POST OS cliente + GET acompanhamento; sem token = 401.”  
6. “Actions: test, image, deploy kind.”  
7. “Grafana volume/tempos; dashboard JSON; alertas Prometheus.”  
8. “Logs JSON e Jaeger.”  
9. “RFCs, ADRs, ER. Links e soat-architecture no PDF do portal.”

---

## Se algo falhar na gravação

| Problema | Ação rápida |
|---|---|
| Swagger chama `app:8080` | Ctrl+F5; Server = `/` ou `localhost:8000` |
| API unhealthy | `docker compose up -d app` e espere healthy |
| JWT curto / login 500 | Conferir `.env` com secret ≥ 64 bytes do `.env.example` |
| Banco antigo | `docker compose down -v` e `up -d` (**apaga dados**) |
| Grafana vazio | Chame `/api/monitoramento/dashboard` com staff e aguarde scrape (~15–30 s) |
| Sem tempo para kind local | Mostre apenas Actions de deploy (PDF aceita pipeline + evidência) |

---

## Após gravar (entrega portal)

1. Upload YouTube/Vimeo (público ou não listado), ≤ 15 min  
2. PDF único com:  
   - 4 links de repositórios  
   - Link do vídeo  
   - Links das documentações (`docs/` no repo API)  
   - Confirmação: usuário **`soat-architecture`** nos 4 repos  
3. Atualizar README da API com o novo link do vídeo (substituir o da Fase 2)

---

## Ordem mental: “como eu uso os repos no dia a dia”

```text
Desenvolvimento / demo rápida
  └─ Fiap-Pos-Tech-Challenge-1
       docker compose up --build -d
       → sobe API + auth + notify + Kong + DB + Grafana + Jaeger

Demo Kubernetes (opcional, se sobrar tempo no vídeo)
  1. infra-k8s     → terraform apply   (kind + Kong + obs)
  2. infra-db      → terraform apply   (Postgres oficina-db)
  3. auth-lambda   → build/load images + kubectl apply k8s/
  4. oficina-api   → build/load image  + kubectl apply k8s/
```

Para o vídeo de 15 minutos, **priorize Compose + GitHub Actions + Swagger/Grafana/Jaeger**. Kind local completo é bônus, não bloqueia o checklist do PDF do vídeo.
