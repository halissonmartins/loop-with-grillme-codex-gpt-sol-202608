# Arquitetura

## Visão geral

O sistema é um mono repositório com versão única. O Airflow reserva o ciclo e dispara um contêiner
Spring Batch por Produto; cada processador lê seu schema transacional uma única vez, grava os
artefatos e registra metadados no schema de controle. A API entrega catálogo e exportações ao
frontend Angular sem acessar schemas transacionais.

## Estrutura pretendida

- `backend/common`: tipos e capacidades realmente compartilhados.
- `backend/processor-starter`: leitura paginada, renderização, persistência e metadados comuns.
- `backend/processors/<produto>`: modelos JRXML e jobs próprios de cada Produto.
- `backend/api`: REST, autorização, catálogo, histórico e exportações.
- `frontend`: interface Angular integrada somente à API.
- `orchestration`: DAGs e callbacks do Airflow, introduzidos na fatia de coleta.

## Invariantes

- Toda DAG declara `catchup=False` explicitamente.
- Todo statement de leitura transacional configura `queryTimeout`, além do limite do relatório.
- Todo JRXML mantém cabeçalho de coluna em `title` e reserva `pageHeader` e `pageFooter` a ornamento.
- A Coleta escreve catálogo, metadados de execução e artefatos no schema de controle; o Airflow
  escreve reservas e encerramentos anômalos; a API escreve atributos editáveis, auditoria,
  downloads e marcas de expurgo. Somente a Coleta lê schemas transacionais.
- Cada Produto respeita seu teto de processamento entre chunks.
- Toda exportação passa pelo semáforo global de simultaneidade da API.

As decisões completas e suas consequências permanecem em `docs/arquitetura-inicial.md` e
`docs/adr/`; este arquivo apenas mapeia onde elas se materializam no código.

## Cenários de referência

- S1 — `backend/api/src/test/resources/feature/diagnostico-da-api.feature` verifica pela HTTP da
  API as sondas de saúde, o contrato de erro e a localização de uma ocorrência pelo Correlation ID.
