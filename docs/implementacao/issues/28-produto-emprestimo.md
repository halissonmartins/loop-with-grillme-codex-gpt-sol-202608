# 28 — Produto EMPRESTIMO

**O que construir:** o quinto e último produto — catálogo publicado na subida, task na DAG, dois
relatórios de exemplo apurados do seu próprio schema transacional e entregues nos quatro formatos.
Ao fim deste ticket o Ciclo apura os **dez** relatórios de RNF-01, e o sistema está completo do lado
da Coleta.

**Bloqueado por:** 06, 14, 18.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] Schema transacional próprio, com dado de exemplo semeado, lido **exclusivamente** por este
      módulo (RA-10).
- [ ] **RA-08** — os dois relatórios de exemplo definidos no ticket 06, com imagens e fontes
      diferentes entre si.
- [ ] Cada relatório com o seu próprio JRXML (RA-07), na convenção de autoria do ticket 18.
- [ ] **RF-44, RF-27, RN-48** — códigos únicos dentro do produto, tempo estimado válido e soma
      dentro do teto, verificados na inicialização.
- [ ] **RA-65** — task estática do produto na DAG, no mesmo PR do módulo.
- [ ] **Teste obrigatório (RA-68)** — cabeçalho único no XLSX de **cada** relatório.
- [ ] Os quatro formatos saem corretamente, com as *font extensions* deste módulo no classpath da
      API.
- [ ] **Um ciclo completo roda de ponta a ponta com os cinco produtos**: reserva de 10 execuções,
      três ondas de pool 2, dez artefatos gravados e dez linhas de metadados. É a primeira vez que a
      janela de RNF-04 é observada de verdade — o número medido entra no ticket 35.
