# Fase 4 — Microsserviços (ponte a partir deste repositório)

A Fase 4 **não refatora este monólito in-place**. Os entregáveis estão em repositórios irmãos:

| Repositório | Função |
|-------------|--------|
| [Fiap-Pos-Tech-Challenge-os-service](https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-os-service) | Ordens de serviço + **Saga orquestrado** (PostgreSQL) |
| [Fiap-Pos-Tech-Challenge-billing-service](https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-billing-service) | Orçamento + Mercado Pago (MongoDB) |
| [Fiap-Pos-Tech-Challenge-execution-service](https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-execution-service) | Fila de execução/produção (PostgreSQL) |
| [Fiap-Pos-Tech-Challenge-fase4](https://github.com/Nicholasspoltidesouza/Fiap-Pos-Tech-Challenge-fase4) | Hub: docker-compose, demo runner, observabilidade, guia e **PDF de entrega** |

## PDF de entrega do portal

Arquivo: `docs/Tech-Challenge-Fase4-Grupo50.pdf`  
(também em `docs/entrega-fase4/` com HTML, Mermaid e PNGs dos diagramas)

Conteúdo conforme enunciado:
- participantes
- links dos repositórios
- espaço para link do vídeo
- diagrama de arquitetura
- estratégia Saga (orquestração)
- justificativa da divisão e tecnologias

Guia completo: no hub `Fiap-Pos-Tech-Challenge-fase4/docs/GUIA_COMPLETO_SISTEMAS.md`

Este repositório (`Fiap-Pos-Tech-Challenge-1`) permanece como a solução das Fases 1–3 (API monolítica + Kong + observabilidade).
