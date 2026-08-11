# Janela de leitura única por produto, e sem cancelamento de execução

A base transacional de cada produto só pode ser aberta pela Coleta, numa janela por produto por
dia. Como a unidade de orquestração passou a ser o **produto** e a unidade de negócio continuou
sendo o **relatório**, o cancelamento de uma execução isolada deixou de ser implementável — e foi
**removido do escopo** em vez de ser reescrito para operar sobre o produto inteiro.

## Considered Options

- **Leitura como fronteira** — apenas a Coleta lê a base, sem limite de consultas. Permitiria um
  contêiner por relatório e manteria RN-13/RN-14 no nível do relatório. Rejeitada: afrouxa a
  premissa fundadora do sistema.
- **Leitura literal, com o cancelamento reescrito para o produto** — o ADMINISTRADOR cancelaria a
  apuração inteira de Poupança. Rejeitada: uma funcionalidade que interrompe cinco relatórios para
  parar um não é a funcionalidade que foi pedida.
- **Leitura literal, sem cancelamento** — escolhida.

## Consequences

- O único interruptor de uma apuração passa a ser o tempo (RN-13). **Risco aceito:** uma apuração
  travada consome até o dobro do tempo estimado sem que ninguém possa intervir pela aplicação. A
  mitigação é o teto de RN-48, que torna esse "dobro" um número limitado e conhecido, e não uma
  variável livre.
- Derrubar a task no Airflow continua disponível como ação de operação, fora da aplicação. O efeito
  é `processado com erro` (RA-14), não um status próprio.
- O status `cancelado` deixa de existir: são quatro status, um não-terminal. Isto **reverte a
  decisão D06** da revisão anterior do PRD.
- "Uma vez" passa a significar **uma leitura bem-sucedida por relatório**: a retentativa reabre a
  janela e relê apenas o que não concluiu (RN-44). **Preço aceito:** dois relatórios do mesmo
  produto podem enxergar instantes diferentes da base quando um deles vem de retentativa.
