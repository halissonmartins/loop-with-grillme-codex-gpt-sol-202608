# 05 — Fluxos principais e design system

**O que construir:** os dois artefatos do eixo de Produto/Design que o guia trata como
**pré-requisito de E3** e que a especificação §6 registra como inexistentes. Ao fim deste ticket
existe um percurso escrito dos fluxos críticos com os seus estados de erro, e uma linguagem visual
fixada em tokens — antes que a primeira tela a improvise. Sem ele, o ticket 30 nasce inventando
espaçamento e cor, e a inconsistência só aparece na vigésima tela.

**Bloqueado por:** 04.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] De três a cinco fluxos principais escritos passo a passo, **com os estados de erro**. No
      mínimo: encontrar e baixar um relatório; um RELATOR pendente de vínculo entrando pela primeira
      vez; um GERENTE concedendo acesso a um conjunto de relatórios.
- [ ] O fluxo de download cobre explicitamente os desfechos que não são o feliz: artefato expurgado
      por retenção (RN-39), execução vigente fora de sucesso ou alerta (RN-42) e recusa por limite
      de simultaneidade (RN-53). São três mensagens diferentes, e nenhuma delas é "erro genérico".
- [ ] O fluxo de listagem trata a armadilha do domínio: a interface precisa **desfazer ativamente**
      a leitura errada da data de referência — o artefato de uma data carrega o movimento fechado do
      dia anterior (RN-07).
- [ ] Design system com tokens de cor, tipografia, espaçamento, raio e sombra, e a regra de que
      nenhum valor fora deles é admitido.
- [ ] Padrões de estado definidos uma única vez: carregando, vazio, erro, sucesso, desabilitado.
- [ ] Requisitos de acessibilidade declarados: contraste mínimo AA, foco visível em todo elemento
      interativo, rótulo associado a todo campo.
- [ ] Registro do porquê de cada fluxo ser esse e do que foi descartado.
- [ ] Idioma da interface e das mensagens em pt-BR (RNF-15).
