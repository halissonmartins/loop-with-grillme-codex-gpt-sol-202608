# API REST

Siga o `CLAUDE.md` da raiz.

- Toda exportação deve adquirir e liberar o semáforo global de simultaneidade (RA-60).
- Todo erro HTTP segue o contrato RA-41: horário ISO 8601, status, título, detalhe, código interno,
  rota e Correlation ID.
- A API nunca acessa schema transacional de produto; usa somente artefatos e schema de controle.
