# language: pt
Funcionalidade: Diagnóstico da API
  Para acompanhar a disponibilidade e diagnosticar falhas
  Como pessoa que opera o sistema
  Quero receber sondas de saúde e um Correlation ID consistente

  Cenário: Falha da API pode ser localizada nos registros da operação
    Quando as sondas de saúde da API são consultadas
    Então ambas indicam que a API está disponível
    Quando uma solicitação não encontra um recurso da API
    Então a resposta de erro informa momento, descrição e Correlation ID
    E o Correlation ID da resposta localiza a mesma ocorrência nos registros da operação
    E o contrato OpenAPI documenta a resposta de erro
