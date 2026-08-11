# Sem apuração retroativa

A Data de referência é sempre derivada do disparo do ciclo e **nunca é informada** — nem pela
aplicação, nem pelo orquestrador, nem pelo reprocessamento forçado.

O motivo é que apuração retroativa só produz dado correto se **toda** base transacional for
temporal. Se a base de Poupança guarda o saldo atual e não o histórico, rodar hoje uma apuração
carimbada como 05/08 lê os números de hoje e os grava sob o rótulo de anteontem: o arquivo existe, o
status é `processado com sucesso`, o número está errado, e **nenhuma métrica deste sistema consegue
detectar isso**. É a única falha do desenho que seria completamente silenciosa.

## Considered Options

- **Retroatividade condicionada ao relatório**, com um atributo *reconstruível por data* no
  catálogo. Rejeitada por ora — é **aditiva**, então adotá-la depois é acréscimo e não reescrita, e
  não há razão para pagar por ela agora.
- **Retroatividade livre.** Rejeitada: é a corrupção silenciosa descrita acima, sem antídoto.

## Consequences

- **`catchup=False` é declarado explicitamente na DAG.** Sem isso, subir a DAG com `start_date` no
  passado dispara uma run por dia perdido, cada uma carimbando data antiga com o dado de hoje — a
  corrupção entrando por omissão. O padrão é `False` no Airflow 3.x e `True` no 2.x; declarar
  torna a versão irrelevante.
- **Um dia perdido é perdido.** Não há como reconstruir a data de referência de um ciclo que não
  rodou, e sob retenção de 7 dias isso é um buraco permanente na série.
- O reprocessamento forçado só opera sobre a data corrente: o ADMINISTRADOR conserta hoje, não
  ontem.
- **Efeito colateral favorável:** data de referência e data de gravação do artefato passam a ser a
  mesma data, então a política de ciclo de vida do bucket implementa a janela de retenção de RN-36
  sem qualquer ajuste de compensação.
