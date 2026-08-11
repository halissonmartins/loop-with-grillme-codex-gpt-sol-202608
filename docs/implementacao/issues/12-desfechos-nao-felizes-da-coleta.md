# 12 — Desfechos não-felizes da Coleta

**O que construir:** os outros três finais possíveis de uma apuração. Ao fim deste ticket, um
relatório lento termina em alerta com artefato utilizável, um relatório que falhou termina em erro
mesmo tendo sido lento, um relatório travado é abortado no dobro do seu tempo estimado **sem
derrubar os vizinhos**, e um dataset grande demais é recusado na Coleta em vez de estourar a memória
da exportação dias depois.

**Bloqueado por:** 11.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RF-03** — execução concluída **sem nenhuma falha** cuja duração ultrapassou o tempo estimado
      registrado na própria execução termina em `processado com alerta` (RN-11). O artefato é válido
      e exportável; o alerta é degradação de desempenho, não de conteúdo.
- [ ] **RF-04** — qualquer falha registrada resulta em `processado com erro`, independentemente da
      duração. **O erro sempre prevalece sobre o alerta** (RN-12, D02).
- [ ] **RF-05** — o relatório que atinge **o dobro** do seu tempo estimado é abortado e encerrado
      como `processado com erro` (RN-13). O limite é verificado **entre chunks**.
- [ ] **RF-05, segunda metade** — abortar um relatório por tempo **não interrompe** os demais
      relatórios do mesmo produto. Um cenário afirma isso com dois relatórios, um lento e um são: o
      são conclui. É o caso que a implementação ingênua derruba junto.
- [ ] **RF-49** — dataset acima do teto de RNF-06 é recusado, encerrando a execução como
      `processado com erro` **com motivo explícito** (RN-52). Não é falha de memória, é recusa.
- [ ] Os quatro status permanecem quatro. Nada aqui inventa um quinto.
- [ ] Uma execução em status terminal **não muda mais de status** em nenhum destes caminhos (RN-15).
