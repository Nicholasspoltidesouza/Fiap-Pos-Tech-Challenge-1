# RFC-003 — CPF authentication via serverless function

- Status: Accepted
- Date: 2026-08-23

## Context

Staff already authenticate with e-mail/password. Customers must now authenticate with CPF,
have their status checked, and receive a JWT to call protected APIs.

## Decision

1. `POST /auth/cpf` is served by the auth function (not by Spring).
2. The function validates CPF check digits, queries `tb_cliente`, rejects `INATIVO`.
3. JWT claims: `iss=oficina-auth`, `profile=CLIENTE`, `tokenType=ACCESS`, `cpf`, `userId`.
4. Algorithm HS512 with the same secret used by Spring and Kong.
5. Staff tokens keep e-mail as `sub`; customer tokens use CPF as `sub`.
6. Opening and tracking work orders by the customer require `ROLE_CLIENTE`.

Staff login (`/api/auth/login`) remains for ADMIN / ATENDENTE / MECANICO.
