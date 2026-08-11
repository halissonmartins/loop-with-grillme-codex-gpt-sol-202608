# 04 — Artefatos descartáveis: protótipo dos fluxos e Swagger

**O que construir:** os **dois artefatos descartáveis** que a arquitetura pedia como primeiro e
segundo tickets do projeto e que o fatiamento em tracer bullets perdeu — o protótipo navegável da
interface e o Swagger de fachada da API. Este ticket é o dono deles: a lista antiga da arquitetura
§15 foi substituída por um ponteiro para este tracker, justamente porque manter as duas divergiu.

O protótipo é HTML, CSS e JavaScript puros — sem framework, sem backend, sem build, com dado de
mentira embutido no próprio arquivo. Ao fim dele dá para percorrer com o mouse os dois fluxos que
ninguém acerta lendo prosa, e julgar o desenho **antes** que o ticket 05 o fixe em tokens e o 30 o
implemente em Angular.

Por que exatamente estes dois fluxos: pela tabela de fidelidade do guia, protótipo navegável é o
tratamento de *"fluxo crítico, ambíguo ou inédito"*. A listagem carrega a única armadilha do domínio
que a interface precisa desfazer **ativamente**, e a cadeia de permissão tem quatro elos N:N — a tela
que fica ilegível na primeira tentativa e cujo problema não aparece em documento escrito.

O Swagger é a outra metade da mesma ideia, do lado da API: um contrato desenhado à mão, sem
implementação nenhuma atrás, que fixa o formato das respostas **antes** que o ticket 08 as improvise.
Os dois se sustentam mutuamente — o protótipo mostra as telas que o contrato precisa alimentar, e o
contrato mostra o que a tela pode pedir. Fazer os dois no mesmo ticket é o que impede que divirjam.

**Bloqueado por:** nada — pode começar imediatamente.

**Status:** ready-for-agent

**Critérios de aceitação — protótipo**

- [ ] **Somente HTML, CSS e JavaScript.** Sem framework, sem npm, sem
      backend: abre com um duplo clique. Precisar de build é o primeiro sinal de que o protótipo está
      virando produto.
- [ ] **Fluxo 1, encontrar um relatório** — o drop-down encadeado data (`dd/MM/yyyy`) → nome do
      produto → código do relatório (RF-13, RA-19).
- [ ] O protótipo mostra **como a interface desfaz a armadilha da data de referência**: o artefato de
      uma data carrega o movimento fechado do dia anterior (RN-07). Rótulo duplo, texto de apoio,
      outra coisa — a escolha é o que se está aqui para julgar com o olho, e é a razão de este fluxo
      não poder ser resolvido só em `fluxos.md`.
- [ ] **Fluxo 2, conceder acesso** — a vinculação de roles de relatório a grupos e de usuários a
      grupos, com os elos N:N visíveis. Um usuário em dois grupos aparece com a **união** dos
      relatórios (RN-22), porque é a interseção que as pessoas erram ao desenhar essa tela.
- [ ] Os desfechos não-felizes do download aparecem **como tela**, não como ideia: artefato expurgado
      por retenção (RN-39), execução vigente fora de sucesso ou alerta (RN-42) e recusa por limite de
      simultaneidade (RN-53). Três mensagens distintas, nenhuma delas genérica.
- [ ] Interface e mensagens em pt-BR (RNF-15).
- [ ] **Aceite humano, e este critério não é verificável por teste automatizado:** quem responde pelo
      produto percorre os dois fluxos ponta a ponta e consegue **explicar cada tela sem hesitar** —
      o checkpoint de P1 do guia. O ticket não fecha sozinho nem por CI verde. O registro do aceite,
      com a data e o que mudou depois da primeira passada, entra no ticket 05 junto com as decisões
      descartadas.
- [ ] **O código morre aqui.** Nada deste protótipo é promovido: o Angular do ticket 30 parte dos
      componentes canônicos do ticket 05, nunca daqui. O guia é explícito em que protótipo gerado por
      agente é o mais tentador de promover, justamente porque já *parece* funcionar — e não tem
      validação, autorização, tratamento de erro nem teste.
- [ ] Vive em `docs/design/prototipo/`, com um `README.md` de duas linhas dizendo que é descartável e
      qual ticket o aposentou. Daqui a três meses ninguém deve conseguir confundi-lo com o frontend.

**Critérios de aceitação — Swagger descartável**

- [ ] **Swagger de fachada**, escrito à mão e **sem implementação atrás**.
      Cobre os endpoints que os dois fluxos do protótipo exigem: a listagem navegável, a exportação
      por formato e a vinculação de roles a relatórios, de roles a grupos e de usuários a grupos.
- [ ] **O contrato de erro de RA-41 já aparece aqui**, com os três campos de RN-40: momento do erro
      em ISO 8601, descrição e Correlation ID. É o formato que o ticket 08 vai implementar, não uma
      aproximação a ser renegociada depois.
- [ ] Os três desfechos não-felizes da exportação têm **respostas distintas e documentadas** —
      artefato expurgado (RN-39), execução vigente fora de sucesso ou alerta (RN-42) e recusa por
      simultaneidade (RN-53). São as mesmas três telas do protótipo, vistas do outro lado.
- [ ] **RF-53** — nenhuma operação aceita data de referência como parâmetro de entrada (RN-54). O
      contrato de fachada é exatamente onde alguém acrescenta um `dataReferencia` por hábito, e onde
      isso custa uma linha para desfazer em vez de uma migração.
- [ ] **Morre no ticket 08**, substituído pelo SpringDoc OpenAPI gerado a partir do código. Fica em
      `docs/design/`, ao lado do protótipo e com a mesma marca de descartável.
