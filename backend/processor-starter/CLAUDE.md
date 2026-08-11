# Starter do processador

Siga o `CLAUDE.md` da raiz.

- Toda leitura transacional deve configurar `queryTimeout`; o timeout JDBC limita cada statement e
  o limite do relatório é verificado entre chunks (RA-57).
- Todo JRXML coloca o cabeçalho de coluna em `title`; `pageHeader` e `pageFooter` contêm somente
  ornamentos descartáveis para manter o XLSX contínuo (RA-59 e ADR 0006).
