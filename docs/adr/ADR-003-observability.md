# ADR-003 — Observability stack

- Status: Accepted
- Date: 2026-08-23

- JSON logs (Logstash encoder) with `correlationId`, `traceId` and `spanId`
- Micrometer Prometheus endpoint for Kong/Datadog scrape
- Zipkin reporter into Jaeger for traces
- Custom counters: `oficina.os.created`, `oficina.os.processing.failures`, `oficina.integration.errors`
- Status history table for average time in DIAGNOSTICO / EXECUCAO / FINALIZADA
- Optional Datadog agent when `datadog_api_key` is provided

Dashboards live as code in `Fiap-Pos-Tech-Challenge-infra-k8s/datadog/dashboard-oficina.json`.
