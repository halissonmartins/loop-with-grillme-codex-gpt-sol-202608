# 24 — Usuários administrativos e senha

**O que construir:** o time que opera a plataforma, e a autonomia de cada um sobre a própria senha.
Ao fim deste ticket o ADMINISTRADOR monta e desmonta o quadro de ADMINISTRADOR e GERENTE, ambos os
perfis removem um RELATOR que saiu da área, e qualquer usuário troca ou recupera a própria senha sem
depender de ninguém.

**Bloqueado por:** 10, 15.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **F15** — o ADMINISTRADOR cadastra e remove usuários ADMINISTRADOR e GERENTE.
- [ ] **RF-35** — ADMINISTRADOR **e** GERENTE conseguem remover um usuário RELATOR (matriz do PRD
      §3.2).
- [ ] **RF-37** — qualquer usuário, de qualquer perfil, troca a própria senha (RN-29).
- [ ] **RF-37, segunda metade** — recuperação de senha por e-mail, entregue pelo SMTP local do
      ambiente (RA-35). Um cenário afirma a chegada da mensagem, não só o disparo.
- [ ] A troca de senha usa o console de conta exposto seletivamente no ticket 10 (RA-34) — o console
      administrativo continua fora do alcance público.
- [ ] Um usuário tem **exatamente um perfil**, e o conjunto de perfis continua fechado e definido em
      código: nada aqui cria perfil novo.
- [ ] Está registrado como fora de escopo deliberado, não como esquecimento: **MFA** e **rotação
      obrigatória da senha inicial do ADMINISTRADOR** (PRD §5).
