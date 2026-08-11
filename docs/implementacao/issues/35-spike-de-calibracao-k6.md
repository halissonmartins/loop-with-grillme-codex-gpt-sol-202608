# 35 — Spike de calibração com `k6`

**O que construir:** os números. Ao fim deste ticket os limites de PRD §10 marcados `PROVISÓRIO`
deixam de ser estimativa e passam a ser medida — e Q10 fecha. É o único spike que a arquitetura §14
ainda lista como bloqueante, mas ele **não bloqueia ticket algum**: §5.5 é explícita em que,
enquanto ele não rodar, reprovar PR contra um número que ninguém mediu é transformar chute em
portão.

**Bloqueado por:** 18.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RNF-05** — tamanho real do `JasperPrint` **desserializado**, não do arquivo em disco. É o
      número que sustenta o teto de memória.
- [ ] **RNF-06** — volume de dataset em que a apuração ainda cabe, medido contra a máquina alvo de
      RA-50: 23 GB de RAM, 4 vCPUs e ~20 GB livres em disco.
- [ ] **RNF-07, RNF-08, RNF-09** — latência p95 de exportação **por formato**, sob carga.
- [ ] **RNF-10** — teto real de exportações simultâneas, para calibrar o semáforo do ticket 17.
- [ ] **RNF-11** — navegação simultânea sob carga; navegar não é exportar, e os dois números não se
      confundem.
- [ ] Os quatro tetos são avaliados **juntos** — artefato, linhas, simultaneidade e semáforo compõem
      um único teto de memória, e afrouxar qualquer um isoladamente o quebra (especificação §7).
- [ ] Os valores medidos substituem os `PROVISÓRIO` em PRD §10, e Q10 é encerrada com o número
      observado, não com o estimado.
- [ ] Se algum número medido derrubar uma decisão de arquitetura, o achado vira ADR — não um ajuste
      silencioso na tabela.
