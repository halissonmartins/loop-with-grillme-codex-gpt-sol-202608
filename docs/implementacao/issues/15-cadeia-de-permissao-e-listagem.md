# 15 — Cadeia de permissão e listagem de relatórios disponíveis

**O que construir:** a primeira coisa que o usuário final vê. Ao fim deste ticket um RELATOR entra,
navega por data → produto → relatório e enxerga **exclusivamente** o que a sua Cadeia de permissão
alcança; um ADMINISTRADOR enxerga tudo sem passar por ela; um GERENTE não enxerga relatório algum
para exportar; e quem se cadastrou ontem e ainda não tem grupo recebe uma listagem vazia com uma
mensagem que explica o que falta.

**Bloqueado por:** 10, 11.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RA-61** — a resolução é híbrida: Perfil e Role de relatório vêm do token; o elo *relatório →
      role de relatório* vem da tabela do schema de controle. A listagem resolve numa consulta só, a
      partir das client roles presentes no token.
- [ ] **RF-13** — navegação por data → produto → relatório, com a data exibida em `dd/MM/yyyy`
      (RN-08).
- [ ] **RF-14** — a listagem de um RELATOR contém **exclusivamente** os relatórios alcançados pela
      sua cadeia (RN-22, RN-23), e o acesso efetivo é a **união** de todos os caminhos (RF-33).
- [ ] **RF-15** — acesso direto a relatório fora da permissão é **negado**. Esconder da listagem não
      é a única defesa.
- [ ] **Teste obrigatório (RA-68)** — **RF-17**: o ADMINISTRADOR alcança relatório que **nenhuma**
      cadeia de permissão lhe concede (RN-24). É a única exceção de autorização do sistema e por
      isso exige cenário próprio e nominal.
- [ ] **RF-18** — o GERENTE não exporta relatório algum (RN-25): administrar acesso e consumir dado
      são capacidades separadas.
- [ ] **RF-16, RF-55** — RELATOR *pendente de vínculo* entra, recebe listagem **vazia** e a mensagem
      de que aguarda a configuração das permissões (RN-28). Não há acesso concedido por omissão.
- [ ] **RA-29** — a listagem **nunca** toca schema transacional. Qualquer dependência da API para um
      schema de produto é defeito, não escolha.
- [ ] A data de referência aparece rotulada de modo que não se confunda com o dia do movimento
      (RN-07) — o texto vem dos fluxos do ticket 05.
