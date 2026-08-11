# 29 — Métricas de PRD §6 e painéis

**O que construir:** as seis métricas do PRD medidas de verdade. Ao fim deste ticket dá para abrir
um painel e responder "o sistema está cumprindo a sua promessa?" sem consulta manual ao banco —
que é a condição que o guia impõe: métrica de PRD não medida de verdade é métrica que não existe.

**Bloqueado por:** 09, 14, 17.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RF-52** — **taxa de apuração limpa**: percentual dos pares cuja **execução vigente de origem
      `agendada`** terminou em `processado com sucesso`, em **janela móvel de 30 dias**. Contam-se
      execuções vigentes, não tentativas; reprocessamentos forçados ficam fora.
- [ ] **Retentativas por mês** como métrica **separada** — a primária mede entrega e esconde
      instabilidade. Sem esta, contar vigentes vira maquiagem (PRD §6, ADR-0004).
- [ ] Execuções presas em `em processamento` **30 min após o fim do ciclo**, esperando **zero**.
      Qualquer valor maior é defeito, não tendência (RN-10).
- [ ] Reprocessamentos forçados por mês — é o sintoma de apuração instável.
- [ ] Exportações que terminam em erro **e** exportações recusadas por limite de simultaneidade. Se
      a segunda subir, o teto de RNF-10 está apertado demais para o uso real.
- [ ] **RA-40** — todas rotuladas por **sigla do produto**, **código do relatório** e **origem da
      execução**. Sem a origem é impossível separar apuração agendada de retentativa e de
      reprocessamento, e as séries do PRD §6 se contaminam.
- [ ] A retenção indefinida dos metadados de Execução (RN-51) é o que torna a janela de 30 dias
      calculável — um cenário afirma que a série não trunca junto com o artefato de 7 dias.
- [ ] Painel montado com as seis, e as metas marcadas como `PROVISÓRIO` onde ainda são (PRD §6, Q11:
      a meta de 98% só se confirma após o primeiro mês de operação).
