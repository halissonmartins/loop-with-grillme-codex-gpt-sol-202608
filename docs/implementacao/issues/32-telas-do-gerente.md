# 32 — Telas do GERENTE

**O que construir:** a interface que tira o GERENTE do chamado para TI. Ao fim deste ticket ele
monta uma Role de relatório, aponta-a para os relatórios que ela alcança, cria um Grupo, vincula os
dois e põe pessoas dentro — vendo, em cada passo, o efeito da concessão.

**Bloqueado por:** 23, 30.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RF-31** — criar e remover Role de relatório, e vinculá-la a relatórios e a grupos.
- [ ] **RF-32** — criar e remover Grupo, e incluir/remover usuários RELATOR nele.
- [ ] A tela deixa visível **o que a concessão alcança**: dado um grupo, quais relatórios os seus
      membros passam a enxergar — a Cadeia é N:N em todos os elos e a união não é óbvia de cabeça
      (RN-22).
- [ ] A remoção avisa o efeito antes de confirmar, porque a revogação é imediata (RF-36) e cirúrgica
      (RF-43).
- [ ] **RF-18** — o GERENTE não vê caminho algum para exportar relatório (RN-25). A ausência é
      afirmada por cenário, não presumida.
- [ ] **RF-34** — não existe na interface controle algum que atribua Perfil. O GERENTE opera sobre
      Roles de relatório, e Perfil é outro tipo de objeto (RN-26, ADR-0003).
- [ ] Tudo pelos componentes canônicos e tokens do ticket 30.
