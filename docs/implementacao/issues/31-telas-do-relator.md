# 31 — Telas do RELATOR

**O que construir:** a tela pela qual o sistema é julgado. Ao fim deste ticket um analista entra,
navega por data → produto → relatório, escolhe o formato e recebe o arquivo — e quando não recebe,
entende **por quê** em três situações diferentes: o dado expirou, a apuração daquele dia não deu
certo, ou o sistema está ocupado demais neste instante.

**Bloqueado por:** 18, 20, 30.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RF-13** — navegação por data → produto → relatório, com a data em `dd/MM/yyyy` (RNF-15).
- [ ] **A armadilha do domínio é desfeita ativamente na interface.** O rótulo deixa explícito que a
      data de referência é o dia da **apuração**, e que o artefato de uma data carrega o movimento
      **fechado do dia anterior** (RN-07). Uma pessoa que leia o número achando que é do próprio dia
      comete um erro que o sistema não tem como detectar.
- [ ] **RF-14, RF-16** — a listagem mostra só o que a Cadeia alcança; um RELATOR pendente de vínculo
      vê a listagem vazia com a mensagem de aguardo.
- [ ] **F07, F08** — exportar e baixar nos quatro formatos, com o arquivo chegando na mesma
      interação (RN-30).
- [ ] **RF-24** — artefato expurgado responde **indisponibilidade por retenção**, com texto próprio
      (RN-39).
- [ ] **RF-20** — execução vigente fora de sucesso ou alerta responde com a sua própria mensagem
      (RN-42) — não a mesma da retenção.
- [ ] **RF-48** — recusa por limite de simultaneidade indica **repetir mais tarde** (RN-53). Três
      causas, três mensagens, nenhuma delas genérica.
- [ ] Os estados de carregando, vazio e erro vêm dos componentes canônicos do ticket 30, não são
      reinventados aqui.
- [ ] Nenhuma tela aceita data de referência como entrada (RF-53).
