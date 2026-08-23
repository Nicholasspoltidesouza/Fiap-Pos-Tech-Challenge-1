# Infra in this repository

The Kubernetes cluster, Kong, Jaeger and the managed PostgreSQL instance now live in:

- `Fiap-Pos-Tech-Challenge-infra-k8s`
- `Fiap-Pos-Tech-Challenge-infra-db`

The Terraform files kept here remain a **self-contained CI smoke test** (kind + Postgres + app)
so this repository still has a green pipeline without checking out the sibling repos.

For the architecture demo, apply the three sibling repositories in this order:

1. infra-k8s
2. infra-db
3. auth-lambda images + manifests
4. this repository (`k8s/01-configmap.yaml`, `k8s/02-secret.yaml`, `k8s/03-postgres-external.yaml`, `k8s/04-mailhog.yaml`, `k8s/05-app.yaml`, `k8s/06-hpa.yaml`). Skip `k8s/03-postgres.yaml` so the managed database in `oficina-db` remains the only PostgreSQL.
