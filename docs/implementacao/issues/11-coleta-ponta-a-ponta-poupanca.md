# 11 — Coleta ponta a ponta de um relatório da Poupança

**O que construir:** a primeira Coleta que funciona de verdade. Ao fim deste ticket dá para semear
o schema transacional da Poupança, disparar o job do produto e olhar os dois artefatos no
repositório e a linha de metadados no schema de controle, com início, fim, duração, status e
origem. Um relatório, um produto, o caminho inteiro.

Este ticket escreve o **primeiro cenário da costura S2** e carrega o peso de §5.4: é o molde que as
próximas sessões vão copiar.

**Bloqueado por:** 02, 06, 07.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] O starter do processador (RA-03) concentra o que é comum: leitura paginada, contagem prévia,
      renderização, gravação de artefato e registro de metadados. O módulo do produto traz o JRXML e
      a consulta definidos no ticket 06, não a mecânica.
- [ ] **RA-10** — o módulo lê **exclusivamente** o schema transacional do seu próprio produto, numa
      única Janela de leitura por ciclo (RN-44). É a única fronteira de leitura dessas bases.
- [ ] **RA-64** — a contagem de linhas acontece **antes** de apurar, e o resultado é comparado ao
      teto de RNF-06.
- [ ] **RA-57** — **todo** statement de leitura declara `queryTimeout`. Sem ele o limite do
      relatório do ticket 12 não existe, e ninguém percebe: o sistema volta silenciosamente a ter um
      só limite.
- [ ] **RA-16, RA-17** — a execução bem-sucedida produz **dois arquivos irmãos**: o `JasperPrint`
      serializado e o dataset bruto comprimido com separador `;`. O dataset **não** passa pelo motor
      de relatório.
- [ ] **RA-19** — o caminho é *data de referência → **sigla** do produto → código do relatório*. A
      sigla, nunca o nome: o nome é editável e o caminho de um artefato jamais muda.
- [ ] **RA-11** — os artefatos são gravados **antes** dos metadados de conclusão. A ordem inversa
      produziria uma execução em sucesso sem artefato, exatamente o estado que RN-42 supõe
      impossível.
- [ ] **RF-02** — a Execução registra data de referência, início, fim, duração, status e origem
      (RN-07, RN-09, RN-46).
- [ ] **RN-47** — o tempo estimado vigente é **copiado para dentro** da Execução no disparo. Editar
      o catálogo depois não reclassifica esta execução.
- [ ] **RF-01** — execução sem falhas e dentro do tempo estimado termina em
      `processado com sucesso`.
- [ ] O cenário é escrito em Gherkin com a linguagem do glossário, entra pela invocação do job e
      afirma artefato no repositório e linha de metadados — não chamadas internas. O
      `ARCHITECTURE.md` o aponta como referência da costura S2.
