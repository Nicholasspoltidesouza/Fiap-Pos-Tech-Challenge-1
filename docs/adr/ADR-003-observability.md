# ADR-003 — Observability stack

- Status: Accepted
- Date: 2026-08-23

- JSON logs (Logstash encoder) with `correlationId`, `traceId` and `spanId`
- Micrometer Prometheus endpoint plus Grafana/Alertmanager for local dashboards and work-order failure alerts
- Zipkin reporter into Jaeger for traces
- Custom counters: `oficina.os.created`, `oficina.os.processing.failures`, `oficina.integration.errors`
- Timer `oficina.os.status.duration` and dashboard gauges for daily volume / average minutes in DIAGNOSTICO, EXECUCAO and FINALIZADA
- Status history table as the source of average time in those statuses
- Optional Datadog agent and metric export when `DATADOG_API_KEY` is provided

Local dashboards:
- Grafana: `http://localhost:3000` (admin/admin)
- Prometheus alerts: `http://localhost:9090/alerts`
- API JSON dashboard: `GET /api/monitoramento/dashboard`

Datadog-as-code lives in `Fiap-Pos-Tech-Challenge-infra-k8s/datadog/` (`dashboard-oficina.json` and `monitor-os-processing-failures.json`).
