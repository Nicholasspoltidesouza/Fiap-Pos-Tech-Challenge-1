# RFC-001 — Local cloud simulation (Kong + kind + serverless functions)

- Status: Accepted
- Date: 2026-08-23

## Context

The Tech Challenge requires an API Gateway, a serverless function for CPF authentication,
managed PostgreSQL, Kubernetes and observability. A full AWS account (API Gateway + Lambda +
RDS + EKS) would meet the letter of the assignment but has high cost and operational risk
for a course delivery.

## Decision

Simulate the target cloud architecture locally:

| Cloud building block | Local equivalent |
|---|---|
| AWS API Gateway | Kong (DB-less) |
| AWS Lambda (auth + notify) | Python functions with Lambda handler contract, packaged as containers |
| Amazon RDS PostgreSQL | PostgreSQL 15 provisioned by Terraform in an isolated namespace |
| EKS | kind cluster with metrics-server and HPA |
| Datadog / New Relic | Datadog agent (optional API key) + Jaeger + Prometheus + JSON logs |

## Consequences

- Four repositories remain independent and have their own CI/CD.
- The same JWT issuer (`oficina-auth`) and HS512 secret are shared by Kong, the auth function and Spring.
- Moving to AWS later is a provider swap, not a redesign.
