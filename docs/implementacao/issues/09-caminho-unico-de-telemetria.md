# 09 — Caminho único de telemetria

**O que construir:** um único mecanismo de instrumentação, usado por todos os módulos. Ao fim deste
ticket, log, span, trace e métrica saem da API e do starter do processador pelo mesmo caminho e
chegam às ferramentas de pesquisa, painel e trace — e quem estiver diagnosticando não precisa
aprender um mecanismo diferente por módulo.

Vem cedo de propósito: instrumentação retrofitada depois de vinte tickets é instrumentação que
alguém esquece em três deles.

**Bloqueado por:** 02, 08.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] Log, span, trace e métrica saem para o **OTel Collector**, que os distribui para a pesquisa de
      log, as métricas/painéis e os traces (RA-36).
- [ ] Logs **estruturados**, enriquecidos com os identificadores de rastreamento (RA-38) — pesquisa
      por ocorrência, não por texto livre.
- [ ] O mecanismo mora na biblioteca comum (RA-02) e é consumido por API e starter da mesma forma.
- [ ] **RA-39** — o SDK de telemetria fica **desabilitado nos testes** (JUnit, Cucumber,
      Testcontainers): a suíte não depende de coletor nem gera telemetria.
- [ ] A convenção de rótulos já está definida — sigla do produto, código do relatório e origem da
      execução (RA-40) — mesmo que as métricas de negócio só apareçam no ticket 29.
- [ ] Um cenário prova que o Correlation ID exibido na resposta de erro (ticket 08) é encontrável
      pelo caminho de log configurado aqui.
