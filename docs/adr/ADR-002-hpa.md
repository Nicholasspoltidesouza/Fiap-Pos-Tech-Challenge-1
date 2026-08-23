# ADR-002 — Horizontal Pod Autoscaler

- Status: Accepted
- Date: 2026-08-23

`oficina-api` scales with an HPA (CPU 70%, memory 80%, 1–5 replicas). metrics-server is
installed by the Kubernetes Terraform stack. The API is stateless (JWT, no HTTP session),
so extra replicas are safe. PostgreSQL stays a single replica in this local simulation.
