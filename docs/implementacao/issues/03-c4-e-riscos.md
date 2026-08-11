# 03 — Artefatos de arquitetura faltantes: C4 e riscos

**O que construir:** os dois artefatos que E0 do guia exige e que o repositório não tem. O C4
responde "como o sistema se relaciona com o mundo e quais são os contêineres"; o registro de riscos
responde "o que ainda pode dar errado e como será resolvido". Ao fim deste ticket, quem chega ao
projeto enxerga a fronteira do sistema sem ler os 680 parágrafos da especificação.

**Bloqueado por:** nada — pode começar imediatamente.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] Diagrama C4 **nível 1** (contexto) e **nível 2** (contêineres) em Mermaid, versionado em
      Markdown para que possa ser lido e atualizado sem ferramenta externa.
- [ ] O nível 2 mostra a assimetria que justifica o sistema: a Coleta como **única** fronteira de
      leitura dos schemas transacionais (RA-10, RA-29), e a API lendo apenas artefato e schema de
      controle.
- [ ] Registro de riscos técnicos abertos, com o encaminhamento de cada um. No mínimo: os dois
      spikes ainda pendentes (calibração com `k6` e conferência do gatilho do expurgo) e os quatro
      riscos aceitos da especificação §4.4.
- [ ] Cada risco aceito aponta para o ADR que o decidiu — o registro não redecide nada, só torna
      visível o que já foi deliberado.
- [ ] Os documentos entram na tabela de documentos relacionados do PRD e da arquitetura, que hoje os
      marcam como inexistentes.
