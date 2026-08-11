# 30 — Shell Angular, OIDC e componentes canônicos

**O que construir:** a casca da aplicação web e os componentes que todas as telas seguintes vão
copiar. Ao fim deste ticket dá para entrar, ver o próprio perfil, sair — e existe no repositório
**uma implementação real** de botão, campo, formulário, tabela e modal, com os estados de
carregando, vazio, erro e sucesso já resolvidos.

Este ticket materializa o design system do ticket 05. Documento sem implementação de referência não
é seguido: as sessões seguintes copiam o que encontram no código, não o que está escrito.

**Bloqueado por:** 05, 10.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RA-06** — o frontend integra-se **exclusivamente** com os endpoints da API. Não acessa
      repositório, banco nem orquestrador. É invariante, não preferência.
- [ ] **F12** — entrar e sair via OIDC contra o provedor de identidade, com o JWT trafegando para a
      API (RA-30).
- [ ] Tokens do design system aplicados como a única fonte de cor, tipografia, espaçamento, raio e
      sombra. Nenhum valor fora deles.
- [ ] Um componente real por padrão canônico, apontado nominalmente no `ARCHITECTURE.md`.
- [ ] Estados de carregando, vazio, erro e sucesso implementados uma vez e reutilizados.
- [ ] **RF-38** — a tela de erro exibe momento, descrição e Correlation ID (RN-40).
- [ ] **RF-39** — a tela de erro oferece **cópia do erro em JSON** com um clique, para anexar em
      chamado (RA-42).
- [ ] Acessibilidade do ticket 05 verificada: contraste AA, foco visível, rótulo associado a todo
      campo.
- [ ] Estrutura de pastas com um exemplo por camada e os cenários em Gherkin no diretório de e2e
      (RA-46).
- [ ] Interface e mensagens em pt-BR (RNF-15).
