# Fluxos principais

Estes fluxos fixam a linguagem e os estados que as telas posteriores devem implementar. A decisão
vinda do protótipo da ISSUE 04 é que a **Data de referência é informação exibida**, nunca campo ou
parâmetro de entrada; assim a interface atende RF-53 e explica RN-07.

## 1. Encontrar e baixar um relatório

**Por que este fluxo:** é o caminho central do RELATOR e concentra a armadilha da Data de referência.

1. A pessoa abre os relatórios disponíveis e escolhe o Produto.
2. A interface lista os relatórios disponíveis daquele Produto, cada um com Código, nome e Data de
   referência em `dd/MM/yyyy`.
3. Após escolher o relatório, a interface mostra: “Referência: 11/08/2026 · movimento fechado:
   10/08/2026”. A data não é editável nem enviada como parâmetro.
4. A pessoa escolhe o formato e solicita o download.
5. Em sucesso, o arquivo é baixado na mesma requisição e a interface confirma o formato entregue.

**Estados de erro do download**

| Situação | Mensagem em pt-BR | Próxima ação |
|---|---|---|
| Artefato expurgado | “Indisponível por retenção: o artefato desta data foi expurgado.” | Voltar à lista e escolher outro relatório disponível. |
| Execução sem sucesso ou alerta | “Exportação indisponível: a execução vigente ainda não tem artefato válido.” | Aguardar a apuração ou consultar a operação. |
| Limite de simultaneidade | “Exportação recusada temporariamente: aguarde e tente novamente.” | Repetir mais tarde; não há fila. |

## 2. RELATOR pendente de vínculo

**Por que este fluxo:** evita que uma lista vazia pareça falha do sistema quando não existe vínculo
na Cadeia de permissão.

1. O RELATOR entra pela primeira vez.
2. A listagem retorna vazia.
3. A interface informa: “Você ainda não possui relatórios liberados. Aguarde o vínculo a um grupo.”
4. Não mostra controles administrativos nem sugere que o usuário escolha permissões.

## 3. GERENTE concede acesso

**Por que este fluxo:** as quatro entidades e suas três relações N:N são difíceis de enxergar apenas por prosa.

1. O GERENTE vincula um relatório a uma Role de relatório.
2. Vincula a Role de relatório a um ou mais grupos.
3. Vincula a pessoa a um ou mais grupos.
4. A interface exibe a união dos relatórios alcançados por todos os grupos da pessoa.
5. A alteração confirmada informa os vínculos afetados; nenhum fluxo permite promover perfis.

## 4. Erro com diagnóstico

**Por que este fluxo:** toda falha observável precisa permitir que a operação encontre a ocorrência.

1. Uma operação retorna erro.
2. A tela mostra momento em ISO 8601, descrição em pt-BR e Correlation ID.
3. A pessoa pode copiar o JSON estruturado para anexar em chamado.
4. O estado não substitui as mensagens específicas de retenção, execução ou simultaneidade.

## Decisões descartadas

- Não há dropdown nem parâmetro de Data de referência: ele contrariaria RF-53.
- Os três erros de exportação não usam uma mensagem genérica.
- A Cadeia de permissão não é reduzida a uma relação de um para um; a união entre grupos permanece
  visível.
