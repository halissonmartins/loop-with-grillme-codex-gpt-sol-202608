# Catálogo seed e modelos transacionais

Este documento é a definição dos dez relatórios de exemplo. Cada Produto tem schema independente,
lido exclusivamente pelo respectivo processador. Não há JRXML neste artefato.

## Limites do seed

- Teto por relatório: `50.000` linhas (RNF-06).
- Teto da soma por Produto: `600` segundos (RNF-19).
- Caminho feliz: `1.000` linhas retornadas por consulta. O cenário de recusa usa
  `EMPRESTIMO-0502`: semear `50.001` parcelas vencidas, cada uma em contrato distinto ou com
  identificador de parcela distinto, e manter a consulta sem agregação; ela retorna exatamente
  `50.001` linhas e deve ser recusada antes da renderização.

## Poupança (`POUPANCA`, schema `poupanca`)

Tabelas: `conta_poupanca(id, agencia, numero, titular_id, saldo_atual)`, `movimentacao_poupanca(id, conta_id, ocorrido_em, tipo, valor)`.

| Código | Nome | Descrição | Consulta principal | Tempo |
|---|---|---|---|---:|
| `POUPANCA-0101` | Saldos por agência | Consolida contas e saldo atual por agência. | `SELECT agencia, count(*), sum(saldo_atual) FROM poupanca.conta_poupanca GROUP BY agencia` | 90 s |
| `POUPANCA-0102` | Movimentações diárias | Agrupa créditos e débitos fechados por dia. | `SELECT ocorrido_em::date, tipo, sum(valor) FROM poupanca.movimentacao_poupanca GROUP BY 1, 2` | 210 s |

## Cliente (`CLIENTE`, schema `cliente`)

Tabelas: `cliente(id, nome, cpf, nascimento_em, cidade)`, `endereco_cliente(id, cliente_id, uf, cidade, principal)`.

| Código | Nome | Descrição | Consulta principal | Tempo |
|---|---|---|---|---:|
| `CLIENTE-0201` | Clientes por cidade | Conta clientes cadastrados por cidade. | `SELECT cidade, count(*) FROM cliente.cliente GROUP BY cidade` | 75 s |
| `CLIENTE-0202` | Aniversariantes do mês | Lista clientes com aniversário no mês da apuração. | `SELECT nome, cpf, nascimento_em FROM cliente.cliente WHERE extract(month FROM nascimento_em)=extract(month FROM current_date)` | 120 s |

## Conta Corrente (`CONTACORRENTE`, schema `contacorrente`)

Tabelas: `conta_corrente(id, agencia, numero, cliente_id, limite)`, `lancamento_corrente(id, conta_id, ocorrido_em, natureza, valor)`.

| Código | Nome | Descrição | Consulta principal | Tempo |
|---|---|---|---|---:|
| `CONTACORRENTE-0301` | Limites por agência | Soma limites contratados nas contas da agência. | `SELECT agencia, sum(limite) FROM contacorrente.conta_corrente GROUP BY agencia` | 90 s |
| `CONTACORRENTE-0302` | Lançamentos por natureza | Consolida lançamentos por natureza financeira. | `SELECT natureza, sum(valor) FROM contacorrente.lancamento_corrente GROUP BY natureza` | 180 s |

## Consórcio (`CONSORCIO`, schema `consorcio`)

Tabelas: `grupo_consorcio(id, codigo, bem, prazo_meses)`, `cota_consorcio(id, grupo_id, cliente_id, status, credito)`. Relação: `cota_consorcio.grupo_id → grupo_consorcio.id`.

| Código | Nome | Descrição | Consulta principal | Tempo |
|---|---|---|---|---:|
| `CONSORCIO-0401` | Cotas por status | Conta cotas e crédito por status. | `SELECT status, count(*), sum(credito) FROM consorcio.cota_consorcio GROUP BY status` | 100 s |
| `CONSORCIO-0402` | Grupos e créditos | Totaliza crédito das cotas por grupo e bem. | `SELECT g.codigo, g.bem, sum(c.credito) FROM consorcio.grupo_consorcio g JOIN consorcio.cota_consorcio c ON c.grupo_id=g.id GROUP BY 1,2` | 190 s |

## Empréstimo (`EMPRESTIMO`, schema `emprestimo`)

Tabelas: `contrato_emprestimo(id, cliente_id, modalidade, valor_original, status)`, `parcela_emprestimo(id, contrato_id, vencimento_em, valor, paga_em)`. Relação: `parcela_emprestimo.contrato_id → contrato_emprestimo.id`.

| Código | Nome | Descrição | Consulta principal | Tempo |
|---|---|---|---|---:|
| `EMPRESTIMO-0501` | Contratos por modalidade | Consolida contratos por modalidade e status. | `SELECT modalidade, status, count(*), sum(valor_original) FROM emprestimo.contrato_emprestimo GROUP BY 1,2` | 110 s |
| `EMPRESTIMO-0502` | Parcelas em atraso | Lista parcelas vencidas sem pagamento. | `SELECT c.modalidade, p.vencimento_em, p.valor FROM emprestimo.parcela_emprestimo p JOIN emprestimo.contrato_emprestimo c ON c.id=p.contrato_id WHERE p.paga_em IS NULL AND p.vencimento_em < current_date` | 230 s |

Todas as somas ficam abaixo de `600` segundos. `POUPANCA-0102` é deliberadamente mais lento que
`POUPANCA-0101` para o cenário do ticket 12.

## Cobertura de riscos de conteúdo

| Produto | Imagens e fontes distintas no par | Renderer serializado |
|---|---|---|
| Poupança | `POUPANCA-0101`: fonte serifada; `POUPANCA-0102`: fonte sem serifa | `POUPANCA-0102`: barcode |
| Cliente | `CLIENTE-0201`: logomarca A; `CLIENTE-0202`: logomarca B | — |
| Conta Corrente | `CONTACORRENTE-0301`: fonte monoespaçada; `CONTACORRENTE-0302`: fonte sem serifa | — |
| Consórcio | `CONSORCIO-0401`: ícone A; `CONSORCIO-0402`: ícone B | — |
| Empréstimo | `EMPRESTIMO-0501`: fonte serifada; `EMPRESTIMO-0502`: fonte monoespaçada | — |
