# 14 — DAG: reserva do ciclo, pool, `catchup=False` e callback de falha

**O que construir:** o orquestrador. Ao fim deste ticket o Ciclo dispara sozinho às 03h00, reserva
uma Execução para cada relatório ativo **antes** de qualquer apuração começar, sobe os produtos em
ondas de dois, e — quando um contêiner morre — encerra como erro tudo o que ficou aberto daquele
produto, inclusive o que nunca chegou a iniciar.

Este ticket escreve o **primeiro cenário da costura S3**, a mais cara e a mais estreita: só entra
nela o que é genuinamente do orquestrador. Nenhuma regra de exportação, catálogo ou acesso encosta
aqui. As tasks de produto entram como dublê.

**Bloqueado por:** 02, 07.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RF-45** — a primeira task lê o catálogo em tempo de execução e grava uma Execução por
      relatório ativo, com data/hora de início **nula** (RA-54). Nenhum contêiner sobe antes disso.
      Sem a reserva, um relatório que nunca rodou some do denominador da métrica em vez de aparecer
      como falha.
- [ ] **RA-65** — **uma task por produto, estática**, espelhando os módulos. A lista de produtos é
      código porque o produto *é* código; a lista de relatórios é dado, e por isso a reserva
      consulta o catálogo.
- [ ] **RNF-18, RA-55** — as tasks de produto correm em **pool de 2**.
- [ ] **RA-56** — a DAG declara **`catchup=False` explicitamente**. Sem isso, subir a DAG com
      `start_date` no passado dispara uma run por dia perdido, cada uma carimbando data antiga com o
      dado de hoje — a corrupção que RN-54 proíbe, entrando por omissão.
- [ ] **RNF-20** — agendada às **03h00**, diariamente, em `America/Sao_Paulo` (RN-07).
- [ ] **RF-53** — a DAG **não** aceita data de referência como parâmetro. A data é derivada do
      disparo (RN-54, ADR-0005).
- [ ] **RA-57** — `execution_timeout` da task no dobro da soma dos tempos estimados do produto, com
      folga. É interruptor de emergência, não o limite do relatório do ticket 12 — os dois não são o
      mesmo prazo implementado duas vezes.
- [ ] **RF-06** — o callback de falha encerra como `processado com erro` **todas** as execuções
      abertas daquele produto (RN-10, RA-14).
- [ ] **Teste obrigatório (RA-68)** — o callback alcança também **a execução reservada que nunca
      chegou a iniciar**, distinguível pelo início nulo. É o caso que a implementação ingênua deixa
      passar, e é o que sustenta a métrica "presas em `em processamento` = 0".
- [ ] O `ARCHITECTURE.md` aponta este cenário como referência da costura S3, e registra o custo
      declarado em §5.2: RN-45 e RN-10 vivem em Python, fora do alcance do Gherkin dos módulos Java.
