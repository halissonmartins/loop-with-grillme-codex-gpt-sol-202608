# Especificação — Scheduler Jasper Report (MVP)

> Spec de implementação do MVP inteiro (F01–F17). Sintetiza [`prd.md`](./prd.md),
> [`arquitetura-inicial.md`](./arquitetura-inicial.md) e os sete ADRs de [`adr/`](./adr/) numa
> forma acionável: histórias, decisões e **costuras de teste**.
>
> Os termos são os do [`glossario.md`](./glossario.md) e não são redefinidos aqui. As
> referências `RN-NN`/`RF-NN` apontam para o PRD; `RA-NN`, para a arquitetura.
>
> Esta spec **cumpre o artefato `user-stories.md`** exigido por P0 do guia — a seção 3 é a lista
> de histórias, e os critérios de aceite verificáveis são os `RF-NN` já escritos no PRD.

---

## 1. Problem Statement

Quem precisa de um relatório hoje não tem onde buscá-lo. Cada pedido vira uma consulta à base
transacional do produto, e é no fim do mês — quando mais se pede relatório — que essa base está
mais carregada. O resultado, por pessoa:

- **O analista** não sabe se o dado da data que ele precisa existe, não sabe se o número que
  recebeu ontem é o mesmo de hoje, e depende de outra pessoa para receber o mesmo relatório num
  formato diferente do PDF.
- **O responsável pela área** não consegue conceder acesso a um conjunto de relatórios. Concede
  caso a caso, a permissão fica dispersa e ninguém consegue auditar quem enxerga o quê.
- **Quem opera** descobre que a apuração falhou quando o usuário reclama. Uma apuração travada é
  indistinguível de uma que ainda está rodando, e um relatório que nunca chegou a ser tentado
  simplesmente não aparece em lugar nenhum.

Não existe hoje um lugar onde o dado do relatório esteja apurado, congelado por data e pronto
para ser entregue.

---

## 2. Solution

O sistema tem duas metades que se encontram num repositório de artefatos.

**Coleta.** Todo dia às 03h00, um Ciclo apura os relatórios ativos de cada produto a partir da
sua base transacional, numa Janela de leitura controlada, e grava o resultado já renderizado como
Artefato, junto com os metadados da Execução. É a **única fronteira de leitura** dessas bases.

**Exportação.** Sob demanda e de forma síncrona, um usuário autorizado escolhe um relatório já
coletado e recebe o arquivo no formato que precisa — PDF, XLSX, DOCX ou CSV — lido do Artefato,
nunca da base.

O relatório é **lido da base uma vez** e **exportado quantas vezes for preciso**. Toda a
arquitetura existe para sustentar essa assimetria.

Três garantias sustentam a promessa, e são elas que a spec precisa fazer valer:

1. **Nada desaparece.** A Reserva do ciclo cria a Execução de todo relatório ativo antes de
   qualquer apuração começar, então um relatório que nunca rodou aparece como falha em vez de
   sumir do denominador da métrica.
2. **Nada fica preso.** Nenhuma Execução permanece em `em processamento` — há dois limites de
   tempo com papéis distintos e um encerramento de execução anômala.
3. **Nada é acessado por omissão.** Todo acesso passa pela Cadeia de permissão. Um RELATOR sem
   grupo não alcança relatório algum.

---

## 3. User Stories

### 3.1 Coleta — ciclo e apuração

1. Como **operação**, quero que o Ciclo dispare automaticamente todos os dias às 03h00 no fuso
   `America/Sao_Paulo`, para que a apuração não dependa de alguém lembrar de acioná-la. *(RF-01,
   RNF-20, RNF-14, RN-07)*
2. Como **operação**, quero que o Ciclo apure todos os relatórios **ativos** de todos os produtos,
   para que nenhum relatório do catálogo visível fique sem apuração. *(RF-01, RN-06)*
3. Como **operação**, quero que a Reserva do ciclo crie uma Execução por relatório ativo **antes**
   de qualquer apuração começar, para que um relatório que nunca chegar a ser apurado apareça como
   falha em vez de desaparecer do denominador da métrica. *(RF-45, RN-45)*
4. Como **operação**, quero que a Execução reservada nasça com data/hora de início nula, para que
   ela seja distinguível de uma execução que de fato começou. *(RA-14, RA-54)*
5. Como **operação**, quero que a Data de referência seja sempre derivada do disparo do Ciclo e
   nunca informada, para que jamais se grave o dado de hoje sob o rótulo de ontem. *(RF-53, RN-54)*
6. Como **operação**, quero que a DAG declare `catchup=False` explicitamente, para que subir a DAG
   com `start_date` no passado não dispare uma run por dia perdido carimbando datas antigas com o
   dado de hoje. *(RA-56, ADR-0005)*
7. Como **operação**, quero que os produtos sejam apurados em ondas de no máximo dois simultâneos,
   para que a contenção de CPU não transforme `processado com alerta` em ruído de ambiente.
   *(RNF-18, RA-55)*
8. Como **operação**, quero que cada produto seja lido numa única Janela de leitura por Ciclo, para
   que a base transacional não seja aberta repetidamente ao longo do dia. *(RN-44, ADR-0001)*
9. Como **operação**, quero que a Coleta seja o único componente que lê os schemas transacionais,
   para que nenhuma consulta de usuário jamais concorra com a operação do produto. *(RN-31, RA-10)*
10. Como **operação**, quero que o artefato seja gravado **antes** dos metadados de conclusão, para
    que nunca exista uma Execução em `processado com sucesso` sem artefato correspondente.
    *(RA-11)*
11. Como **operação**, quero que cada Execução registre data de referência, início, fim, duração,
    status e origem, para que a apuração seja auditável sem consultar log. *(RF-02, F02)*
12. Como **operação**, quero que o tempo estimado vigente seja **copiado para dentro** da Execução
    no momento do disparo, para que editar o catálogo não reclassifique execuções passadas.
    *(RN-47, ADR-0004)*
13. Como **operação**, quero que o processador conte as linhas do dataset antes de apurar e recuse
    o relatório que ultrapassar o teto, para que a violação apareça como erro explícito na Coleta e
    não como falha de memória na exportação, dias depois. *(RF-49, RN-52, RA-64)*
14. Como **operação**, quero que todo statement de leitura da Coleta declare `queryTimeout`, para
    que uma consulta travada dentro de uma única chamada JDBC não escape ao limite do relatório.
    *(RA-57)*

### 3.2 Coleta — status e desfechos

15. Como **operação**, quero que uma Execução concluída sem falhas e dentro do tempo estimado
    termine em `processado com sucesso`, para que o caso feliz seja o caso medido pela métrica
    primária. *(RF-01)*
16. Como **operação**, quero que uma Execução concluída **sem nenhuma falha** cuja duração
    ultrapassou o tempo estimado termine em `processado com alerta`, para que a degradação de
    desempenho seja visível sem invalidar um artefato que é utilizável. *(RF-03, RN-11)*
17. Como **operação**, quero que qualquer falha registrada resulte em `processado com erro`
    independentemente da duração, para que o erro sempre prevaleça sobre o alerta. *(RF-04, RN-12,
    D02)*
18. Como **operação**, quero que um relatório que atinja **o dobro** do seu tempo estimado seja
    abortado e encerrado como `processado com erro`, para que uma apuração travada tenha fim
    conhecido. *(RF-05, RN-13)*
19. Como **operação**, quero que abortar um relatório por tempo **não interrompa** os demais
    relatórios do mesmo produto, para que um relatório problemático não derrube a apuração inteira
    do domínio. *(RF-05)*
20. Como **operação**, quero um limite de segurança no nível do produto — o dobro da soma dos
    tempos estimados, com folga — para que uma apuração travada a ponto de não conseguir aplicar o
    próprio limite ainda assim termine. *(RN-13, RA-57)*
21. Como **operação**, quero que a Execução cujo processo termine de forma anômala seja encerrada
    como `processado com erro` em vez de permanecer em `em processamento`, para que uma apuração
    morta seja distinguível de uma que ainda está rodando. *(RF-06, RN-10, RA-14)*
22. Como **operação**, quero que o encerramento anômalo alcance também **as execuções reservadas
    que nunca chegaram a iniciar**, para que a queda de um contêiner no meio do produto não deixe
    metade dos relatórios em limbo. *(RA-14, RA-68)*
23. Como **operação**, quero que a tabela de metadados de execução seja a única fonte da verdade do
    status, para que nem o estado da task no orquestrador nem a presença do arquivo no repositório
    possam contradizê-la. *(RN-09, RA-25)*
24. Como **operação**, quero que uma Execução em status terminal nunca mais mude de status, para
    que o histórico seja um registro de eventos e não um estado sobrescrito. *(RN-15, RA-67)*

### 3.3 Coleta — retentativa, unicidade e reprocessamento

25. Como **operação**, quero que uma Execução que falhou origine uma nova Execução de origem
    `retentativa`, com a anterior preservada e marcada como não-vigente, para que a tentativa
    anterior continue contável. *(RF-46, RN-15, RN-46)*
26. Como **operação**, quero que a retentativa releia **apenas** os relatórios que não concluíram,
    para que a Janela de leitura não seja reaberta para o que já entregou artefato válido. *(RN-44,
    ADR-0001)*
27. Como **operação**, quero um teto de retentativas por relatório dentro de um Ciclo, para que uma
    falha sistemática não vire um laço infinito contra a base transacional. *(RNF-17)*
28. Como **operação**, quero que exista no máximo uma Execução vigente por par *Data de referência
    + Código do relatório*, para que a resposta a "qual é o dado de 08/08?" seja única. *(RN-16)*
29. Como **operação**, quero que "vigente" seja um **ponteiro** e não um status, para que os quatro
    status permaneçam quatro e não seja preciso inventar um `invalidada`. *(RN-46, ADR-0004)*
30. Como **usuário do sistema**, quero que uma nova execução de um par cuja execução vigente esteja
    em sucesso ou alerta seja **recusada**, sem alterar artefatos e sem criar Execução, para que um
    dado já apurado não seja sobrescrito por acidente. *(RF-08, RN-17)*
31. Como **ADMINISTRADOR**, quero que essa recusa seja registrada como evento de auditoria — com
    solicitante, momento e Correlation ID — e **não** como Execução, para que uma tentativa recusada
    nunca apareça como falha de apuração na métrica. *(RF-08, RN-18)*
32. Como **operação**, quero que um par cuja execução vigente esteja em `processado com erro` possa
    ser executado novamente sem autorização especial, porque não há artefato válido a preservar.
    *(RF-09, RN-19)*
33. Como **ADMINISTRADOR**, quero solicitar o Reprocessamento forçado de um par já concluído com
    sucesso ou alerta, para consertar uma apuração que saiu errada sem esperar o Ciclo do dia
    seguinte. *(RF-10, RN-20, F04)*
34. Como **ADMINISTRADOR**, quero que o Reprocessamento forçado exija **motivo textual
    obrigatório**, para que exista registro do porquê de um dado congelado ter sido refeito.
    *(RF-11, RN-21)*
35. Como **ADMINISTRADOR**, quero que o Reprocessamento forçado invalide a execução anterior
    **preservando-a**, ligada ao motivo e ao solicitante, porque auditoria que apaga o registro
    auditado não é auditoria. *(RF-10, ADR-0004)*
36. Como **ADMINISTRADOR**, quero que o Reprocessamento forçado sobrescreva os artefatos, para que
    a exportação passe a entregar o dado corrigido. *(RN-20, RA-12)*
37. Como **ADMINISTRADOR**, quero que o Reprocessamento forçado opere **somente sobre a data de
    referência corrente**, para que não haja caminho algum de apuração retroativa. *(RN-20, RN-54,
    ADR-0005)*
38. Como **usuário de perfil GERENTE ou RELATOR**, quero ser impedido de solicitar Reprocessamento
    forçado, para que a refeitura de um dado congelado permaneça restrita a quem responde pela
    plataforma. *(RF-12)*
39. Como **operação**, quero que enquanto existir Execução do par em `em processamento`, nova
    execução do mesmo par seja recusada como evento de auditoria, para que não haja duas apurações
    concorrentes do mesmo dado. *(RN-43)*
40. Como **operação**, quero que o Airflow nunca seja exposto ao usuário final, e que o
    Reprocessamento forçado seja acionado pela API, para que solicitante, motivo e Correlation ID
    sejam capturados antes do disparo. *(RA-13)*

### 3.4 Artefatos, retenção e expurgo

41. Como **operação**, quero que cada Execução bem-sucedida produza **dois arquivos irmãos** — o
    renderizado e o dataset bruto comprimido — para que PDF/XLSX/DOCX e CSV sejam servidos sem que
    nenhum deles precise reabrir a base. *(RA-16, RA-17)*
42. Como **operação**, quero que o caminho do artefato seja *data de referência → sigla do produto
    → código do relatório*, para que ele dependa apenas de identificadores imutáveis e nunca mude.
    *(RN-08, RA-19)*
43. Como **quem precisa do relatório**, quero que os artefatos fiquem disponíveis por 7 dias
    contados da Data de referência, para que eu tenha uma janela previsível para buscá-los.
    *(RN-36, RNF-12)*
44. Como **operação**, quero a janela de retenção configurável por variável de ambiente, para
    ajustá-la sem alterar código. *(RN-36, RA-20)*
45. Como **operação**, quero que o Expurgo dos artefatos seja automático, para que o disco da
    máquina alvo não seja consumido indefinidamente. *(RN-37, F05)*
46. Como **operação**, quero que o Expurgo atinja **somente os artefatos**, para que a Execução e o
    histórico de downloads sobrevivam a ele. *(RN-51, RN-38, ADR-0007)*
47. Como **operação**, quero que os metadados de Execução **nunca** sejam expurgados, para que a
    métrica primária de 30 dias não passe a ser calculada sobre uma série truncada de 7. *(RN-51,
    RNF-16, RA-62)*
48. Como **operação**, quero que o repositório notifique o sistema quando um artefato expira, para
    que a indisponibilidade seja conhecida e não descoberta na hora do download. *(RA-21)*
49. Como **operação**, quero que a assinatura do evento de expurgo seja a de remoção de objeto e não
    a de ciclo de vida, para que o webhook de fato dispare em vez de ficar silenciosamente mudo.
    *(RA-21)*
50. Como **quem precisa do relatório**, quero que a data cujos artefatos foram expurgados deixe de
    aparecer na listagem, para que eu não perca tempo pedindo o que não existe mais. *(RF-24,
    RN-37)*
51. Como **quem precisa do relatório**, quero que a solicitação de um artefato já expurgado seja
    recusada com mensagem explícita de indisponibilidade por retenção — nunca com erro genérico —
    para que eu entenda que o dado expirou e não que o sistema quebrou. *(RF-24, RN-39)*
52. Como **operação**, quero que a API derive o estado *expirado* por comparação de datas quando a
    notificação de expurgo faltar, para que uma notificação perdida custe uma inconsistência
    transitória e não uma resposta errada ao usuário. *(RA-63)*

### 3.5 Listagem e exportação

53. Como **RELATOR**, quero navegar os relatórios disponíveis por data → produto → relatório, para
    encontrar o que preciso sem conhecer a estrutura interna do sistema. *(RF-13, F06)*
54. Como **RELATOR**, quero ver a data no formato `dd/MM/yyyy`, porque é o formato que uso.
    *(RF-13, RNF-15)*
55. Como **RELATOR**, quero que a interface deixe claro que a Data de referência é o dia da
    apuração e não o dia do movimento, para que eu não leia o fechamento de um dia
    acreditando que é o de outro. *(RN-07, e o verbete Data de referência do glossário)*
56. Como **RELATOR**, quero que a minha listagem contenha exclusivamente os relatórios alcançados
    pela minha Cadeia de permissão, para não ver o que não me diz respeito. *(RF-14, RN-23)*
57. Como **responsável pela segurança**, quero que a tentativa de acesso direto a um relatório fora
    da permissão do usuário seja negada, para que esconder da listagem não seja a única defesa.
    *(RF-15, RN-23)*
58. Como **RELATOR** *pendente de vínculo*, quero entrar na aplicação e receber uma mensagem
    dizendo que aguardo a configuração das permissões, com a listagem vazia, para saber que o
    cadastro deu certo e o que falta. *(RF-16, RN-28)*
59. Como **ADMINISTRADOR**, quero enxergar e exportar todos os relatórios sem passar pela Cadeia de
    permissão, para diagnosticar qualquer apuração sem depender de me conceder acesso. *(RF-17,
    RN-24)*
60. Como **responsável pela segurança**, quero que o GERENTE não consiga exportar relatório algum,
    para que administrar acesso e consumir dado sejam capacidades separadas. *(RF-18, RN-25)*
61. Como **RELATOR**, quero receber o arquivo na mesma requisição em que o solicito, para não ter
    que acompanhar o andamento de um pedido. *(RF-19, RN-30, RA-26, F07)*
62. Como **RELATOR**, quero exportar em **PDF**, para ler e arquivar o relatório como ele foi
    desenhado. *(RF-19, RN-32)*
63. Como **RELATOR**, quero exportar em **DOCX** preservando a paginação, para editar o documento
    mantendo o layout. *(RN-32)*
64. Como **RELATOR**, quero exportar em **XLSX** como planilha contínua, para filtrar e somar sem
    tropeçar em cabeçalho e rodapé repetidos no meio dos dados. *(RF-21, RN-33)*
65. Como **RELATOR**, quero que o cabeçalho de coluna do XLSX apareça **exatamente uma vez**, para
    que a planilha seja utilizável como tabela. *(RF-21, ADR-0006)*
66. Como **RELATOR**, quero exportar em **CSV** o dataset bruto da consulta principal — sem
    subrelatórios, sem imagens e sem formatação — para reprocessar o dado em outra ferramenta.
    *(RF-22, RN-34)*
67. Como **RELATOR** brasileiro, quero o CSV com separador compatível com o Excel pt-BR, para
    abri-lo sem passo de importação. *(RA-17)*
68. Como **RELATOR**, quero que a exportação seja recusada quando a execução vigente daquela data
    não estiver em sucesso ou alerta, para que eu nunca receba um arquivo derivado de apuração que
    falhou. *(RF-20, RN-42)*
69. Como **operação**, quero que a exportação nunca acesse a base transacional, para que a razão de
    existir do sistema seja um invariante e não uma intenção. *(RN-31, RA-29)*
70. Como **operação**, quero um teto de exportações simultâneas, para que o consumo de memória da
    API permaneça dentro de um limite conhecido. *(RF-48, RN-53, RA-60)*
71. Como **RELATOR**, quero que a requisição excedente ao teto seja **recusada de imediato** com
    indicação de repetir mais tarde, e não enfileirada, para que eu saiba na hora o que aconteceu.
    *(RF-48, RN-53)*

### 3.6 Download e histórico

72. Como **RELATOR**, quero baixar o arquivo exportado, porque é isso que eu vim buscar. *(F08)*
73. Como **ADMINISTRADOR**, quero que todo Download seja registrado com usuário, relatório, data de
    referência, formato e momento, para saber quem levou qual dado e quando. *(RF-23, RN-35)*
74. Como **ADMINISTRADOR**, quero consultar o histórico de downloads, para responder a perguntas de
    auditoria sem abrir o banco. *(RF-23, F09)*
75. Como **ADMINISTRADOR**, quero que o registro de Download guarde **cópia** dos identificadores
    do momento, para que o download de 2026 não passe a exibir o nome que o relatório ganhou em
    2027. *(RF-51, RA-66, ADR-0007)*
76. Como **ADMINISTRADOR**, quero que o histórico de downloads sobreviva ao Expurgo do artefato e à
    Inativação do relatório, para que a auditoria não tenha buracos. *(RF-51, RN-38, RA-22,
    RNF-13)*

### 3.7 Catálogo

77. Como **operação**, quero que cada módulo processador publique o seu produto e os seus
    relatórios ao iniciar, para que o catálogo seja derivado do código e não possa divergir dele.
    *(RA-58, RN-49, ADR-0002)*
78. Como **operação**, quero que a inicialização de um produto seja **recusada** se dois de seus
    relatórios declararem o mesmo código, para que a violação de RN-03 seja impossível de
    persistir. *(RF-44, RN-03)*
79. Como **ADMINISTRADOR**, quero editar o nome do produto, para corrigir a exibição sem tocar em
    identificador algum. *(RF-41, F10)*
80. Como **ADMINISTRADOR**, quero editar nome, descrição e tempo estimado de um relatório, porque
    são ajustes de operação. *(RF-41, F11)*
81. Como **responsável pela plataforma**, quero que a Sigla do produto e o Código do relatório
    sejam imutáveis, para que o caminho do artefato e o histórico nunca fiquem ambíguos. *(RF-41,
    RN-01, RN-02)*
82. Como **operação**, quero que todo relatório tenha tempo estimado em segundos inteiros maior que
    zero, validado tanto na inicialização quanto na edição, para que o alerta de degradação e o
    limite de interrupção tenham base. *(RF-27, RN-04)*
83. Como **operação**, quero que a edição do tempo estimado seja recusada se fizer a soma do produto
    ultrapassar o teto, para que a janela do Ciclo seja garantida pelo catálogo e não desejada.
    *(RF-47, RN-48)*
84. Como **ADMINISTRADOR**, quero inativar um relatório, para retirá-lo do catálogo visível sem
    destruir o seu histórico. *(F11, RN-50)*
85. Como **ADMINISTRADOR**, quero que a inativação de um produto seja recusada enquanto houver
    relatório ativo nele, para não deixar relatórios órfãos de produto visível. *(RF-50, RN-05)*
86. Como **ADMINISTRADOR**, quero que relatório e produto inativados desapareçam da listagem e
    permaneçam referenciáveis por execuções, auditoria e downloads, para que a métrica dos 30 dias
    não mude retroativamente. *(RF-54, RN-50, ADR-0007)*
87. Como **ADMINISTRADOR**, quero que a aplicação nunca crie nem apague produto ou relatório, para
    que uma sigla sem módulo, sem base e sem JRXML não possa existir. *(RN-49, ADR-0002)*

### 3.8 Identidade e acesso

88. Como **usuário**, quero entrar e sair da aplicação, para que a minha sessão seja minha. *(F12)*
89. Como **analista de uma área**, quero me autocadastrar publicamente, para não depender de um
    chamado só para conseguir entrar. *(RF-29, F13, RA-33)*
90. Como **responsável pela segurança**, quero que o autocadastro crie exclusivamente usuários de
    perfil RELATOR, sempre *pendente de vínculo*, para que ninguém se conceda perfil pela porta da
    frente. *(RF-30, RN-27)*
91. Como **responsável pela segurança**, quero que não exista grupo padrão nem acesso concedido por
    omissão, para que um RELATOR recém-cadastrado não alcance relatório algum. *(RF-55, D32)*
92. Como **usuário de qualquer perfil**, quero trocar a minha própria senha, para mantê-la sob meu
    controle. *(RF-37, RN-29, F14)*
93. Como **usuário de qualquer perfil**, quero recuperar a senha esquecida por e-mail, para não
    depender de alguém para voltar a entrar. *(RF-37, RA-35)*
94. Como **ADMINISTRADOR**, quero cadastrar e remover usuários ADMINISTRADOR e GERENTE, para
    montar o time que opera a plataforma. *(F15)*
95. Como **ADMINISTRADOR ou GERENTE**, quero remover um usuário RELATOR, para revogar o acesso de
    quem saiu da área. *(RF-35)*
96. Como **GERENTE**, quero criar e remover Roles de relatório, para agrupar acesso por conjunto de
    relatórios em vez de conceder caso a caso. *(RF-31, F16)*
97. Como **GERENTE**, quero vincular uma Role de relatório a relatórios, para definir o que aquela
    permissão alcança. *(RF-31, RN-22)*
98. Como **GERENTE**, quero criar e remover Grupos, para modelar as áreas da organização. *(F17)*
99. Como **GERENTE**, quero vincular Roles de relatório a Grupos, para conceder acesso a um
    conjunto de pessoas de uma vez. *(RF-31, RN-22)*
100. Como **GERENTE**, quero incluir e remover usuários RELATOR em Grupos, para administrar acesso
     sem depender de TI. *(RF-32, F17)*
101. Como **RELATOR** em múltiplos grupos, quero acessar a **união** dos relatórios de todos eles,
     para que pertencer a mais de uma área não me tire acesso. *(RF-33, RN-22)*
102. Como **GERENTE**, quero que remover uma Role de relatório ou um Grupo revogue o acesso
     concedido por eles **sem afetar outros caminhos** da Cadeia, para que a revogação seja
     cirúrgica. *(RF-43, RN-22)*
103. Como **RELATOR**, quero que a remoção do meu vínculo cesse imediatamente o meu acesso aos
     relatórios correspondentes, sem esperar expiração de sessão. *(RF-36)*
104. Como **responsável pela segurança**, quero que o GERENTE não consiga criar nem promover usuário
     a GERENTE ou ADMINISTRADOR **nem por manipulação direta da requisição**, para que a separação
     seja estrutural e não uma verificação que se possa esquecer de escrever. *(RF-34, RN-26,
     ADR-0003)*
105. Como **operação**, quero que um usuário ADMINISTRADOR exista já na subida do ambiente, com
     senha vinda de variável de ambiente, para que o sistema seja administrável desde o primeiro
     minuto. *(RA-32)*
106. Como **operação**, quero que apenas o console de conta e a página de registro do provedor de
     identidade sejam expostos, e não o console administrativo, para reduzir a superfície pública.
     *(RA-34)*

### 3.9 Erros e diagnóstico

107. Como **usuário**, quero que toda mensagem de erro exiba momento, descrição e Correlation ID,
     para que eu tenha o que informar ao abrir um chamado. *(RF-38, RN-40)*
108. Como **usuário**, quero copiar o erro em formato estruturado com um clique, para anexá-lo ao
     chamado sem transcrever nada. *(RF-39, RA-42)*
109. Como **quem opera**, quero que o Correlation ID exibido ao usuário localize a mesma ocorrência
     nos registros da operação, para que o diagnóstico comece pelo que o usuário me deu. *(RF-40,
     RN-41)*
110. Como **quem opera**, quero logs estruturados enriquecidos com identificadores de rastreamento,
     para pesquisar por ocorrência e não por texto livre. *(RA-38)*
111. Como **quem opera**, quero que o módulo API responda a sondas de *liveness* e *readiness*, para
     saber se ele subiu de fato. *(RA-43)*

### 3.10 Métrica e observabilidade

112. Como **responsável pela plataforma**, quero medir a **taxa de apuração limpa** — pares cuja
     execução vigente de origem `agendada` terminou em `processado com sucesso` — em janela móvel
     de 30 dias, para saber se o sistema está cumprindo a sua promessa. *(RF-52, PRD §6)*
113. Como **responsável pela plataforma**, quero que a métrica primária considere exclusivamente
     execuções vigentes de origem `agendada`, para que retentativa e reprocessamento não a
     contaminem. *(RF-52, ADR-0004)*
114. Como **responsável pela plataforma**, quero uma métrica separada de **retentativas por mês**,
     porque a primária mede entrega e esconde instabilidade — sem ela, contar vigentes vira
     maquiagem. *(PRD §6, ADR-0004)*
115. Como **responsável pela plataforma**, quero medir execuções presas em `em processamento` 30 min
     após o fim do Ciclo, esperando **zero**, porque qualquer valor maior é um defeito e não uma
     tendência. *(PRD §6, RN-10)*
116. Como **responsável pela plataforma**, quero medir reprocessamentos forçados por mês, porque são
     o sintoma de apuração instável. *(PRD §6)*
117. Como **responsável pela plataforma**, quero medir exportações que terminam em erro e exportações
     recusadas por limite de simultaneidade, para saber se o teto está apertado demais para o uso
     real. *(PRD §6, RNF-10)*
118. Como **responsável pela plataforma**, quero todas as métricas rotuladas por sigla do produto,
     código do relatório e **origem da execução**, para poder separar as séries sem consulta manual
     ao banco. *(RA-40)*
119. Como **quem opera**, quero que o SDK de telemetria seja desabilitado nos testes, para que a
     suíte não dependa de coletor nem gere ruído. *(RA-39)*
120. Como **quem opera**, quero que log, span, trace e métrica saiam dos módulos por um caminho
     único de instrumentação, para que eu não tenha que aprender um mecanismo diferente por
     módulo quando estiver diagnosticando. *(RA-36)*

---

## 4. Implementation Decisions

### 4.1 Módulos

| Módulo | Papel | Observação |
|---|---|---|
| Biblioteca comum | Tipos do domínio, contrato de erro, utilidades de fuso e data de referência | Dependência de todos os módulos backend *(RA-02)* |
| Starter do Processador | Leitura paginada, contagem prévia, renderização, gravação de artefato, registro de metadados, limite do relatório | Sobre Spring Batch *(RA-03)* |
| Processadores (5) | Um por produto: `POUPANCA`, `CLIENTE`, `CONTACORRENTE`, `CONSORCIO`, `EMPRESTIMO` | **O módulo processador *é* o Produto** *(RA-04)* |
| API REST | Único módulo que atende o usuário final; exportação, catálogo, acesso, auditoria, disparo de reprocessamento | Porta `8080` *(RA-05)* |
| Frontend | Angular; integra-se **exclusivamente** com a API | Não toca repositório, banco nem orquestrador *(RA-06)* |
| DAG do Airflow | Reserva do ciclo, uma task estática por produto, pool de 2, callback de falha | *(RA-54, RA-65, RA-55, RA-14)* |

Mono repositório com **versão única** *(RA-01)*. Essa premissa é o que sustenta os trade-offs da
serialização do artefato renderizado — se cair, eles precisam ser reavaliados.

**Infraestrutura que os módulos pressupõem.** O repositório de artefatos implementa o **padrão
S3**, provido por MinIO *(RA-18)*. O ambiente local inteiro sobe por **Docker Compose com as
dependências reais** — banco, identidade, repositório, orquestrador, ingress, SMTP e a pilha de
telemetria *(RA-51)* — e é esse mesmo conjunto que as costuras de §5 levantam em contêiner. Cada
relatório traz o **seu próprio JRXML**, versionado no repositório e associado ao módulo processador
do seu produto *(RA-07)*, e cada módulo processador traz dois relatórios de exemplo com imagens e
fontes diferentes entre si; no conjunto dos dez, ao menos um carrega elemento que produz renderer
serializado, para que a armadilha do `ClassNotFoundException` da §12 seja exercida e não apenas
declarada *(RA-08)*.

### 4.2 Schema de controle

Escrevem nele a Coleta, o orquestrador e a API. Os schemas transacionais, **só a Coleta lê** —
esse é o invariante que não se negocia *(RA-23)*.

| Conjunto | Conteúdo | Regra que o governa |
|---|---|---|
| Catálogo | Produto (sigla, nome, ativo) e Relatório (código, nome, descrição, tempo estimado, ativo) | Linhas criadas apenas pela publicação de inicialização; a API só edita colunas mutáveis e o sinalizador de ativo *(RA-58)* |
| Execução | Append-only: data de referência, código do relatório, início, fim, duração, status, origem, **cópia** do tempo estimado, ponteiro de vigência | Nenhum caminho atualiza status terminal *(RA-67)* |
| Artefato | Localização lógica e marca de expurgo | Marca autoritativa quando presente; na ausência, estado derivado por comparação de datas *(RA-63)* |
| Auditoria | Recusas de RN-18, solicitações de reprocessamento com motivo e solicitante, Correlation ID | Nunca expurgada |
| Download | Cópia de código, nome do relatório, sigla, data de referência, formato, usuário, momento | Retenção indefinida *(RA-66)* |
| Relatório → Role de relatório | O único elo da Cadeia que precisa conhecer o catálogo | Os demais elos vivem no Keycloak *(RA-61)* |

Versionamento com Flyway; **migration aplicada não se altera** *(RA-24)*.

### 4.3 Contratos e interações

- **Ordem da cadeia de execução.** Reserva do ciclo → orquestrador → contêiner de processamento →
  base transacional do produto → repositório de artefatos → registro de metadados. Nenhuma etapa é
  pulada e nenhuma inverte a ordem *(RA-09)*.
- **Publicação do catálogo.** Na inicialização, cada processador publica produto e relatórios.
  Código duplicado dentro do produto **impede o módulo de subir** *(RF-44)*.
- **Contrato API ↔ orquestrador.** A API dispara a DAG passando `forcar_reprocessamento`. **Não
  existe parâmetro de data de referência** *(RA-12, RA-13)*. Solicitante, motivo e Correlation ID
  são capturados na API antes do disparo.
- **Reserva do ciclo.** Primeira task da DAG: lê o catálogo em tempo de execução e grava uma
  Execução por relatório ativo, com início nulo. Nenhum contêiner sobe antes disso *(RA-54)*.
- **Callback de falha.** Encerra como `processado com erro` todas as execuções abertas daquele
  produto, inclusive as reservadas que nunca iniciaram — distinguíveis pelo início nulo *(RA-14)*.
- **Dois limites de tempo**, com papéis distintos e não intercambiáveis *(RA-57)*:

  | Limite | Onde vive | Valor | Papel |
  |---|---|---|---|
  | Do relatório | Dentro do contêiner, verificado entre chunks | 2× o tempo estimado da própria Execução | Regra de negócio; alimenta a métrica; os demais relatórios seguem |
  | De segurança | `execution_timeout` da task | 2× a soma dos tempos estimados do produto, com folga | Interruptor de emergência; efeito recai no callback de falha |

  O limite do relatório só funciona se **todo statement de leitura declarar `queryTimeout`** — sem
  ele, uma consulta travada dentro de uma chamada JDBC não é interrompida e o sistema
  silenciosamente volta a ter um único limite.
- **Artefatos.** Dois arquivos irmãos por execução bem-sucedida: o `JasperPrint` serializado (base
  de PDF, XLSX e DOCX) e o dataset bruto comprimido com separador `;` (base do CSV) *(RA-16,
  RA-17)*. Caminho por data de referência → **sigla** → código *(RA-19)*. A exportação parte do
  `JasperPrint` para PDF, XLSX e DOCX, e do dataset comprimido para o CSV, que **não passa pelo
  motor de relatório** *(RA-27)*.
- **Expurgo.** Política de ciclo de vida do bucket, janela padrão de 7 dias por variável de
  ambiente. A expiração é notificada a um endpoint da API autenticado por credencial de serviço
  *(RA-63)*. A assinatura do evento é a de **remoção de objeto**, não a de ciclo de vida — a
  documentação do repositório induz ao erro oposto, e segui-la produz um webhook que nunca dispara,
  sem erro algum *(RA-21)*.
- **Precisão da janela.** O cálculo do repositório trunca para o fim do dia em UTC: com 7 dias e o
  Ciclo às 03h00 BRT, o artefato some por volta das **21h do sétimo dia**. Retenção efetiva de 7
  dias e ~18 horas — nunca menor que os 7 dias prometidos, e o desvio é constante *(RA-20)*.
- **XLSX contínuo** *(ADR-0006, RA-59)*. Não é configuração de exportação, é **convenção de
  autoria**: todo JRXML coloca o cabeçalho de coluna na banda `title`, renderizada uma única vez, e
  mantém em `pageHeader`/`pageFooter` apenas ornamento descartável. A exportação exclui essas
  bandas por origem de elemento, sem paginar por planilha e sem espaço vazio entre linhas.
- **Semáforo de exportação.** Teto de exportações simultâneas com **recusa imediata** da
  excedente — não há fila, porque a resposta tem que vir na mesma requisição *(RA-60)*.
- **Cadeia de permissão híbrida** *(ADR-0003, RA-61)*: Perfil como *realm role*; Role de relatório
  como *client role* de um cliente dedicado; Grupo e pertinência nativos; elo Relatório → Role em
  tabela. A resolução da listagem é uma consulta só, a partir das client roles presentes no token.
  O provedor de identidade é o Keycloak e a autorização entre frontend e API trafega por **JWT**
  *(RA-30)*.
- **Telemetria.** Log, span, trace e métrica saem dos módulos para um **coletor OpenTelemetry**,
  que os distribui para as ferramentas de pesquisa de log, de métrica e painel, e de trace
  *(RA-36)*. É o caminho único de instrumentação de todos os módulos.
- **Contrato de erro da API**, documentado no OpenAPI: momento em ISO 8601, descrição e Correlation
  ID *(RA-41)*. O Correlation ID é o identificador de rastreamento propagado via MDC *(RA-37)*.
- **Fuso.** Toda a aplicação e todos os contêineres em `America/Sao_Paulo` *(RA-52)*.

### 4.4 Riscos aceitos que a implementação carrega

Estão nos ADRs e são deliberados. A implementação não deve "consertá-los" por conta própria:

- **A API tem, tecnicamente, poder de criar um ADMINISTRADOR.** Restringir o service account a um
  único cliente depende de recurso em *preview*, e não apoiamos segurança em preview. O que impede
  é o nosso código, reforçado pela separação de espaços de nomes *(ADR-0003)*.
- **Dois relatórios do mesmo produto podem enxergar instantes diferentes da base**, quando um vem
  de retentativa. É o preço de permitir retentativa sob janela única *(ADR-0001)*.
- **Uma apuração travada consome até o dobro do tempo estimado sem intervenção pela aplicação.**
  Não há cancelamento; a mitigação é o teto de catálogo, que torna esse dobro um número conhecido
  *(ADR-0001)*.
- **Um dia perdido é perdido.** Sem retroatividade, um Ciclo que não rodou deixa buraco permanente
  na série *(ADR-0005)*.

---

## 5. Testing Decisions

### 5.1 O que é um bom teste aqui

Um bom teste afirma **comportamento observável pelo negócio** e nada além disso. Concretamente:

- Entra por uma costura e afirma o que sai dela — resposta HTTP, bytes do arquivo, linha de
  Execução, objeto no repositório. **Nunca** afirma que um método foi chamado, que uma classe
  existe ou em que ordem duas dependências internas conversaram.
- Sobrevive a refatoração. Se trocar a estrutura interna de um módulo quebrar o teste sem mudar o
  que o usuário observa, o teste estava afirmando implementação.
- Usa **dependências reais em contêiner** — Postgres, MinIO, Keycloak — e não dublês, espelhando o
  Compose do ambiente local *(RA-51)*. Metade das regras deste sistema *é* comportamento de
  infraestrutura: a política de expurgo, a separação realm role/client role, a unicidade no schema.
  Um dublê afirmaria a nossa suposição sobre elas, que é exatamente o que precisa ser verificado.
- É escrito em Gherkin quando descreve comportamento de negócio *(RA-44)*, com a linguagem do
  glossário nos passos. Cenário que fala de tabela, classe ou endpoint está na altura errada.
- **É escrito antes da implementação.** Teste escrito depois é teste que passa, não teste que
  verifica o critério de aceite.

Localização: `*.feature` em `src/test/resources/feature` nos módulos Java *(RA-45)* e em
`e2e/features/` nos módulos Angular *(RA-46)*. Ferramentas: JUnit 5 + Cucumber + Testcontainers +
Flyway *(RA-47)*, cobertura com JaCoCo *(RA-49)*, CI bloqueante por PR *(RA-53)*.

### 5.2 As três costuras

**Decisão do usuário: três costuras.** A alternativa de duas — mover Reserva do ciclo e
encerramento anômalo para comandos do mono repositório, fazendo-os cair na costura da Coleta — foi
proposta e **recusada**, para manter RA-54 e RA-14 como a arquitetura os escreve hoje. O custo,
declarado aqui para quem ler isto depois: RN-45 e RN-10 permanecem em Python, fora do alcance do
Gherkin dos módulos Java, e o projeto mantém um terceiro ambiente de teste.

| # | Costura | Entrada | Saída afirmada | Cobre |
|---|---|---|---|---|
| **S1** | HTTP na API REST | Requisição com JWT *(RA-30)* | Resposta HTTP, bytes do arquivo, estado do schema de controle | RF-08 a RF-24, RF-29 a RF-44, RF-47 a RF-55 |
| **S2** | Invocação do job de coleta do produto | Disparo do job, schema transacional semeado | Artefatos no repositório, linhas de metadados | RF-01 a RF-05, RF-45 (parcial), RF-46, RF-49 |
| **S3** | DAG do Airflow, com tasks de produto substituídas por dublê | Disparo da DAG | Execuções reservadas, execuções encerradas pelo callback, configuração da DAG | RF-06, RF-45, RF-53 |

**Regras de uso das costuras:**

- Toda regra de negócio é afirmada na costura **mais alta** que a alcança. Uma regra alcançável por
  S1 não ganha teste em S2 também.
- S3 é a costura mais cara e a mais estreita: só entra nela o que é genuinamente do orquestrador —
  a Reserva, o encerramento anômalo, `catchup=False`, o pool de 2 e o `execution_timeout`. Nenhuma
  regra de exportação, catálogo ou acesso encosta em S3.
- O navegador (Playwright, *RA-48*) **não é uma quarta costura de regra**: recebe de 1 a 3 fluxos
  críticos como verificação de fumaça. Toda regra de negócio já foi afirmada em S1.
- Newman + psql *(RA-48)* cobre o E2E de backend ponta a ponta, complementando S1 — não o
  substituindo.

### 5.3 Testes obrigatórios por natureza de risco

São os que ninguém escreve espontaneamente, e por isso são exigidos nominalmente *(RA-68)*:

| Teste | Costura | Por quê |
|---|---|---|
| ADMINISTRADOR acessa relatório fora de qualquer Cadeia de permissão | S1 | É a única exceção de autorização do sistema *(RN-24)* |
| Cabeçalho aparece exatamente uma vez no XLSX de **cada** relatório | S1 | A convenção de autoria apodrece em silêncio no primeiro JRXML escrito por quem não leu o ADR *(RF-21)* |
| GERENTE não promove ninguém, **nem manipulando a requisição** | S1 | Verificação esquecida é escalada de privilégio *(RF-34)* |
| Execução reservada que nunca iniciou é encerrada pelo callback | S3 | É o caso que a implementação ingênua deixa passar *(RA-14)* |

Testes adicionais de risco alto, derivados dos ADRs:

- Recusa de RN-17 gera **evento de auditoria e nenhuma Execução** — se gerar Execução, a métrica
  primária passa a contar tentativas recusadas como falha *(S1, RF-08)*.
- Retentativa **insere linha nova** e move o ponteiro, com a anterior preservada e não-vigente
  *(S2, RF-46)*.
- Tempo estimado editado **não** reclassifica execução passada *(S1 + S2, RN-47)*.
- Exportação de artefato expurgado responde indisponibilidade por retenção, **não** erro genérico —
  e responde certo também quando a notificação de expurgo não chegou *(S1, RF-24, RA-63)*.
- Relatório abortado por tempo **não** derruba os demais relatórios do produto *(S2, RF-05)*.
- Recusa por dataset acima do teto encerra a execução com motivo explícito *(S2, RF-49)*.

### 5.4 Prior art

**Não existe.** O repositório tem apenas documentação; não há um único teste de onde copiar
padrão. Consequência prática, e ela é a razão de esta seção existir: o **primeiro** cenário escrito
em cada costura vira o padrão que todas as sessões seguintes vão copiar. Os três primeiros — um por
costura — devem ser escritos com esse peso em mente e citados no `ARCHITECTURE.md` como
implementação de referência. Regra escrita sem exemplo canônico não é seguida.

### 5.5 Critérios de aceite não-funcionais

Os valores vivem em **PRD §10** (os `RNF`) e **PRD §6** (as metas das métricas) e **não são
repetidos aqui** — duas fontes para o mesmo número divergem em uma semana, que é a mesma disciplina
que o `glossario.md` impõe às definições. O que esta spec fixa é **onde cada um é verificado**:

| Requisito | Como se verifica | Costura |
|---|---|---|
| RNF-05, RNF-06 | Recusa por dataset acima do teto; tamanho do artefato medido no spike | S2 |
| RNF-07, RNF-08, RNF-09 | Latência p95 de exportação, por formato, sob carga | S1 |
| RNF-10 | Recusa imediata da exportação excedente ao semáforo | S1 |
| RNF-11 | Navegação simultânea, sob carga | S1 |
| RNF-12, RNF-13, RNF-16 | As três retenções — artefato, download e Execução | S1 |
| RNF-14, RNF-15 | Fuso e idioma das mensagens | S1 |
| RNF-17 | Teto de retentativas por relatório dentro de um Ciclo | S2 |
| RNF-02 | Decorre de RN-03; validado na inicialização do módulo | S2 |
| RNF-18, RNF-20 | Pool das tasks de produto e horário do Ciclo | S3 |
| RNF-19 | Recusa da edição de tempo estimado que estoure o teto do produto | S1 |
| RNF-04 | Consequência de RNF-19 e RN-48; não é meta medida isoladamente | — |
| RNF-01, RNF-03 | Dimensionamento: calibra o spike, não reprova PR | — |
| Metas de PRD §6 | Instrumentação rotulada, conferida sobre a série real no painel | — |

As três primeiras linhas e a de RNF-11 dependem do spike de `k6`. **Enquanto ele não rodar, os
itens marcados `PROVISÓRIO` em PRD §10 são alvo de calibração e não critério de reprovação de PR** —
reprovar contra um número que ninguém mediu é transformar chute em portão.

---

## 6. Out of Scope

**De produto** *(PRD §5)*: cancelamento de execução em andamento; apuração retroativa; criação e
remoção de produto e de relatório pela aplicação; MFA; rotação obrigatória da senha inicial do
ADMINISTRADOR; verificação de e-mail obrigatória no autocadastro; moderação do autocadastro e grupo
padrão; edição de sigla e de código; agendamento configurável pelo usuário; **parametrização dos
tetos de memória** — RNF-05, RNF-06 e RNF-10 são valores fixos, e `PROVISÓRIO` significa que serão
substituídos por medição, não que alguém possa ajustá-los em operação; notificação ativa de
conclusão ou falha; reexportação a partir de artefato expurgado.

**De engenharia** *(arquitetura, Fora de escopo)*: Kubernetes; classificação de dados, mascaramento
e criptografia em repouso; cache de exportação com o binário exportado; deploy em produção ou
homologação; virtualização do `JasperPrint` na exportação.

**Desta spec, especificamente:**

- Modelagem detalhada das tabelas do schema de controle — é E2 do guia, e esta spec fixa apenas os
  conjuntos e os invariantes de cada um.
- Definição dos relatórios de exemplo *(RA-08)* e dos seus modelos de dados.
- Definição dos nomes dos módulos e da arquitetura interna de cada um.
- O eixo de Produto/Design a jusante: `design/fluxos.md` (P1) e `design/design-system.md` (P2)
  continuam inexistentes e são **pré-requisito de E3** pelo próprio guia. Nenhuma tela deve ser
  construída antes deles.

---

## 7. Further Notes

**A primeira versão executa somente em ambiente local** *(RA-50)*. Não há meta de disponibilidade
nem de escala horizontal. A máquina alvo declarada em RA-50 é o que justifica o pool de produtos, o
teto de linhas, o teto de artefato e o semáforo de exportação. **Esses quatro são um único teto de
memória**: afrouxar qualquer um deles isoladamente o quebra.

**Os números de PRD §10 estão marcados `PROVISÓRIO` e precisam ser medidos, não estimados.** O spike
de calibração com `k6` é o único que ainda bloqueia implementação: tamanho real do `JasperPrint`
desserializado, latência de exportação por formato e teto real de simultaneidade. Enquanto ele não
rodar, Q10 permanece aberta. Q11 — se a meta da métrica primária é adequada — só se responde após o
primeiro mês de operação. Ver §5.5 para onde cada requisito não-funcional é verificado.

**O rótulo da Data de referência engana quem não foi avisado.** O Ciclo roda de madrugada, então o
artefato de uma data carrega o movimento fechado do dia anterior — o glossário marca isso como algo
que não é detalhe, e é a única armadilha do domínio que a interface precisa desfazer ativamente, na
listagem e no cabeçalho do relatório. Uma pessoa que leia o número achando que é do próprio dia
comete um erro que o sistema não tem como detectar *(RN-07, história 55)*.

**Spike de conferência, barato:** confirmar o gatilho do evento de expurgo na tag de imagem que o
Compose fixar. O achado é do branch principal do repositório de artefatos e o contrato é estável há
anos, mas a verificação custa minutos: assinar o evento de remoção, apagar um objeto manualmente e
observar o webhook. Não é incógnita de desenho — é conferência de versão *(RA-21)*.

**Ordem sugerida de ataque**, derivada das costuras e não do PRD:

1. Esqueleto compilando + sondas de saúde + CI verde *(E1/E3 do guia)*. Sem isso, nada abaixo é
   verificável.
2. Schema de controle com Flyway e a publicação do catálogo na inicialização — é o que S1 e S2
   pressupõem existir.
3. Primeiro cenário de cada costura, escrito com o peso de §5.4.
4. Coleta ponta a ponta de **um** produto, com um relatório: reserva → apuração → artefato →
   metadados.
5. Exportação nos quatro formatos, com o teste de cabeçalho único desde o primeiro relatório.
6. Cadeia de permissão e os três testes obrigatórios de acesso.
7. Expurgo, notificação e a derivação por comparação de datas.

**Documentos que esta spec não substitui e que continuam faltando** *(guia)*:
`arquitetura/c4-contexto.md` e `riscos.md` (exigidos por E0), `ARCHITECTURE.md` (E1),
`design/fluxos.md` (P1), `design/design-system.md` (P2).
