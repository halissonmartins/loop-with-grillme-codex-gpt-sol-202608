# Inativação lógica e três retenções independentes

Nada do catálogo é apagado: produto e relatório são **inativados**. E existem **três** ciclos de vida
distintos, não dois — artefato por 7 dias, **metadados de Execução para sempre**, histórico de
downloads indefinidamente.

A terceira retenção não estava declarada em documento nenhum e é obrigatória por aritmética: a
métrica primária tem janela móvel de 30 dias e o artefato vive 7. Sem a Execução sobrevivendo ao
artefato, a métrica passaria a ser calculada sobre uma série truncada — funcionando, e devolvendo
números errados no fim do mês.

## Considered Options

- **Remoção física com bloqueio dentro da janela de retenção** (a RF-42 original). Rejeitada:
  apagar um relatório em 20/08 destruiria as execuções de 01/08 a 13/08 e **mudaria retroativamente
  a métrica dos 30 dias** — o número de ontem deixaria de bater com o de hoje sem que ninguém
  tivesse feito nada errado. Métrica que muda o passado não é métrica.
- **Inativação lógica** — escolhida.

## Consequences

- RF-42 é aposentado: não há mais o que bloquear, porque a inativação não destrói nada. Isso resolve
  a questão Q2 do PRD.
- RN-05 muda de verbo, não de conteúdo: inativar um produto exige inativar os seus relatórios antes.
- **O código `SIGLA-NNNN` fica ocupado para sempre.** É correto e não é efeito colateral:
  reaproveitá-lo tornaria o histórico ambíguo, e RN-02 já declara o código imutável.
- O registro de Download guarda **cópia** dos identificadores do momento — código, nome do relatório,
  sigla, data de referência e formato. O nome do relatório é editável (RF-41): sem a cópia, um
  histórico que "sobrevive indefinidamente" exibiria o download de 2026 com o nome que o relatório
  ganhou em 2027.
- Os metadados de Execução são baratos: dez linhas por dia, ~3.650 por ano. Não há razão de custo
  para expurgá-los, e há uma razão de correção para mantê-los.
