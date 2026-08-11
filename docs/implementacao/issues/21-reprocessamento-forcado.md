# 21 — Reprocessamento forçado ponta a ponta

**O que construir:** o único caminho pelo qual um dado congelado pode ser refeito. Ao fim deste
ticket o ADMINISTRADOR informa um motivo, a API captura solicitante, motivo e Correlation ID e
dispara o orquestrador; a execução anterior é invalidada **e preservada**, ligada a esse registro; e
os artefatos são sobrescritos, de modo que a exportação passa a entregar o dado corrigido.

**Bloqueado por:** 13, 14, 15.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RA-13** — quem aciona a DAG é a **API**, nunca o usuário. O Airflow não é exposto ao usuário
      final, e solicitante, motivo e Correlation ID são capturados antes do disparo.
- [ ] **RA-12** — o contrato entre API e orquestrador é o parâmetro `forcar_reprocessamento`. **Não
      existe parâmetro de data de referência** (RN-54).
- [ ] **RF-10** — a execução anterior é invalidada e **permanece registrada**; os artefatos são
      sobrescritos (RN-20, RN-46). Auditoria que apaga o registro auditado não é auditoria.
- [ ] **RF-11** — solicitação **sem motivo** é rejeitada (RN-21).
- [ ] **RF-12** — usuário de perfil diferente de ADMINISTRADOR não consegue solicitar, **nem
      manipulando a requisição**.
- [ ] **RN-20, ADR-0005** — opera **somente sobre a data de referência corrente**. Não há caminho
      algum de apuração retroativa.
- [ ] O evento de auditoria registra solicitante, motivo, momento e Correlation ID (RN-21), e é
      distinguível da recusa de RN-18 do ticket 13.
- [ ] Um cenário exporta antes e depois e afirma que o conteúdo entregue mudou — é o que prova que
      "sobrescreve" aconteceu de fato.
