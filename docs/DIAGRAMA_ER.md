# Logical data model (ER) — oficina-api

PostgreSQL remains the system of record. The managed instance (simulated RDS) lives in namespace `oficina-db`. Hibernate `update` plus `data.sql` keep the schema and seed data aligned for the course demo.

```mermaid
erDiagram
    CLIENTE ||--o{ VEICULO : owns
    CLIENTE ||--o{ ORDEM_SERVICO : opens
    VEICULO ||--o{ ORDEM_SERVICO : is-serviced
    USUARIO ||--o{ ORDEM_SERVICO : staff-operates
    ORDEM_SERVICO ||--o{ ORDEM_SERVICO_ITEM : contains
    SERVICO ||--o{ ORDEM_SERVICO_ITEM : quoted-as
    ORDEM_SERVICO ||--o{ ORDEM_SERVICO_STATUS_HISTORICO : audits

    CLIENTE {
        uuid id PK
        string nome
        string cpf_cnpj UK
        string status "ATIVO|INATIVO"
    }

    VEICULO {
        uuid id PK
        uuid cliente_id FK
        string placa
        string marca
        string modelo
        int ano
    }

    USUARIO {
        uuid id PK
        string nome
        string email UK
        string senha_hash
        string perfil "ADMIN|ATENDENTE|MECANICO"
    }

    ORDEM_SERVICO {
        uuid id PK
        uuid cliente_id FK
        uuid veiculo_id FK
        string status
        timestamp data_abertura
        numeric valor_orcamento
    }

    ORDEM_SERVICO_ITEM {
        uuid id PK
        uuid ordem_id FK
        uuid servico_id FK
        numeric preco
    }

    SERVICO {
        uuid id PK
        string descricao
        numeric preco
    }

    ORDEM_SERVICO_STATUS_HISTORICO {
        uuid id PK
        uuid ordem_id FK
        string status
        timestamp alterado_em
    }
```

## Why PostgreSQL (relational)

- Work orders are strongly consistent transactions (client + vehicle + items + status history).
- CPF lookup must be exact and unique; relational unique indexes enforce that without a document store.
- Status history is an append-only child table, ideal for SQL and for the observability/audit requirement.
- The course target on AWS is RDS PostgreSQL; the local Terraform release in `oficina-db` is the same contract (`postgres.oficina-db.svc.cluster.local:5432`).
