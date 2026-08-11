# Execução append-only, com ponteiro de vigência

A Execução é um **registro imutável de um evento**, não uma linha que representa o estado atual do
par *Relatório + Data de referência*. Retentativa e reprocessamento forçado **inserem linha nova** e
movem um ponteiro de vigência; nenhum caminho do sistema atualiza o status de uma execução já
terminal.

Não foi uma preferência: RN-15 proíbe mudar status terminal, e a retentativa precisa de algum lugar
onde registrar o seu resultado. Se a Execução fosse mutável, uma das duas regras teria que cair.

## Consequences

- "Execução vigente" (RN-16) é **um ponteiro, não um status** — e por isso o ciclo de vida continua
  com quatro status, sem inventar um `invalidada`.
- O **tempo estimado é copiado para dentro da Execução** no momento do disparo. Alterar o catálogo
  não reclassifica execuções passadas, e a métrica primária permanece comparável ao longo do tempo.
- Toda Execução declara a sua **origem**: `agendada`, `retentativa` ou `reprocessamento forçado`.
  Sem essa distinção, a métrica de apuração e a de instabilidade se contaminam mutuamente.
- **A métrica primária conta execuções vigentes agendadas**, uma por par por dia: mede *entrega*, não
  tentativa. **Consequência aceita:** um relatório que falhou e foi salvo pela retentativa aparece
  como limpo. É por isso que a métrica de retentativas precisa existir junto — sem ela, esta decisão
  esconde instabilidade em vez de revelá-la.
- A execução invalidada por reprocessamento forçado permanece, ligada ao motivo e ao solicitante de
  RN-21. Auditoria que apaga o registro auditado não é auditoria.
