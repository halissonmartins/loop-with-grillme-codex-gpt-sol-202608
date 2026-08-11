# 13 — Retentativa, vigência e unicidade

**O que construir:** o que acontece na segunda vez. Ao fim deste ticket, um relatório que falhou
ganha uma **nova** Execução de origem `retentativa` — com a anterior preservada e marcada como
não-vigente — a retentativa relê apenas o que não concluiu, existe um teto para ela, e uma tentativa
de reapurar um par já concluído com sucesso é recusada sem deixar rastro de falha na métrica.

**Bloqueado por:** 12.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RF-46** — a execução que falhou origina uma **linha nova** de origem `retentativa`; a
      anterior permanece e passa a não-vigente (RN-15, RN-46). Nenhum caminho reabre a execução
      anterior.
- [ ] **RN-44** — a retentativa relê **apenas** os relatórios que não concluíram. O que já produziu
      artefato válido não é reaberto.
- [ ] **RNF-17** — há teto de retentativas por relatório dentro de um ciclo, para que uma falha
      sistemática não vire laço infinito contra a base transacional.
- [ ] **RN-16** — no máximo **uma** execução vigente por par *data de referência + código do
      relatório*, garantida pelo schema.
- [ ] **RN-46** — "vigente" é **ponteiro**, não status. Um cenário prova que os quatro status
      continuam quatro e que não existe `invalidada`.
- [ ] **RF-08** — nova execução de par cuja vigente esteja em sucesso ou alerta é **recusada**, sem
      alterar artefatos e **sem criar Execução** (RN-17).
- [ ] **RN-18** — essa recusa vira **evento de auditoria** com solicitante, momento e Correlation
      ID. Se ela criar Execução, a métrica primária passa a contar tentativa recusada como falha de
      apuração — é o teste de risco alto que a especificação nomeia.
- [ ] **RF-09** — par cuja vigente esteja em `processado com erro` é executado de novo sem
      autorização especial (RN-19).
- [ ] **RN-43** — enquanto existir execução do par em `em processamento`, nova execução do mesmo par
      é recusada pela mesma via de auditoria.
- [ ] Registrado como risco aceito, não como defeito: dois relatórios do mesmo produto **podem
      enxergar instantes diferentes da base** quando um vem de retentativa (ADR-0001).
