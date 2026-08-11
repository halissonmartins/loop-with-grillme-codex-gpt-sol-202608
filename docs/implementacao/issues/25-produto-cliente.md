# 25 — Produto CLIENTE

**O que construir:** o segundo produto, inteiro. Ao fim deste ticket o módulo `CLIENTE` publica o
seu catálogo ao subir, tem a sua task na DAG, apura os seus **dois** relatórios de exemplo a partir
do seu próprio schema transacional e entrega os quatro formatos. É o primeiro teste real de que o
starter do processador é reutilizável e não um molde feito sob medida para a Poupança.

**Bloqueado por:** 06, 14, 18.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] Schema transacional próprio, com dado de exemplo semeado, lido **exclusivamente** por este
      módulo (RA-10).
- [ ] **RA-08** — os dois relatórios de exemplo definidos no ticket 06, **com imagens e fontes
      diferentes entre si**: é o que
      exercita na prática os trade-offs da serialização declarados na arquitetura §12.
- [ ] Cada relatório tem o **seu próprio JRXML** versionado no repositório (RA-07), escrito na
      convenção de autoria do ticket 18 — cabeçalho de coluna na banda `title`.
- [ ] **RF-44, RF-27, RN-48** — códigos únicos dentro do produto, tempo estimado válido e soma
      dentro do teto, todos verificados na inicialização.
- [ ] **RA-65** — task estática do produto na DAG, no mesmo PR do módulo. O mono repositório existe
      para que os dois não divirjam.
- [ ] **Teste obrigatório (RA-68)** — cenário de cabeçalho único no XLSX **para cada um dos dois
      relatórios**.
- [ ] Os quatro formatos saem corretamente, e as *font extensions* deste módulo estão no classpath
      da API que exporta (arquitetura §12).
- [ ] O produto entra na apuração do Ciclo sem alteração no starter — se precisar mexer nele, o
      starter estava acoplado à Poupança e isso é um achado a registrar.
