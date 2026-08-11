# XLSX contínuo a partir do JasperPrint, com convenção de autoria do JRXML

O XLSX continua saindo do `.jrprint` já renderizado, e não do dataset bruto. Cumprir RN-33 — planilha
contínua, sem cabeçalho e rodapé repetidos — **não é configuração de exportação**: é uma convenção
que todo modelo de relatório do projeto precisa seguir.

O fato técnico que força isso: o atributo `ignorePagination` do JasperReports é aplicado **no
preenchimento**, não na exportação, e `pageHeader`/`pageFooter` já estão gravados página a página
dentro do `.jrprint`. Reconstruir o print sem paginação exigiria preencher de novo — o que fere a
leitura única (RN-44) e a proibição de a exportação tocar a base (RN-31). O filtro de elementos por
banda de origem, disponível desde o JasperReports 2.0.2, resolve a repetição, mas é tudo-ou-nada:
não existe "só a primeira ocorrência".

## Considered Options

- **Dois `.jrprint` por execução**, um paginado e um não. Rejeitada: o segundo preenchimento teria de
  ser alimentado por um dataset em memória, o que dobra o pior caso de heap da Coleta.
- **XLSX gerado a partir do `.csv.gz`, em streaming.** Tecnicamente superior — memória limitada por
  buffer, sem convenção alguma para apodrecer. Rejeitada porque o XLSX perderia **subtotais e totais
  de grupo**, que o motor calcula no preenchimento e não existem no dataset bruto: o mesmo relatório
  exibiria conjuntos de linhas diferentes em PDF e em XLSX.
- **XLSX do `.jrprint` com convenção de autoria** — escolhida.

## Consequences

- **Invariante de autoria:** todo JRXML coloca o cabeçalho de coluna na banda `title`, renderizada
  uma única vez, e mantém em `pageHeader`/`pageFooter` apenas ornamento descartável.
- A exportação XLSX exclui essas bandas por origem, com `onePagePerSheet(false)` e
  `removeEmptySpaceBetweenRows(true)`.
- **Cada relatório tem teste** que exporta em XLSX e afirma que o cabeçalho aparece exatamente uma
  vez (RF-21). Sem esse teste a convenção apodrece em silêncio no primeiro relatório escrito por
  quem não leu este arquivo — e o defeito só aparece na mão do usuário.
- O XLSX permanece no caminho do `.jrprint`, portanto sujeito ao teto de memória da exportação: RNF-05,
  RNF-06 e o semáforo de RA-60 continuam sendo um único teto conjunto.
