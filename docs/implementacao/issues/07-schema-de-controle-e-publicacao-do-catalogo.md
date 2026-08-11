# 07 — Schema de controle e publicação do catálogo

**O que construir:** o schema de controle versionado e o catálogo nascendo de onde ele tem que
nascer — do código. Ao fim deste ticket, subir o módulo processador da Poupança faz aparecerem no
schema de controle o produto e os seus relatórios, com sigla, código, nome, descrição e tempo
estimado; e subir um módulo com dois relatórios de mesmo código **não sobe**. É o que as costuras
S1 e S2 pressupõem existir antes de qualquer outra coisa.

**Bloqueado por:** 01, 02, 06.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] Primeira migration Flyway com os seis conjuntos da especificação §4.2: catálogo, execução,
      artefato, auditoria, download e o elo relatório → role de relatório (RA-24).
- [ ] A Execução é **append-only por construção do schema** (RA-67): nenhum caminho atualiza status
      terminal, e "vigente" é ponteiro, não coluna de status. RN-15 vira propriedade do schema, não
      disciplina de código.
- [ ] Unicidade da execução vigente por par *data de referência + código do relatório* garantida
      **no banco** (RN-16) — não numa verificação da aplicação.
- [ ] Ao iniciar, o processador publica produto e relatórios no schema de controle (RA-58, RN-49). A
      publicação preserva o que é mutável: reiniciar o módulo **não** sobrescreve nome, descrição e
      tempo estimado já editados pela operação.
- [ ] **RF-44** — dois relatórios do mesmo produto declarando o mesmo código **impedem o módulo de
      iniciar**. É validação de inicialização, não de formulário, e é o que torna a violação de
      RN-03 impossível de persistir.
- [ ] **RF-27** — tempo estimado em segundos inteiros maior que zero, validado na inicialização
      (RN-04).
- [ ] **RN-48** — a soma dos tempos estimados dos relatórios ativos do produto não ultrapassa o teto
      de RNF-19, verificado na inicialização.
- [ ] A aplicação **nunca** cria nem apaga linha de catálogo (RN-49, RN-50) — só o código publica, e
      nada é removido fisicamente.
- [ ] O código do relatório obedece a `^[A-Z]{1,20}-\d{4}$` e é formado pela sigla do próprio
      produto (RN-02).
