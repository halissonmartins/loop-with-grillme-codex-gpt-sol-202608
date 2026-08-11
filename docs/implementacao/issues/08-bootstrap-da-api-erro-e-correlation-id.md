# 08 — Bootstrap da API: sondas, contrato de erro e Correlation ID

**O que construir:** a API respondendo pela primeira vez, e respondendo **errado** do jeito certo.
Ao fim deste ticket as sondas de saúde dizem `UP`, toda falha volta no contrato de erro
documentado, e o identificador que o usuário lê na tela é o mesmo que localiza a ocorrência nos
registros da operação.

Este ticket escreve o **primeiro cenário da costura S1**, e por isso carrega peso além do seu
tamanho: não existe teste algum no repositório de onde copiar padrão (especificação §5.4). O
cenário escrito aqui vira o molde que todas as sessões seguintes vão imitar, e precisa ser citado
no `ARCHITECTURE.md` como implementação de referência.

**Bloqueado por:** 01, 02, 04.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RA-43** — `liveness` e `readiness` do Actuator respondem `UP` na porta `8080`.
- [ ] **RF-38** — toda resposta de erro carrega momento em ISO 8601, descrição e Correlation ID
      (RN-40), documentada no OpenAPI (RA-41).
- [ ] **RF-40** — o Correlation ID exibido é o identificador de rastreamento propagado via MDC
      (RA-37), e localiza a mesma ocorrência nos registros. Um cenário prova isso de ponta a ponta,
      não por inspeção visual.
- [ ] O Correlation ID é **um só do começo ao fim da requisição** (glossário) — não se regenera por
      camada.
- [ ] SpringDoc OpenAPI publicando o contrato, **substituindo o Swagger descartável do ticket 04** —
      que é o que torna o contrato de erro acima uma implementação de um formato já acordado, e não
      um formato inventado aqui. O Swagger de fachada é aposentado no mesmo PR.
- [ ] O cenário é escrito em Gherkin, com a linguagem do glossário nos passos, em
      `src/test/resources/feature` (RA-44, RA-45), e roda contra dependências reais em contêiner
      (RA-47).
- [ ] O cenário entra por HTTP e afirma o que sai por HTTP. Não afirma que um método foi chamado nem
      que uma classe existe (especificação §5.1).
- [ ] O `ARCHITECTURE.md` aponta este cenário como a referência da costura S1.
