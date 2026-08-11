# PRD — Scheduler Jasper Report

> Documento do eixo **Produto** (P0 do `guias/guia-app-web.md`). Descreve **o que** o
> sistema faz e **por quê**. O **como** — stack, módulos, formatos de arquivo,
> orquestração e infraestrutura — vive em [`arquitetura-inicial.md`](./arquitetura-inicial.md).
> Os termos do domínio são definidos uma única vez em [`glossario.md`](./glossario.md) e
> **não** são redefinidos aqui.

---

## 1. Visão geral

O sistema tem duas metades que se encontram num repositório de artefatos:

1. **Coleta** — todo dia, de forma agendada, cada produto tem seus relatórios gerados a
   partir da sua base transacional e gravados já renderizados no repositório, junto com
   os metadados da execução.
2. **Exportação** — sob demanda e de forma síncrona, um usuário autorizado escolhe um
   relatório já coletado e recebe o arquivo no formato que precisa: PDF, XLSX, DOCX ou CSV.

O relatório é **lido da base uma vez** e **exportado quantas vezes for preciso**.

---

## 2. Problema

A base transacional de cada produto só pode ser lida pela Coleta, numa janela controlada.
Toda solicitação de relatório que vá direto à base transacional concorre com a operação do
produto — e é justamente no fim do mês, quando mais se pede relatório, que a base está mais
carregada. Não existe hoje um lugar onde o dado do relatório já esteja apurado, congelado por
data e pronto para ser entregue.

As consequências, por pessoa:

- **Quem precisa do relatório** não sabe se o dado está disponível, não sabe se o número
  que recebeu ontem é o mesmo de hoje, e depende de alguém para exportar num formato
  diferente do PDF.
- **Quem administra o acesso** não tem como conceder acesso a um conjunto de relatórios:
  concede caso a caso, e a permissão fica dispersa e não auditável.
- **Quem opera** descobre que a apuração falhou quando o usuário reclama. Uma execução que
  travou fica indistinguível de uma que ainda está rodando.

**O que muda com o sistema:** o dado é apurado uma vez por dia, congelado por data de
referência, e a entrega passa a ser um problema de leitura — rápida, repetível, autorizada
e observável.

---

## 3. Usuários-alvo

### 3.1 Personas

| Persona | Quem é | O que precisa | Frequência |
|---|---|---|---|
| **Relator** | Analista de uma área de negócio | Baixar, no formato certo, os relatórios da sua área numa data específica | Diária a semanal |
| **Gerente** | Responsável por uma área | Conceder e revogar acesso a relatórios sem depender de TI | Eventual |
| **Administrador** | Responsável pela plataforma | Ajustar o catálogo, gerir usuários administrativos, diagnosticar e refazer apurações | Eventual |

### 3.2 Perfis

Três perfis, **conjunto fechado e definido em código** — não são administráveis pela
aplicação. Um usuário tem exatamente um perfil.

| Ação | ADMINISTRADOR | GERENTE | RELATOR |
|---|:---:|:---:|:---:|
| Editar nome do produto | ✅ | ❌ | ❌ |
| Editar nome, descrição e tempo estimado de relatório | ✅ | ❌ | ❌ |
| Inativar produto e relatório | ✅ | ❌ | ❌ |
| Cadastrar/remover usuário ADMINISTRADOR e GERENTE | ✅ | ❌ | ❌ |
| Remover usuário RELATOR | ✅ | ✅ | ❌ |
| Cadastrar/remover role de relatório | ❌ | ✅ | ❌ |
| Vincular role de relatório a relatório | ❌ | ✅ | ❌ |
| Cadastrar/remover grupo | ❌ | ✅ | ❌ |
| Vincular role de relatório a grupo | ❌ | ✅ | ❌ |
| Incluir/remover usuário em grupo | ❌ | ✅ | ❌ |
| Listar relatórios disponíveis | ✅ *(todos)* | ❌ | ✅ *(só os permitidos)* |
| Exportar e baixar relatório | ✅ *(todos)* | ❌ | ✅ *(só os permitidos)* |
| Solicitar reprocessamento forçado | ✅ | ❌ | ❌ |
| Consultar histórico de downloads | ✅ | ❌ | ❌ |
| Autocadastrar-se | ❌ | ❌ | ✅ |
| Trocar a própria senha e recuperá-la | ✅ | ✅ | ✅ |

O **ADMINISTRADOR** é o único perfil com acesso irrestrito aos relatórios: ele não passa
pela cadeia de permissão. É uma exceção deliberada de autorização e, como tal, exige teste
próprio (RN-24).

> **Não existe "cadastrar produto" nem "cadastrar relatório".** O catálogo é derivado do
> código (RN-49): um produto novo é um módulo novo, um relatório novo é um modelo novo no
> repositório. A aplicação edita atributos e inativa; não cria.

---

## 4. Escopo

### 4.1 Coleta

- **F01** — Apuração agendada diária de todos os relatórios ativos, por produto.
- **F02** — Registro dos metadados de cada execução: data de referência, data/hora de
  início e fim, status, duração e origem.
- **F03** — *Aposentada.* Cancelamento de execução em andamento saiu do escopo (§5, D20).
- **F04** — Solicitação de reprocessamento forçado, pelo ADMINISTRADOR, com motivo
  obrigatório e registro de auditoria.
- **F05** — Expurgo automático dos artefatos após a janela de retenção.

### 4.2 Exportação e consulta

- **F06** — Listagem dos relatórios disponíveis, navegável por data → produto → relatório.
- **F07** — Exportação síncrona de um relatório coletado em PDF, XLSX, DOCX ou CSV.
- **F08** — Download do arquivo exportado.
- **F09** — Consulta, pelo ADMINISTRADOR, do histórico de downloads.

### 4.3 Catálogo

- **F10** — Edição do nome do produto e inativação de produto.
- **F11** — Edição de nome, descrição e tempo estimado de relatório, e inativação de relatório.

### 4.4 Identidade e acesso

- **F12** — Entrada e saída da aplicação (*sign in* / *sign out*).
- **F13** — Autocadastro público de RELATOR.
- **F14** — Troca da própria senha e recuperação de senha por e-mail.
- **F15** — Gestão de usuários administrativos (ADMINISTRADOR e GERENTE).
- **F16** — Gestão de roles de relatório e do seu vínculo com relatórios.
- **F17** — Gestão de grupos: criação, vínculo de roles e inclusão/remoção de usuários.

---

## 5. Fora de escopo

Explicitamente **não** faz parte desta versão:

- **Cancelamento de execução em andamento.** O único mecanismo de interrupção é o limite de
  tempo (RN-13). Derrubar uma execução é ação de operação, fora da aplicação, e o seu efeito
  é `processado com erro`, não um status próprio.
- **Apuração retroativa.** A data de referência nunca é informada por ninguém (RN-54).
- **Criação e remoção de produto e de relatório pela aplicação** — o catálogo é derivado do
  código (RN-49). Nada é removido fisicamente; o que existe é inativação.
- **MFA** (autenticação multifator).
- **Rotação obrigatória** da senha inicial do ADMINISTRADOR.
- **Verificação de e-mail obrigatória** no autocadastro — o RELATOR entra assim que se
  cadastra, apenas sem acesso a relatório algum até ser vinculado a um grupo.
- **Moderação/aprovação do autocadastro** e **grupo padrão** — não há fila de aprovação nem
  acesso concedido por omissão; todo acesso passa pela cadeia de permissão.
- **Edição da sigla de um produto** e **edição do código de um relatório**.
- **Agendamento configurável pelo usuário** — a periodicidade da coleta não é editável pela
  aplicação nesta versão.
- **Parametrização dos tetos de memória** — RNF-05 (tamanho do artefato), RNF-06 (volume do
  dataset) e RNF-10 (exportações simultâneas) são **valores fixos**, não editáveis pela aplicação
  nem por configuração de ambiente. A recusa de dataset acima do teto (RN-52) compara contra um
  número fixo. A marca `PROVISÓRIO` significa que o valor será **substituído por medição** no spike
  de calibração, e não que alguém possa ajustá-lo em operação. Os três, com o pool de RNF-18,
  compõem **um único teto de memória**: expô-los como botões independentes permitiria afrouxar um
  deles e quebrar o conjunto sem que nada detectasse.
- **Notificação ativa** (e-mail, push) de conclusão ou falha de execução.
- **Reexportação a partir de artefato expurgado** — passada a janela de retenção, o dado
  daquela data deixa de existir para o sistema.

---

## 6. Métrica de sucesso

O sistema existe para que o relatório esteja pronto quando alguém pedir. A métrica
primária mede exatamente isso.

### Primária

> **Taxa de apuração limpa:** percentual dos pares *Relatório + Data de referência* cuja
> **execução vigente de origem agendada** terminou em `processado com sucesso` — sem falha e
> dentro do tempo estimado cadastrado.
>
> **Meta: ≥ 98%, medida em janela móvel de 30 dias.** `PROVISÓRIO — a validar após o
> primeiro mês de operação.`
>
> **Denominador:** um par por relatório ativo por dia. Contam-se **execuções vigentes**, não
> tentativas: um relatório que falhou e foi salvo pela retentativa aparece como limpo, porque
> para quem precisa do relatório ele foi entregue. **Reprocessamentos forçados ficam fora** —
> não são apuração agendada e têm métrica secundária própria.
>
> A janela é de 30 dias, e não do ciclo diário, por uma razão aritmética: com o volume
> previsto em RNF-03 (10 execuções por ciclo), uma única falha derruba o dia para 90% e a
> meta de 98% seria inalcançável ou irrelevante. Em 30 dias a série tem ~300 pares e a meta
> passa a significar algo: **no máximo 6 apurações sujas por mês**.

### Secundárias

| Métrica | Meta | Por que importa |
|---|---|---|
| **Retentativas por mês** | **≤ 10** `PROVISÓRIO` | É o que a primária deixa de mostrar. A primária mede entrega; esta mede instabilidade. Sem ela, contar vigentes esconde problema |
| Execuções presas em `em processamento` 30 min após o fim do ciclo | **0** | Mede se a detecção de término anômalo funciona. Qualquer valor > 0 é um bug, não uma tendência |
| Reprocessamentos forçados por mês | **≤ 2** `PROVISÓRIO` | É o sintoma. Subiu, a apuração está instável |
| Exportações que terminam em erro | **≤ 1%** `PROVISÓRIO` | Mede a metade do sistema que o usuário sente |
| Exportações recusadas por limite de simultaneidade | **≤ 1%** `PROVISÓRIO` | Se subir, o teto de RNF-10 está apertado demais para o uso real |

**Instrumentação:** todas devem ser mensuráveis sem consulta manual ao banco, rotuladas por
sigla do produto e código do relatório. A instrumentação é obrigatória — métrica de PRD não
medida de verdade é métrica que não existe (guia, P3).

---

## 7. Conceitos do domínio

Definidos em [`glossario.md`](./glossario.md). Este PRD usa aqueles termos e não os
redefine.

Os que mais mudaram de sentido nesta revisão, e que valem uma leitura antes de seguir:
**Janela de leitura**, **Data de referência** (o rótulo é o dia da apuração, não o do
movimento), **Execução vigente**, **Origem da execução**, **Catálogo** e **Inativação**.

---

## 8. Regras de negócio

### 8.1 Catálogo

- **RN-01** — O Produto tem Sigla e Nome. A Sigla obedece a `^[A-Z]{1,20}$`, é única no
  sistema, **declarada em código** e imutável. O Nome é livre e editável.
- **RN-02** — O Código do Relatório é `SIGLA-NNNN` (regex `^[A-Z]{1,20}-\d{4}$`), formado
  pela Sigla do produto ao qual pertence. É declarado em código e imutável.
- **RN-03** — Os 4 dígitos do Código do Relatório são únicos dentro de um mesmo Produto.
  A violação impede o produto de iniciar — não é validação de formulário, é validação de
  inicialização.
- **RN-04** — Todo relatório tem **tempo estimado de execução** obrigatório, em segundos
  inteiros e maior que zero. O valor inicial vem do código; a operação pode ajustá-lo.
- **RN-05** — Um Produto só pode ser **inativado** se não tiver relatórios ativos.
- **RN-48** — A soma dos tempos estimados dos relatórios ativos de um mesmo Produto não pode
  ultrapassar o teto de RNF-19. É a única forma de RNF-04 ser uma promessa e não um desejo:
  a janela do ciclo é consequência do catálogo, então o catálogo é onde ela se garante.
- **RN-49** — O Catálogo é **derivado do código**. Produto e Relatório não são criados nem
  removidos pela aplicação: cada produto anuncia a si e aos seus relatórios ao iniciar. A
  aplicação edita apenas nome, descrição e tempo estimado.
- **RN-50** — Nada do catálogo é removido fisicamente. Inativar retira do catálogo visível e
  preserva toda a referência de execuções, auditoria e downloads.

> Exemplos de código válidos: `POUPANCA-0001`, `CLIENTE-0005`, `CONTACORRENTE-1234`,
> `CONSORCIO-9874`, `EMPRESTIMO-4567`.

### 8.2 Coleta

- **RN-06** — A coleta é agendada e apura todos os relatórios **ativos**.
- **RN-07** — A **Data de referência** é o dia em que o ciclo foi disparado, no fuso
  `America/Sao_Paulo`. Como o ciclo roda de madrugada, o artefato de uma data contém o
  movimento fechado do dia anterior — ver a advertência no glossário.
- **RN-54** — A Data de referência **nunca é informada**. Não é parâmetro de entrada da
  aplicação, do orquestrador nem do reprocessamento forçado. Apuração retroativa só produziria
  dado correto se toda base transacional fosse temporal, o que não é garantido: rodar hoje uma
  apuração carimbada com data passada gravaria os números de hoje sob o rótulo de ontem, e
  nenhuma métrica do sistema conseguiria detectar isso.
- **RN-44** — Existe no máximo **uma janela de leitura bem-sucedida por relatório por dia**.
  Uma janela que falhou não entregou artefato e pode ser reaberta pela retentativa, que relê
  **apenas** os relatórios que não concluíram. O número de retentativas é o de RNF-17.
- **RN-45** — Antes de qualquer apuração começar, o ciclo **reserva** uma Execução para cada
  relatório ativo, com início ainda não preenchido. Sem a reserva, um relatório que nunca chega
  a ser apurado desaparece do denominador da métrica em vez de aparecer como falha — e o dia em
  que metade dos relatórios não saiu seria contabilizado como 100% limpo.
- **RN-08** — O armazenamento é organizado logicamente por
  **data de referência → sigla do produto → código do relatório**. Por depender apenas de
  identificadores imutáveis (RN-01, RN-02), o caminho de um artefato nunca muda.
- **RN-09** — A tabela de metadados de execução é a **fonte da verdade** do status. Nenhuma
  outra fonte pode contradizê-la.
- **RN-10** — Nenhuma execução permanece em `em processamento` indefinidamente. Quando a
  apuração termina de forma anômala e não consegue registrar o próprio encerramento, o
  orquestrador encerra como `processado com erro` toda execução daquele produto que tenha
  ficado aberta.

### 8.3 Ciclo de vida do status

Quatro status. `em processamento` é o único não-terminal.

| Status | Produziu artefato válido | Usuário pode exportar | Bloqueia nova execução do par |
|---|:---:|:---:|:---:|
| `em processamento` | ❌ | ❌ | ✅ *(enquanto durar)* |
| `processado com sucesso` | ✅ | ✅ | ✅ |
| `processado com alerta` | ✅ | ✅ | ✅ |
| `processado com erro` | ❌ | ❌ | ❌ |

- **RN-11** — `processado com alerta` ocorre **somente** quando a execução concluiu **sem
  nenhuma falha** e sua duração ultrapassou o tempo estimado registrado na própria execução
  (RN-47). O artefato é válido e utilizável; o alerta sinaliza degradação de desempenho, não
  de conteúdo.
- **RN-12** — Qualquer falha registrada durante a execução resulta em
  `processado com erro`, independentemente da duração. **O erro sempre prevalece sobre o
  alerta.**
- **RN-13** — A interrupção por tempo tem **dois limites, com papéis distintos**:
  - **Limite do relatório** — ao atingir **o dobro** do tempo estimado, a apuração daquele
    relatório é abortada e encerrada como `processado com erro`. Os demais relatórios do mesmo
    produto continuam. É regra de negócio e alimenta a métrica.
  - **Limite de segurança do produto** — o orquestrador interrompe a apuração inteira do produto
    quando ela ultrapassa o dobro da soma dos tempos estimados, com folga. Existe para o caso de
    a apuração estar travada a ponto de não conseguir aplicar o próprio limite, e o seu efeito
    recai em RN-10.

  Não são o mesmo prazo implementado duas vezes: o primeiro mede um relatório, o segundo é um
  interruptor de emergência.
- **RN-14** — *Aposentada.* O cancelamento de execução saiu do escopo (§5, D20).
- **RN-15** — Uma execução em status terminal não muda mais de status. Consequência direta:
  uma retentativa **cria uma nova Execução**, nunca reabre a anterior.
- **RN-42** — Só é possível exportar um relatório cuja execução vigente para aquela data de
  referência esteja em `processado com sucesso` ou `processado com alerta`. Nos demais
  status não existe artefato válido e a exportação é recusada.
- **RN-43** — Enquanto existir execução do par em `em processamento`, nova execução do
  mesmo par é recusada. É a mesma recusa de RN-18: evento de auditoria, não Execução.

### 8.4 Unicidade, retentativa e reprocessamento

- **RN-16** — Existe no máximo **uma execução vigente** para o par
  *Data de referência + Código do relatório*.
- **RN-46** — A Execução é um **registro imutável**. "Vigente" é um **ponteiro**, não um
  status: as execuções anteriores permanecem, marcadas como não-vigentes. Toda Execução
  declara a sua **origem**: `agendada`, `retentativa` ou `reprocessamento forçado`.
- **RN-47** — O tempo estimado é **copiado para dentro da Execução** no momento do disparo.
  Alterar o catálogo não reclassifica execuções passadas, e a métrica permanece comparável ao
  longo do tempo.
- **RN-17** — Uma nova execução para um par cuja execução vigente esteja em
  `processado com sucesso` ou `processado com alerta` é **recusada**. Os artefatos e os
  metadados originais permanecem intactos.
- **RN-18** — A recusa de RN-17 é registrada como **evento de auditoria** — com solicitante,
  momento e Correlation ID — e **não** cria uma nova Execução. Uma tentativa recusada nunca
  aparece como falha de apuração.
- **RN-19** — Um par cuja execução vigente esteja em `processado com erro` pode ser executado
  novamente sem qualquer autorização especial: não há artefato válido a preservar.
- **RN-20** — O ADMINISTRADOR pode solicitar o **reprocessamento forçado** de um par já
  concluído com sucesso ou alerta, **para a data de referência corrente**. A solicitação
  invalida a execução anterior, gera nova apuração e **sobrescreve** os artefatos.
- **RN-21** — Toda solicitação de reprocessamento forçado exige **motivo textual
  obrigatório** e é registrada com o identificador do solicitante, o motivo e o
  Correlation ID. A execução invalidada permanece, ligada a esse registro.

### 8.5 Acesso e autorização

- **RN-22** — A concessão de acesso segue a cadeia
  **Relatório → Role de relatório → Grupo → Usuário**. Todos os elos são **N:N**. O acesso
  efetivo é a **união** de todos os caminhos.
- **RN-23** — Um RELATOR só enxerga e só exporta os relatórios alcançados pela sua cadeia de
  permissão. Relatório fora dela **não aparece na listagem e não é acessível por acesso
  direto**.
- **RN-24** — O ADMINISTRADOR acessa todos os relatórios **sem** passar pela cadeia de
  permissão. É a única exceção de autorização do sistema e exige teste automatizado dedicado.
- **RN-25** — O GERENTE administra o acesso mas **não** exporta relatórios.
- **RN-26** — O GERENTE não pode criar nem promover outro usuário a GERENTE ou a
  ADMINISTRADOR. Perfil e Role de relatório são objetos de **tipos distintos**, e o GERENTE
  opera apenas sobre os segundos — a separação é estrutural, não uma verificação que se possa
  esquecer de escrever.
- **RN-27** — O autocadastro público cria **exclusivamente** usuários de perfil RELATOR,
  sempre no estado *pendente de vínculo*.
- **RN-28** — Um RELATOR *pendente de vínculo* consegue entrar na aplicação e recebe uma
  mensagem informando que aguarda a configuração das permissões. Sua listagem de relatórios
  vem vazia.
- **RN-29** — Todo usuário, de qualquer perfil, pode trocar a própria senha e recuperá-la
  por e-mail.

### 8.6 Exportação e formatos

- **RN-30** — A exportação é **síncrona**: devolve o arquivo ou devolve erro na mesma
  requisição. Não possui status persistido — o ciclo de vida de status da seção 8.3 pertence
  exclusivamente à Execução da Coleta.
- **RN-31** — A exportação nunca consulta a base transacional do produto. Lê apenas o
  artefato já apurado.
- **RN-32** — Os formatos de entrega são **PDF, XLSX, DOCX e CSV**.

| Formato | Preserva a paginação | Origem do dado |
|---|:---:|---|
| **PDF** | ✅ | Artefato renderizado |
| **DOCX** | ✅ | Artefato renderizado |
| **XLSX** | ❌ | Artefato renderizado, exportado como planilha contínua |
| **CSV** | não se aplica | Dataset bruto da consulta principal |

- **RN-33** — O artefato é apurado já paginado e posicionado para leitura em página. Traduzir
  esse posicionamento para planilha produz um resultado ilegível; por isso o **XLSX ignora a
  paginação** e é entregue como planilha contínua, sem cabeçalho e rodapé repetidos. Isso
  **não** é obtido por configuração de exportação isolada: impõe uma convenção de autoria a
  todo modelo de relatório do projeto, declarada na arquitetura.
- **RN-34** — O **CSV é o dataset bruto** da consulta principal do relatório — sem
  subrelatórios, sem imagens e sem formatação. É o formato para quem vai reprocessar o dado,
  não para quem vai lê-lo.
- **RN-35** — Todo download é registrado com usuário, relatório, data de referência, formato
  e momento, **com cópia dos identificadores** como estavam naquele instante.
- **RN-52** — A Coleta recusa apurar relatório cujo dataset ultrapasse o teto de RNF-06,
  encerrando a execução como `processado com erro` com motivo explícito. Sem essa recusa o teto
  é decorativo: a violação só apareceria como falha de memória na exportação, dias depois.
- **RN-53** — O número de exportações simultâneas é limitado (RNF-10). A requisição excedente é
  **recusada de imediato**, com indicação de repetir mais tarde — não é enfileirada, porque
  RN-30 exige resposta na mesma requisição.

### 8.7 Retenção

Três ciclos de vida independentes. Confundi-los é o que faria a métrica de 30 dias ser
calculada sobre dados que já não existem.

- **RN-36** — Os **artefatos** ficam disponíveis por uma **janela de retenção de 7 dias**,
  contada a partir da data de referência. O valor é configurável pela operação. O expurgo é
  aplicado por política do repositório, que arredonda para o fim do dia em UTC: na prática o
  artefato some por volta das **21h do sétimo dia**, e nunca antes de completar os 7 dias. Quem
  precisa do relatório no último dia da janela precisa buscá-lo antes disso — ver RA-20.
- **RN-37** — Expirada a janela, os artefatos são expurgados automaticamente e aquela data de
  referência deixa de aparecer na listagem.
- **RN-51** — Os **metadados de Execução nunca são expurgados**. A métrica primária tem janela
  de 30 dias e a retenção de artefato é de 7: os metadados precisam sobreviver ao artefato, ou
  a métrica passa a ser calculada sobre uma série truncada sem que ninguém perceba.
- **RN-38** — O **histórico de downloads não é expurgado**: sobrevive indefinidamente à
  remoção do dado que registrou e à inativação do relatório.
- **RN-39** — Uma solicitação de exportação de artefato já expurgado é recusada com mensagem
  explícita de indisponibilidade por retenção — nunca com erro genérico.

### 8.8 Erros e diagnóstico

- **RN-40** — Toda mensagem de erro exibida ao usuário contém: **momento do erro**,
  **descrição**, e **Correlation ID**.
- **RN-41** — O Correlation ID exibido ao usuário é o mesmo que permite localizar a
  ocorrência nos registros da operação.

---

## 9. Requisitos funcionais

Cada requisito é uma afirmação verificável por teste automatizado. O detalhamento em
Given/When/Then pertence a `docs/user-stories.md` — artefato P0 exigido pelo guia, ainda
inexistente.

> **Os identificadores são permanentes.** Um `RF-NN` nunca é renumerado nem reaproveitado:
> requisito novo recebe o próximo número livre, ainda que pertença a um grupo anterior;
> requisito descartado tem o ID **aposentado** e permanece listado como tal.

### Coleta

| ID | Requisito | Regras |
|---|---|---|
| **RF-01** | A apuração agendada executa todos os relatórios ativos e grava um artefato por relatório concluído com sucesso ou alerta | RN-06, RN-08 |
| **RF-02** | Cada execução registra data de referência, início, fim, duração, status e origem | RN-07, RN-09, RN-46 |
| **RF-03** | Execução que ultrapassa o tempo estimado sem falhas termina em `processado com alerta` | RN-11 |
| **RF-04** | Execução com qualquer falha termina em `processado com erro`, mesmo tendo ultrapassado o tempo estimado | RN-12 |
| **RF-05** | Relatório que atinge o dobro do seu tempo estimado é abortado e termina em `processado com erro`, **sem impedir a apuração dos demais relatórios do mesmo produto** | RN-13 |
| **RF-06** | Execução cujo processo termina de forma anômala é encerrada como `processado com erro`, sem permanecer em `em processamento` | RN-10 |
| **RF-07** | *Aposentado.* Cancelamento de execução saiu do escopo | — |
| **RF-08** | Nova execução de par com execução vigente em sucesso ou alerta é recusada, sem alterar artefatos nem criar execução | RN-16, RN-17, RN-18 |
| **RF-09** | Nova execução de par com execução vigente em erro é aceita normalmente | RN-19 |
| **RF-10** | O ADMINISTRADOR solicita reprocessamento forçado informando motivo; a execução anterior é invalidada e permanece registrada, e os artefatos são sobrescritos | RN-20, RN-21, RN-46 |
| **RF-11** | Solicitação de reprocessamento forçado sem motivo é rejeitada | RN-21 |
| **RF-12** | Usuário de perfil diferente de ADMINISTRADOR não consegue solicitar reprocessamento forçado | RN-20 |
| **RF-45** | A reserva do ciclo cria uma execução por relatório ativo antes de qualquer apuração começar | RN-45 |
| **RF-46** | Execução que falha origina nova execução de origem `retentativa`; a anterior permanece e passa a não-vigente | RN-15, RN-44, RN-46 |
| **RF-49** | A Coleta recusa apurar relatório cujo dataset ultrapasse o teto e registra erro com motivo explícito | RN-52 |
| **RF-52** | A métrica primária considera exclusivamente execuções vigentes de origem `agendada` | §6 |
| **RF-53** | Nenhuma interface aceita data de referência como entrada | RN-54 |

### Exportação e consulta

| ID | Requisito | Regras |
|---|---|---|
| **RF-13** | A listagem apresenta os relatórios disponíveis navegáveis por data → produto → relatório, exibindo a data em `dd/MM/yyyy` | RN-08 |
| **RF-14** | A listagem de um RELATOR contém exclusivamente os relatórios alcançados pela sua cadeia de permissão | RN-22, RN-23 |
| **RF-15** | Tentativa de acesso direto a relatório fora da permissão do usuário é negada | RN-23 |
| **RF-16** | A listagem de um RELATOR pendente de vínculo vem vazia, acompanhada da mensagem de aguardo | RN-28 |
| **RF-17** | O ADMINISTRADOR enxerga e exporta todos os relatórios | RN-24 |
| **RF-18** | O GERENTE não consegue exportar relatório algum | RN-25 |
| **RF-19** | A exportação devolve o arquivo no formato solicitado dentro da mesma requisição, sem criar registro de status | RN-30 |
| **RF-20** | Só é possível exportar relatório cuja execução vigente esteja em sucesso ou alerta | RN-42 |
| **RF-21** | A exportação em XLSX é entregue como planilha contínua, com o cabeçalho de coluna aparecendo **exatamente uma vez** | RN-33 |
| **RF-22** | A exportação em CSV entrega o dataset bruto da consulta principal | RN-34 |
| **RF-23** | Todo download é registrado e consultável pelo ADMINISTRADOR | RN-35, F09 |
| **RF-24** | Solicitação de artefato já expurgado é recusada com mensagem de indisponibilidade por retenção | RN-39 |
| **RF-48** | Exportação solicitada acima do limite de simultaneidade é recusada de imediato, com indicação de repetir mais tarde | RN-53 |
| **RF-51** | O histórico de downloads exibe os identificadores como estavam no momento do download, mesmo após edição de nome ou inativação | RN-35, RN-38, RN-50 |

### Catálogo

| ID | Requisito | Regras |
|---|---|---|
| **RF-25** | *Aposentado.* Cadastro de produto pela aplicação não existe | RN-49 |
| **RF-26** | *Aposentado.* Cadastro de relatório pela aplicação não existe | RN-49 |
| **RF-27** | Todo relatório tem tempo estimado em segundos maior que zero, validado na inicialização e na edição | RN-04 |
| **RF-28** | *Aposentado.* Substituído por RF-50 | RN-05 |
| **RF-41** | Nome do produto, e nome, descrição e tempo estimado do relatório são editáveis; sigla e código não | RN-01, RN-02, RN-04 |
| **RF-42** | *Aposentado.* Não há remoção de relatório a bloquear — há inativação | RN-50 |
| **RF-43** | Remoção de role de relatório ou de grupo revoga o acesso concedido por eles, sem afetar outros caminhos da cadeia | RN-22 |
| **RF-44** | A inicialização de um produto é recusada se dois de seus relatórios declararem o mesmo código | RN-03, RN-49 |
| **RF-47** | A edição do tempo estimado é recusada se fizer a soma do produto ultrapassar o teto | RN-48 |
| **RF-50** | A inativação de um produto é recusada enquanto houver relatório ativo nele | RN-05 |
| **RF-54** | Relatório e produto inativados desaparecem da listagem e permanecem referenciáveis por execuções, auditoria e downloads | RN-50 |

### Identidade e acesso

| ID | Requisito | Regras |
|---|---|---|
| **RF-29** | O autocadastro público cria usuário RELATOR pendente de vínculo | RN-27 |
| **RF-30** | O autocadastro não permite escolher perfil nem criar GERENTE/ADMINISTRADOR | RN-27 |
| **RF-31** | O GERENTE cria roles de relatório e as vincula a relatórios e a grupos | RN-22 |
| **RF-32** | O GERENTE inclui e remove usuários RELATOR em grupos | RN-22 |
| **RF-33** | Usuário em múltiplos grupos acessa a união dos relatórios de todos eles | RN-22 |
| **RF-34** | O GERENTE não consegue criar nem promover usuário a GERENTE ou ADMINISTRADOR, nem por manipulação direta da requisição | RN-26 |
| **RF-35** | ADMINISTRADOR e GERENTE conseguem remover um usuário RELATOR | matriz §3.2 |
| **RF-36** | Removido o vínculo de um usuário, seu acesso aos relatórios correspondentes cessa imediatamente | RN-22 |
| **RF-37** | Qualquer usuário troca a própria senha e recupera senha esquecida por e-mail | RN-29 |
| **RF-55** | Um RELATOR sem grupo não alcança relatório algum — não há acesso concedido por omissão | RN-22, §5 |

### Erros

| ID | Requisito | Regras |
|---|---|---|
| **RF-38** | Toda mensagem de erro exibe momento, descrição e Correlation ID | RN-40 |
| **RF-39** | A tela de erro oferece a cópia do erro em formato estruturado, para anexar em chamado | RN-40 |
| **RF-40** | O Correlation ID exibido localiza a ocorrência nos registros da operação | RN-41 |

---

## 10. Requisitos não-funcionais

> Os números foram **recalibrados nesta revisão** contra a máquina alvo — 23 GB de RAM,
> 4 vCPUs e ~20 GB livres em disco (RA-50: a primeira versão executa em ambiente local).
> Os valores anteriores (250 MB de artefato, 500 mil linhas, 10 exportações simultâneas) não
> cabiam: implicavam ~10 GB de heap só de artefatos vivos e 17,5 GB de disco só de retenção.
> Continuam marcados `PROVISÓRIO` porque devem ser **medidos**, não estimados — há ticket de
> calibração com `k6` para substituí-los por números observados.

| ID | Requisito | Valor |
|---|---|---|
| **RNF-01** | Relatórios no MVP | 10 (2 por produto, 5 produtos) `PROVISÓRIO` |
| **RNF-02** | Teto de relatórios por produto | 9.999 — decorre de RN-03, não é provisório |
| **RNF-03** | Execuções por ciclo diário | 10 `PROVISÓRIO` |
| **RNF-04** | Janela do ciclo de coleta | Concluir em até 60 min — **garantida por RN-48**, não estimada |
| **RNF-05** | Tamanho máximo de artefato | **25 MB** `PROVISÓRIO` |
| **RNF-06** | Volume máximo do dataset de um relatório | **50.000 linhas** `PROVISÓRIO` |
| **RNF-07** | Latência de exportação, p95 — PDF | ≤ 15 s `PROVISÓRIO` |
| **RNF-08** | Latência de exportação, p95 — XLSX e DOCX | ≤ 25 s `PROVISÓRIO` |
| **RNF-09** | Latência de exportação, p95 — CSV | ≤ 5 s `PROVISÓRIO` |
| **RNF-10** | Exportações simultâneas suportadas | **2** `PROVISÓRIO` |
| **RNF-11** | Usuários simultâneos | 25 `PROVISÓRIO` — navegar não é exportar |
| **RNF-12** | Janela de retenção dos artefatos | 7 dias, configurável — RN-36 |
| **RNF-13** | Retenção do histórico de downloads | Indefinida — RN-38 |
| **RNF-14** | Fuso horário de todo o sistema | `America/Sao_Paulo` |
| **RNF-15** | Idioma da interface e das mensagens | pt-BR |
| **RNF-16** | Retenção dos metadados de execução | **Indefinida** — RN-51 |
| **RNF-17** | Retentativas por relatório em um ciclo | **2** `PROVISÓRIO` |
| **RNF-18** | Produtos apurados simultaneamente | **2** `PROVISÓRIO` — 4 vCPUs disputados por toda a pilha |
| **RNF-19** | Teto da soma dos tempos estimados de um produto | **10 min** — deriva de RNF-04 e RNF-18: 5 produtos em ondas de 2 dão 3 ondas de 20 min |
| **RNF-20** | Horário do ciclo | **03h00**, diário, todos os dias — **fixo em código**, ver nota |

> **Onde o horário do ciclo é declarado.** O agendamento de RNF-20 vive na **declaração da DAG do
> orquestrador**, no mono repositório — é código, e não configuração. Não é editável pela aplicação
> (§5) nem por variável de ambiente, ao contrário da janela de retenção (RNF-12), que é. Mudar o
> horário é alterar a DAG e publicar; a mudança entra por PR e CI, como a task estática de um produto
> (`arquitetura-inicial.md`, RA-65 e RA-56; ticket
> [14](./implementacao/issues/14-dag-reserva-pool-e-callback-de-falha.md)).
>
> Duas consequências que a alteração arrasta, e que existem por isto estar registrado aqui:
>
> 1. **O rótulo da data muda de sentido.** A data de referência é o dia do disparo (RN-07), e é a
>    madrugada que faz o artefato de um dia conter o movimento fechado do anterior. Deslocar o ciclo
>    para outro período do dia altera esse significado sem que métrica alguma detecte.
> 2. **A janela de retenção efetiva muda.** O expurgo de RN-36 foi verificado com o ciclo às 03h00
>    BRT (`arquitetura-inicial.md`, RA-20); outro horário desloca o instante da remoção e exige
>    refazer a conta que garante que a retenção nunca fica menor que 7 dias.

---

## 11. Decisões assumidas

### 11.1 Revisão anterior (D01–D19)

Permanecem válidas, com uma exceção anotada.

| # | Decisão | Situação |
|---|---|---|
| D01 | O PRD trata só do eixo de produto | Vigente |
| D02 | **O erro sempre prevalece sobre o alerta** | Vigente — RN-11, RN-12 |
| D03 | Data de referência ≠ data de execução | Vigente, com D25 estreitando-a |
| D04 | ADMINISTRADOR com acesso irrestrito, com teste dedicado | Vigente — RN-24 |
| D05 | Unicidade vale para a execução **vigente**; a recusa é evento de auditoria | Vigente — reforçada por D28 |
| D06 | Quinto status terminal `cancelado` | **REVERTIDA por D20** |
| D07 | Sigla imutável separada do Nome editável | Vigente |
| D08 | Cadeia N:N em todos os elos, acesso pela união | Vigente — RN-22 |
| D09 | O ciclo de status vale só para a Execução da Coleta | Vigente — RN-30 |
| D10 | Reprocessamento forçado é funcionalidade autenticada e auditada | Vigente — F04 |
| D11 | Retenção é regra de negócio, não configuração | Vigente, ampliada por D29 |
| D12 | Sucesso e alerta bloqueiam nova execução; erro libera | Vigente |
| D13 | Taxa de apuração limpa como métrica primária | Vigente, redefinida por D27 |
| D14 | ADMINISTRADOR também remove RELATOR | Vigente |
| D15 | Listagem filtrada por permissão, inclusive contra acesso direto | Vigente |
| D16 | Autocadastro só de RELATOR, sem verificação nem moderação | Vigente |
| D17 | Tempo estimado obrigatório | Vigente |
| D18 | PDF e DOCX paginam; XLSX ignora paginação; CSV é dataset bruto | Vigente, com o custo revelado por D24 |
| D19 | Metas não-funcionais numéricas e provisórias | Vigente, recalibradas por D26 |

### 11.2 Esta revisão (D20–D33)

| # | O que estava ambíguo ou errado | Decisão | Onde |
|---|---|---|---|
| **D20** | O cancelamento (F03/RN-14) operava sobre o relatório, mas a unidade de execução é o produto — a regra não era implementável | **Cancelamento removido do escopo.** O único interruptor é o limite de tempo. Reverte D06 e elimina o status `cancelado` | §5, RN-13 |
| **D21** | "A base é lida uma vez por dia" tinha duas leituras incompatíveis | **Leitura literal:** uma janela de leitura por produto. O que se conta é a leitura **bem-sucedida** por relatório; retentativa relê só o que faltou | RN-44 |
| **D22** | A retentativa reabriria a base no mesmo dia, contra D21, e RN-19 exigia poder refazer | "Uma vez" = uma leitura **bem-sucedida**. Retentativa permitida e limitada (RNF-17). **Preço aceito:** relatórios do mesmo produto podem ver instantes diferentes da base | RN-44 |
| **D23** | RN-13 media o relatório, mas o orquestrador só enxerga o produto | **Dois limites com papéis distintos:** o do relatório é regra de negócio; o do produto é interruptor de emergência | RN-13 |
| **D24** | Não se dizia quando a Execução passa a existir — e sem isso a métrica mentiria | **Reserva do ciclo** antes de qualquer apuração | RN-45 |
| **D25** | RN-07 admitia data de referência informada, o que exigiria base transacional temporal | **Sem retroatividade.** A data nunca é informada | RN-54 |
| **D26** | RNF-04 era um desejo; nada impedia o catálogo de estourá-la. E os números de §10 não cabiam na máquina | **RNF-04 vira validação de catálogo** (RN-48) e os limites são recalibrados | RN-48, §10 |
| **D27** | O denominador da métrica não dizia se contava tentativas ou entregas | Conta **execuções vigentes agendadas**; retentativa ganha métrica própria; a meta é 98% = 6 sujas/mês (a versão anterior dizia 98% e "3 sujas", que não fecha) | §6 |
| **D28** | RN-15 proíbe mudar status terminal, mas a retentativa precisaria reabrir a execução | **Execução imutável; "vigente" é ponteiro.** Tempo estimado copiado para dentro dela | RN-46, RN-47 |
| **D29** | Havia dois ciclos de vida declarados; são três, e a métrica de 30 dias dependia do terceiro | **Artefato 7 dias, Execução nunca, Download indefinido.** Catálogo por inativação, não remoção | RN-50, RN-51 |
| **D30** | F10/F11 permitiam cadastrar produto e relatório, mas ambos são código | **Catálogo derivado do código.** A aplicação edita e inativa; não cria | RN-49 |
| **D31** | Nada impunha RN-26 além de uma verificação no código | Perfil e Role de relatório passam a ser objetos de **tipos distintos** — a separação é estrutural | RN-26 |
| **D32** | Q7 perguntava por relatório visível a todos | **Não.** Sem grupo padrão; todo acesso passa pela cadeia | §5, RF-55 |
| **D33** | RN-33 prometia XLSX sem cabeçalho repetido, o que não é configuração de exportação | Mantido, com o custo explicitado: **convenção de autoria** obrigatória em todo modelo, com teste por relatório | RN-33, RF-21 |

---

## 12. Questões em aberto

As nove questões da revisão anterior foram respondidas.

| # | Questão | Resposta | Onde |
|---|---|---|---|
| Q1 | Periodicidade e horário da coleta | Diária, 03h00, `America/Sao_Paulo` | RNF-20 |
| Q2 | O que acontece ao remover um relatório | Não há remoção — há inativação, e nada é destruído | RN-50 |
| Q3 | Alterar o tempo estimado reclassifica o passado? | Não. O valor é copiado para a Execução | RN-47 |
| Q4 | Reprocessamento forçado alcança data passada? | Não. Só a data corrente | RN-20, RN-54 |
| Q5 | Os números provisórios valem? | Não valiam. Recalibrados, com ticket de medição | §10 |
| Q6 | A meta é 98% ou 99%? | **98%**, e a frase explicativa passa a dizer 6 sujas/mês | §6 |
| Q7 | Relatório visível a todo RELATOR? | Não | §5, RF-55 |
| Q8 | A retenção conta da data de referência ou da gravação? | São a mesma data, dado que não há retroatividade | RN-54, RN-36 |
| Q9 | Execuções paralelas ou sequenciais? | Paralelas, 2 produtos por vez, com teto de catálogo | RNF-18, RN-48 |

**Ainda em aberto**

| # | Questão | Precisa ser resolvida antes de |
|---|---|---|
| **Q10** | Os limites recalibrados de §10 resistem à medição real? | Fechar os critérios de aceite não-funcionais |
| **Q11** | A meta de 98% é adequada depois do primeiro mês de operação? | Encerrar o `PROVISÓRIO` da métrica primária |

---

## 13. Documentos relacionados

| Documento | Papel | Estado |
|---|---|---|
| [`glossario.md`](./glossario.md) | Linguagem ubíqua. Fonte única das definições | Existe |
| [`arquitetura-inicial.md`](./arquitetura-inicial.md) | Eixo de engenharia: stack, módulos, decisões (`RA-NN`), trade-offs | Existe |
| [`adr/`](./adr/) | Uma decisão estruturante por arquivo | Existe |
| [`guias/guia-app-web.md`](./guias/guia-app-web.md) | Método: define os artefatos exigidos em cada fase | Existe |
| [`especificacao.md`](./especificacao.md) | Spec do MVP: histórias, decisões de implementação e costuras de teste | Existe |
| `user-stories.md` | Histórias com critério de aceite em Given/When/Then | Coberto por [`especificacao.md`](./especificacao.md) §3, com os `RF-NN` deste PRD como critério; o Given/When/Then vive nos `.feature` (E3) |
| `design/fluxos.md` | Os fluxos principais com estados de erro | **Não existe** — P1 |
