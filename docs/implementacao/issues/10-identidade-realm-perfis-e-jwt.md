# 10 — Identidade: realm, perfis, cliente de roles e JWT

**O que construir:** entrar e sair da aplicação, com a separação estrutural que sustenta RN-26. Ao
fim deste ticket existe um ADMINISTRADOR desde a subida do ambiente, um analista consegue se
autocadastrar sozinho pela página pública, e a API só aceita requisição com token válido — com o
perfil vindo de um espaço de nomes e as roles de relatório de outro.

**Bloqueado por:** 02, 08.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] Realm provisionado na subida: os três **Perfis como realm roles** — conjunto fechado, criado
      na inicialização e nunca pela aplicação (RA-61).
- [ ] Cliente dedicado `relatorios` para as **Roles de relatório como client roles** — conjunto
      aberto. A separação realm role vs. client role é o que torna RN-26 estrutural, e não uma
      verificação que se possa esquecer de escrever (ADR-0003).
- [ ] **RA-32** — usuário ADMINISTRADOR criado automaticamente na subida, com senha vinda de
      variável de ambiente.
- [ ] **RF-29, RF-30** — a página pública de registro cria **exclusivamente** RELATOR, sempre
      *pendente de vínculo*, sem escolha de perfil (RN-27). **Não há grupo padrão** (RF-55).
- [ ] **RA-34** — Traefik expõe seletivamente apenas o console de conta e a página de registro. O
      console administrativo **não** é exposto.
- [ ] A API é resource server: autorização por JWT entre frontend e API (RA-30), e requisição sem
      token ou com token inválido é recusada no contrato de erro do ticket 08.
- [ ] **F12** — entrar e sair da aplicação funciona ponta a ponta.
- [ ] O risco aceito de ADR-0003 está registrado no `ARCHITECTURE.md`: o service account da API
      recebe permissões administrativas grossas porque restringi-lo a um único cliente depende de
      recurso em *preview*. O que impede a criação de um ADMINISTRADOR é o nosso código e a
      separação de espaços de nomes — **não** conserte isso por conta própria.
