# 19 — Histórico de downloads

**O que construir:** a resposta à pergunta de auditoria sem abrir o banco. Ao fim deste ticket o
ADMINISTRADOR consulta quem levou qual dado e quando, e o registro continua legível mesmo depois de
o artefato ter sido expurgado, o nome do relatório ter mudado e o relatório ter sido inativado.

**Bloqueado por:** 16.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RF-23** — o ADMINISTRADOR consulta o histórico de downloads (F09). Nenhum outro perfil
      consulta.
- [ ] **RF-51** — o histórico exibe os identificadores **como estavam no momento do download**,
      mesmo após edição de nome ou inativação (RN-35, RN-38, RN-50).
- [ ] **RN-38, RA-22** — o histórico **não** é expurgado junto com os artefatos: são ciclos de vida
      independentes, e a retenção é indefinida (RNF-13).
- [ ] Um cenário prova a sobrevivência ao expurgo e à inativação de verdade — edita o nome do
      relatório, inativa-o, e afirma que a linha antiga continua exibindo o nome antigo.
- [ ] As três retenções ficam distinguíveis no `ARCHITECTURE.md`: artefato por 7 dias, Execução
      nunca, download indefinidamente (RA-62, ADR-0007).
