# 26 — Produto CONTACORRENTE

**O que construir:** o produto `CONTACORRENTE` inteiro — catálogo publicado na subida, task na DAG,
dois relatórios de exemplo apurados do seu próprio schema transacional e entregues nos quatro
formatos.

**Bloqueado por:** 06, 14, 18.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] Schema transacional próprio, com dado de exemplo semeado, lido **exclusivamente** por este
      módulo (RA-10).
- [ ] **RA-08** — os dois relatórios de exemplo definidos no ticket 06, com imagens e fontes
      diferentes entre si.
- [ ] Cada relatório com o seu próprio JRXML (RA-07), na convenção de autoria do ticket 18.
- [ ] **RF-44, RF-27, RN-48** — códigos únicos dentro do produto, tempo estimado válido e soma
      dentro do teto, verificados na inicialização.
- [ ] **RA-65** — task estática do produto na DAG, no mesmo PR do módulo.
- [ ] **Teste obrigatório (RA-68)** — cabeçalho único no XLSX de **cada** relatório.
- [ ] Os quatro formatos saem corretamente, com as *font extensions* deste módulo no classpath da
      API.
- [ ] A sigla tem 12 caracteres e o código fica com 17 — é o caso mais longo do catálogo e vale
      confirmar que nada trunca o identificador no caminho do artefato nem no registro de download.
