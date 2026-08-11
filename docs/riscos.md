# Registro de riscos técnicos

Este registro torna visíveis riscos já aceitos e validações técnicas ainda pendentes. Ele não
substitui ADRs nem decide novamente o que já foi deliberado.

| ID | Risco | Situação e encaminhamento | Decisão de origem |
|---|---|---|---|
| R-01 | A API tem capacidade técnica de criar um ADMINISTRADOR porque o service account recebe permissões administrativas amplas. | **Aceito.** A separação entre realm roles e client roles, junto da autorização da API, impede esse fluxo no produto. Reavaliar quando as permissões administrativas granulares do Keycloak deixarem de ser preview. | [ADR 0003](./adr/0003-autorizacao-hibrida-keycloak-e-schema-de-controle.md) |
| R-02 | Relatórios do mesmo Produto podem observar instantes diferentes da base quando houver retentativa. | **Aceito.** A retentativa fica limitada e a leitura bem-sucedida continua sendo a referência da janela por Produto. | [ADR 0001](./adr/0001-janela-de-leitura-unica-e-sem-cancelamento.md) |
| R-03 | Uma apuração travada pode consumir até o dobro do tempo estimado sem intervenção pela aplicação. | **Aceito.** Não há cancelamento pela aplicação; o teto do catálogo e o limite de execução do Produto mantêm o impacto conhecido. | [ADR 0001](./adr/0001-janela-de-leitura-unica-e-sem-cancelamento.md) |
| R-04 | Um Ciclo que não rodar deixa lacuna permanente na série. | **Aceito.** Não há apuração retroativa; a DAG declara `catchup=False` e o reprocessamento forçado opera somente sobre a data corrente. | [ADR 0005](./adr/0005-sem-apuracao-retroativa.md) |
| R-05 | Os limites de memória, volume e simultaneidade ainda são `PROVISÓRIO`. | **Aberto.** Medir tamanho desserializado do `JasperPrint`, dataset, latência e simultaneidade com `k6`; substituir os valores do PRD §10 e registrar ADR se algum limite invalidar uma decisão. | [ISSUE 35](./implementacao/issues/35-spike-de-calibracao-k6.md) |
| R-06 | O callback de expurgo depende de o MinIO na tag fixada emitir `s3:ObjectRemoved:Delete` quando o ILM remover um artefato. | **Conferência operacional pendente; o desenho está resolvido.** O evento já foi confirmado no código-fonte do MinIO. Assinar `--event delete`, expurgar um objeto manualmente e observar o webhook na tag fixada, no fluxo da retenção. | [RA-21](./arquitetura-inicial.md) e [ISSUE 20](./implementacao/issues/20-retencao-expurgo-e-indisponibilidade.md) |
