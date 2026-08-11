# Tickets de implementação

O trabalho do MVP fatiado em **tracer bullets**, derivado de [`../especificacao.md`](../especificacao.md).

Tracker markdown local, versionado em `docs/` porque é documentação de primeira classe, não
rascunho descartável.

| Documento | Papel |
|---|---|
| [`../prd.md`](../prd.md) | o **quê** e o **porquê** — `RN-NN`, `RF-NN`, `RNF-NN` |
| [`../arquitetura-inicial.md`](../arquitetura-inicial.md) | o **como** — `RA-NN` |
| [`../especificacao.md`](../especificacao.md) | a **especificação** do MVP: histórias, decisões e as três costuras |
| `docs/implementacao/` | o **trabalho**, fatiado |

Cada ticket é uma fatia **vertical**: um caminho estreito mas completo por todas as camadas,
demonstrável sozinho. Não existe ticket "o schema" ou "a API" — existem tickets que fazem um
comportamento funcionar de ponta a ponta.

## Formato

```markdown
# NN — Título

**O que construir:** o comportamento que este ticket faz funcionar, pela perspectiva de quem usa.
**Bloqueado por:** os números que precisam terminar antes, ou "nada — pode começar imediatamente".
**Status:** ready-for-agent

**Critérios de aceitação**
- [ ] ...
```

Os critérios referenciam o PRD e a arquitetura pelo identificador. Um `RF-NN` ou `RA-NN` no critério
é onde o teste vai buscar o que afirmar.

## Como trabalhar

Trabalhe a **fronteira**: qualquer ticket cujos bloqueadores estejam todos concluídos. A numeração
está em ordem de dependência — os bloqueadores de um ticket sempre têm número menor —, mas **não é
uma fila**. Vários tickets ficam livres ao mesmo tempo.

Quatro frentes ficam livres cedo e correm em paralelo: os artefatos de arquitetura (03), os
artefatos descartáveis (04) e o catálogo seed (06), os três sem bloqueador algum; a Coleta a partir
de 07; e a plataforma a partir de 08.

Quatro tickets ficam livres de uma vez quando 18 fechar: os produtos 25 a 28 são independentes
entre si.

## Onde estão os três cenários canônicos

Não existe teste algum no repositório de onde copiar padrão (especificação §5.4). O **primeiro**
cenário escrito em cada costura vira o molde de todas as sessões seguintes, e por isso os três
carregam peso além do seu tamanho:

| Costura | Ticket | Entra por |
|---|---|---|
| **S1** | [08](issues/08-bootstrap-da-api-erro-e-correlation-id.md) | HTTP na API REST |
| **S2** | [11](issues/11-coleta-ponta-a-ponta-poupanca.md) | invocação do job de coleta do produto |
| **S3** | [14](issues/14-dag-reserva-pool-e-callback-de-falha.md) | DAG, com as tasks de produto em dublê |

Os três precisam ser citados nominalmente no `ARCHITECTURE.md` como implementação de referência.

## Os quatro testes obrigatórios por natureza de risco

São os que ninguém escreve espontaneamente (RA-68), e cada um tem dono:

| Teste | Ticket |
|---|---|
| ADMINISTRADOR acessa relatório fora de qualquer Cadeia de permissão | [15](issues/15-cadeia-de-permissao-e-listagem.md) |
| Cabeçalho aparece exatamente uma vez no XLSX de **cada** relatório | [18](issues/18-exportacao-xlsx-continua.md), herdado por 25–28 |
| GERENTE não promove ninguém, nem manipulando a requisição | [23](issues/23-gestao-de-acesso-pelo-gerente.md) |
| Execução reservada que nunca iniciou é encerrada pelo callback | [14](issues/14-dag-reserva-pool-e-callback-de-falha.md) |

## Os tickets

**Fundações**

| # | Ticket | Bloqueado por |
|---|---|---|
| 01 | [Fundações do mono repositório e CI verde](issues/01-fundacoes-do-monorepo-e-ci.md) | — |
| 02 | [Ambiente local em Docker Compose](issues/02-ambiente-local-docker-compose.md) | 01 |
| 03 | [Artefatos de arquitetura faltantes: C4 e riscos](issues/03-c4-e-riscos.md) | — |
| 04 | [Artefatos descartáveis: protótipo dos fluxos e Swagger](issues/04-artefatos-descartaveis-prototipo-e-swagger.md) | — |
| 05 | [Fluxos principais e design system](issues/05-fluxos-e-design-system.md) | 04 |
| 06 | [Catálogo seed: os dez relatórios de exemplo e seus modelos de dados](issues/06-catalogo-seed-relatorios-e-modelos-de-dados.md) | — |

**Esqueleto vertical e costuras**

| # | Ticket | Bloqueado por |
|---|---|---|
| 07 | [Schema de controle e publicação do catálogo](issues/07-schema-de-controle-e-publicacao-do-catalogo.md) | 01, 02, 06 |
| 08 | [Bootstrap da API: sondas, contrato de erro e Correlation ID](issues/08-bootstrap-da-api-erro-e-correlation-id.md) | 01, 02, 04 |
| 09 | [Caminho único de telemetria](issues/09-caminho-unico-de-telemetria.md) | 02, 08 |
| 10 | [Identidade: realm, perfis, cliente de roles e JWT](issues/10-identidade-realm-perfis-e-jwt.md) | 02, 08 |

**Coleta**

| # | Ticket | Bloqueado por |
|---|---|---|
| 11 | [Coleta ponta a ponta de um relatório da Poupança](issues/11-coleta-ponta-a-ponta-poupanca.md) | 02, 06, 07 |
| 12 | [Desfechos não-felizes da Coleta](issues/12-desfechos-nao-felizes-da-coleta.md) | 11 |
| 13 | [Retentativa, vigência e unicidade](issues/13-retentativa-vigencia-e-unicidade.md) | 12 |
| 14 | [DAG: reserva do ciclo, pool, `catchup=False` e callback de falha](issues/14-dag-reserva-pool-e-callback-de-falha.md) | 02, 07 |
| 21 | [Reprocessamento forçado ponta a ponta](issues/21-reprocessamento-forcado.md) | 13, 14, 15 |

**Exportação e entrega**

| # | Ticket | Bloqueado por |
|---|---|---|
| 15 | [Cadeia de permissão e listagem](issues/15-cadeia-de-permissao-e-listagem.md) | 10, 11 |
| 16 | [Exportação CSV e registro de Download](issues/16-exportacao-csv-e-registro-de-download.md) | 15 |
| 17 | [Exportação PDF e DOCX, com semáforo](issues/17-exportacao-pdf-docx-e-semaforo.md) | 16 |
| 18 | [Exportação XLSX contínua e o teste de cabeçalho único](issues/18-exportacao-xlsx-continua.md) | 17 |
| 19 | [Histórico de downloads](issues/19-historico-de-downloads.md) | 16 |
| 20 | [Retenção, expurgo e indisponibilidade explícita](issues/20-retencao-expurgo-e-indisponibilidade.md) | 16 |

**Catálogo e acesso**

| # | Ticket | Bloqueado por |
|---|---|---|
| 22 | [Catálogo administrável](issues/22-catalogo-administravel.md) | 07, 15 |
| 23 | [Gestão de acesso pelo GERENTE](issues/23-gestao-de-acesso-pelo-gerente.md) | 10, 15 |
| 24 | [Usuários administrativos e senha](issues/24-usuarios-administrativos-e-senha.md) | 10, 15 |

**Demais produtos e medição**

| # | Ticket | Bloqueado por |
|---|---|---|
| 25 | [Produto CLIENTE](issues/25-produto-cliente.md) | 06, 14, 18 |
| 26 | [Produto CONTACORRENTE](issues/26-produto-contacorrente.md) | 06, 14, 18 |
| 27 | [Produto CONSORCIO](issues/27-produto-consorcio.md) | 06, 14, 18 |
| 28 | [Produto EMPRESTIMO](issues/28-produto-emprestimo.md) | 06, 14, 18 |
| 29 | [Métricas de PRD §6 e painéis](issues/29-metricas-e-paineis.md) | 09, 14, 17 |

**Frontend e verificação**

| # | Ticket | Bloqueado por |
|---|---|---|
| 30 | [Shell Angular, OIDC e componentes canônicos](issues/30-shell-angular-oidc-e-componentes-canonicos.md) | 05, 10 |
| 31 | [Telas do RELATOR](issues/31-telas-do-relator.md) | 18, 20, 30 |
| 32 | [Telas do GERENTE](issues/32-telas-do-gerente.md) | 23, 30 |
| 33 | [Telas do ADMINISTRADOR](issues/33-telas-do-administrador.md) | 19, 21, 22, 24, 30 |
| 34 | [E2E: Newman + psql e fluxos críticos no navegador](issues/34-e2e-newman-e-playwright.md) | 31, 33 |
| 35 | [Spike de calibração com `k6`](issues/35-spike-de-calibracao-k6.md) | 18 |

## Cinco arestas que não são acidente

- **04 bloqueia 05 e 08, e é o dono dos dois artefatos descartáveis.** O protótipo vem antes dos
  fluxos escritos e dos tokens, não depois — a ordem do guia é divergir → prototipar → validar →
  convergir. O Swagger de fachada mora no mesmo ticket porque é a outra metade da mesma pergunta: o
  protótipo mostra as telas que o contrato alimenta, o contrato mostra o que a tela pode pedir, e
  separá-los é o que os faz divergir.

  A aresta **04 → 08** tem um preço declarado: põe a costura S1 atrás do único portão de **aceite
  humano** do tracker — percorrer os dois fluxos e explicar cada tela sem hesitar. É deliberado. E2
  do guia manda desenhar o contrato antes de implementá-lo, e um 08 que inventa o formato do erro
  transforma o Swagger do 04 em documento morto no dia em que nasce.
- **05 bloqueia 30.** O guia trata `fluxos.md` e `design-system.md` como pré-requisito de E3, e a
  especificação §6 repete. Sem eles, a primeira tela nasce fora do padrão.
- **06 bloqueia 07, 11 e 25–28.** Os dez relatórios de exemplo e os modelos de dados eram pendência
  declarada na arquitetura §14 e na especificação §6, e ao mesmo tempo insumo de tickets marcados
  como prontos. Definir vem antes de publicar catálogo e antes de apurar — senão a primeira sessão
  inventa os dez, e a invenção vira fato consumado no molde da costura S2.
- **18 bloqueia os quatro produtos.** A convenção de autoria do JRXML precisa existir e estar testada
  **antes** de alguém escrever mais oito relatórios — senão ela apodrece exatamente como ADR-0006
  prevê.
- **35 não bloqueia ninguém.** Enquanto o spike não rodar, os números `PROVISÓRIO` de PRD §10 são
  alvo de calibração e **não** critério de reprovação de PR (especificação §5.5).
