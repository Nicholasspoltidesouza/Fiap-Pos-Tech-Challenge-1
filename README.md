# Fiap Pos Tech - Tech Challenge 1

API REST para gerenciamento de oficina mecânica, desenvolvida como primeiro desafio da FIAP Pós Tech Architecture.

## Tecnologias

- Java 25 + Spring Boot 4
- Spring Security 7 com autenticação JWT
- Spring Data JPA + PostgreSQL
- Springdoc OpenAPI 3 (Swagger UI)
- Docker + Docker Compose
- Lombok

## Pré-requisitos

- Java 25+
- Maven 3.9+ (ou use o `mvnw` incluso)
- PostgreSQL 15+ **ou** Docker

## Rodando localmente

### Com Docker (recomendado)

```bash
cp .env.example .env
docker-compose up --build
```

### Sem Docker

1. Crie o banco de dados no PostgreSQL:

```sql
CREATE DATABASE oficina_mec_db;
```

2. Ajuste as variáveis de ambiente se necessário (padrão: `admin`/`admin`, porta `5432`):

```bash
export POSTGRES_USER=admin
export POSTGRES_PASSWORD=admin
export POSTGRES_DB=oficina_mec_db
```

3. Execute a aplicação:

```bash
./mvnw spring-boot:run
```

## Acessando a API

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

## Autenticação JWT

A API utiliza autenticação stateless via JWT. O fluxo é:

### 1. Criar usuário

```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@oficina.com",
  "password": "senha123",
  "profile": "ADMIN"
}
```

Perfis disponíveis: `ADMIN`, `ATENDENTE`, `MECANICO`

### 2. Fazer login

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "joao@oficina.com",
  "password": "senha123"
}
```

A resposta retorna o `accessToken` (validade: 1h) e o `refreshToken` (validade: 7 dias).

### 3. Usar o token

Adicione o header em todas as requisições protegidas:

```
Authorization: Bearer <accessToken>
```

### 4. Renovar o token

```http
POST http://localhost:8080/api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "<refreshToken>"
}
```

### Autenticando no Swagger UI

1. Acesse http://localhost:8080/swagger-ui.html
2. Faça login via `POST /api/auth/login` ou registre um usuário via `POST /api/auth/register`
3. Copie o `accessToken` da resposta
4. Clique no botão **Authorize** (cadeado) no topo da página
5. Cole o token e clique em **Authorize**
6. Todos os endpoints passam a ser executados autenticados

## Variáveis de ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `POSTGRES_HOST` | `localhost` | Host do PostgreSQL |
| `POSTGRES_PORT` | `5432` | Porta do PostgreSQL |
| `POSTGRES_DB` | `oficina_mec_db` | Nome do banco |
| `POSTGRES_USER` | `admin` | Usuário do banco |
| `POSTGRES_PASSWORD` | `admin` | Senha do banco |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Estratégia DDL do Hibernate |
