# RFC-002 — Managed PostgreSQL

- Status: Accepted
- Date: 2026-08-23

## Context

The workshop already uses a relational model (customers, vehicles, work orders, stock).
The new phase asks for a managed database and a formal justification of that choice.

## Decision

Keep PostgreSQL 15, now provisioned as a dedicated Terraform stack
(`Fiap-Pos-Tech-Challenge-infra-db`) that mimics RDS:

- Isolated namespace `oficina-db`
- Persistent volume
- Credentials in Kubernetes Secret
- Health probes and resource limits
- Stable DNS `postgres.oficina-db.svc.cluster.local`

PostgreSQL remains the right engine because the domain is strongly relational (1:N customer-vehicle,
N:N work-order-service/parts), needs transactions for stock consumption, and already has indexes
on CPF/CNPJ and plate.

## Consequences

The application no longer owns the database Deployment. Schema evolution stays in Hibernate
`ddl-auto=update` plus `data.sql` for the academic bootstrap.
