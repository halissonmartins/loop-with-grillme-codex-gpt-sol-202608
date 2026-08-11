# 23 — Gestão de acesso pelo GERENTE

**O que construir:** a razão de o GERENTE existir. Ao fim deste ticket ele cria uma Role de
relatório, aponta-a para um conjunto de relatórios, cria um Grupo, vincula a role ao grupo e põe
pessoas dentro — tudo sem abrir chamado para TI. E não consegue, por caminho algum, se promover.

**Bloqueado por:** 10, 15.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RF-31** — o GERENTE cria e remove Roles de relatório (como **client roles** do cliente
      dedicado) e as vincula a relatórios (tabela do schema de controle) e a grupos (RA-61).
- [ ] **RF-32** — o GERENTE cria e remove Grupos e inclui/remove usuários RELATOR neles (RN-22).
- [ ] **RF-33** — usuário em múltiplos grupos acessa a **união** dos relatórios de todos eles.
- [ ] **RF-43** — remover uma Role de relatório ou um Grupo revoga o acesso concedido por eles **sem
      afetar outros caminhos** da Cadeia. A revogação é cirúrgica.
- [ ] **RF-36** — removido o vínculo, o acesso cessa **imediatamente**, sem esperar expiração de
      sessão. Um cenário afirma isso com uma requisição feita logo após a revogação.
- [ ] **Teste obrigatório (RA-68)** — **RF-34**: o GERENTE não cria nem promove usuário a GERENTE ou
      ADMINISTRADOR, **nem por manipulação direta da requisição** (RN-26). Os endpoints do GERENTE só
      operam sobre client roles do cliente dedicado; um Perfil é realm role e está em outro espaço de
      nomes e outro endpoint. Verificação esquecida aqui é escalada de privilégio.
- [ ] A defesa é estrutural **e** codificada: a separação de espaços de nomes de ADR-0003 deixa de
      ser a única barreira, mas continua sendo a principal.
