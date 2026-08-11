# 18 — Exportação XLSX contínua e o teste de cabeçalho único

**O que construir:** a planilha utilizável como tabela. Ao fim deste ticket o XLSX sai contínuo — o
cabeçalho de coluna aparece **exatamente uma vez**, sem cabeçalho e rodapé de página repetidos no
meio dos dados — e existe um teste por relatório que garante que continue assim.

Este ticket bloqueia os quatro produtos restantes de propósito: a convenção de autoria tem que
existir e estar testada **antes** de alguém escrever mais oito JRXML, senão ela apodrece em silêncio
no primeiro relatório escrito por quem não leu o ADR.

**Bloqueado por:** 17.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **A convenção de autoria, escrita e publicada** (ADR-0006, RA-59): todo JRXML do projeto
      coloca o cabeçalho de coluna na banda `title`, renderizada uma única vez, e mantém em
      `pageHeader`/`pageFooter` apenas ornamento descartável.
- [ ] A convenção está no `ARCHITECTURE.md` como invariante e tem um JRXML de referência apontado
      nominalmente — regra escrita sem exemplo canônico não é seguida.
- [ ] **RF-21** — a exportação XLSX exclui essas bandas **por origem de elemento**, sem paginar por
      planilha e sem espaço vazio entre linhas (RN-33).
- [ ] Está registrado por que isto **não** é configuração de exportação: `ignorePagination` age no
      preenchimento, e `pageHeader`/`pageFooter` já estão gravados por página dentro do artefato.
      Reconstruir o print sem paginação exigiria preencher de novo, o que fere RN-31 e RN-44.
- [ ] **Teste obrigatório (RA-68)** — **cada** relatório tem cenário que exporta em XLSX e afirma
      que o cabeçalho aparece **exatamente uma vez**. É por relatório, não por módulo.
- [ ] O teste é parte da definição de pronto de todo relatório novo — os tickets 25 a 28 o herdam.
