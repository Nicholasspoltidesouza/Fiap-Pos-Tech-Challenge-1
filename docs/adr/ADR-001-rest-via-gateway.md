# ADR-001 — Synchronous REST through the API Gateway

- Status: Accepted
- Date: 2026-08-23

North-south traffic is HTTP REST via Kong. The workshop bounded context is request/response
(open work order, change status, query tracking). Asynchronous messaging is used only for
notifications (HTTP call to the notify function, which then talks to SMTP).

This keeps the academic surface small while still showing a serverless side-effect path.
