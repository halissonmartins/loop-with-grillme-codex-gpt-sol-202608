# 22 — Catálogo administrável

**O que construir:** o que a aplicação pode mexer no catálogo — que é pouco, de propósito. Ao fim
deste ticket o ADMINISTRADOR corrige o nome de um produto, ajusta nome, descrição e tempo estimado
de um relatório, e inativa o que saiu de uso. O que ele **não** consegue é criar, apagar, ou mexer
em identificador.

**Bloqueado por:** 07, 15.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RF-41** — nome do produto e nome, descrição e tempo estimado do relatório são editáveis;
      **sigla e código não** (RN-01, RN-02). São imutáveis porque o caminho do artefato e o histórico
      dependem deles.
- [ ] **RF-27** — o tempo estimado editado continua sendo segundos inteiros maiores que zero
      (RN-04).
- [ ] **RF-47** — a edição do tempo estimado é **recusada** se fizer a soma do produto ultrapassar o
      teto de RNF-19 (RN-48). É a única forma de a janela do ciclo ser promessa e não desejo.
- [ ] **RN-47** — editar o tempo estimado **não** reclassifica execução passada. Um cenário prova
      isso ponta a ponta: apura, edita o tempo, e afirma que o status da execução antiga não mudou.
- [ ] **RF-50** — a inativação de um produto é **recusada** enquanto houver relatório ativo nele
      (RN-05).
- [ ] **RF-54** — relatório e produto inativados **desaparecem da listagem** e permanecem
      referenciáveis por execuções, auditoria e downloads (RN-50). Nada é removido fisicamente.
- [ ] **RN-49** — não existe endpoint de criar nem de apagar produto ou relatório. Um cenário afirma
      a ausência: uma sigla sem módulo, sem base e sem JRXML não pode existir (ADR-0002).
