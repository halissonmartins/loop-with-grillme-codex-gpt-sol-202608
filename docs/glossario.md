# Glossário — Scheduler Jasper Report

Linguagem ubíqua do projeto. Artefato **P0** do [`guias/guia-app-web.md`](./guias/guia-app-web.md).

Este é o **único** lugar onde um termo do domínio é definido. O [`prd.md`](./prd.md) e a
[`arquitetura-inicial.md`](./arquitetura-inicial.md) usam os termos daqui e não os redefinem —
duas fontes para a mesma definição divergem em uma semana.

Regras deste arquivo:

- Uma definição por termo, em uma ou duas frases. Define **o que a coisa é**, não o que ela faz.
- Quando existe mais de uma palavra para o mesmo conceito, uma é escolhida e as outras entram
  em **Evite**.
- Nada de detalhe de implementação. Tabela, classe, endpoint e contêiner não moram aqui.

---

## Catálogo

**Produto**
Domínio de negócio com base transacional própria — Poupança, Cliente, Conta Corrente, Consórcio
e Empréstimo. O conjunto de produtos é **fechado e definido em código**: não se cria produto pela
aplicação, porque um produto sem apuração própria é uma sigla que nunca produz relatório.
*Evite:* sistema, módulo de negócio, carteira.

**Sigla**
Identificador do produto, `^[A-Z]{1,20}$`. Declarada em código, **nunca derivada do nome** e
imutável. Compõe o código do relatório e o caminho de armazenamento do artefato.
*Evite:* código do produto, abreviação.

**Nome do produto**
Texto de exibição, editável a qualquer momento. Não aparece em identificador algum.
*Evite:* usar como sinônimo de Sigla.

**Relatório**
A **definição** de uma apuração: código, nome, descrição, produto e tempo estimado. Existe uma
vez e origina muitas Execuções. Assim como o Produto, o conjunto de relatórios é definido em
código — a aplicação edita atributos, não cria nem remove relatórios.
*Evite:* extrato, arquivo, listagem.

**Código do relatório**
`SIGLA-NNNN`, regex `^[A-Z]{1,20}-\d{4}$`. Os quatro dígitos são únicos **dentro do produto**.
Imutável.
*Evite:* id do relatório, número do relatório.

**Tempo estimado de execução**
Duração esperada da apuração de um relatório, em segundos inteiros e maior que zero. Governa o
alerta de degradação e o limite de interrupção. É editável, porque é ajuste de operação.
*Evite:* SLA, duração, tempo médio.

**Catálogo**
O conjunto de produtos e relatórios conhecidos pelo sistema. É **derivado do código**: cada
produto anuncia os seus relatórios ao iniciar, e a aplicação guarda apenas o que é mutável.
*Evite:* cadastro, registro de relatórios.

**Inativação**
Retirada de um produto ou relatório do catálogo visível. Nada é apagado: execuções, auditoria e
histórico de downloads continuam a referenciar o item inativado.
*Evite:* remoção, exclusão, delete.

---

## Coleta

**Coleta**
A metade agendada do sistema: a apuração diária dos relatórios a partir das bases transacionais.
É a **única fronteira de leitura** dessas bases.
*Evite:* carga, ETL, processamento.

**Ciclo**
Uma rodada completa da Coleta, correspondente a uma data de referência. Cobre todos os
relatórios ativos de todos os produtos.
*Evite:* rodada, batch, job.

**Janela de leitura**
A abertura da base transacional de um produto pela Coleta. Existe no máximo **uma janela
bem-sucedida por relatório por dia**; uma janela que falhou não entregou nada e pode ser
reaberta pela retentativa, que relê apenas o que não concluiu.
*Evite:* conexão, sessão, acesso à base.

**Leitura única**
A promessa central do sistema: nenhuma leitura das bases transacionais fora da Coleta, e nenhuma
releitura do que já produziu artefato válido. Não é uma promessa sobre o número de consultas.
*Evite:* "ler uma vez por dia" sem qualificação.

**Data de referência**
`yyyy-MM-dd`. O dia a que a apuração pertence, e o dia em que o Ciclo foi disparado. Particiona o
armazenamento e compõe a chave de unicidade.
**Atenção, e isto não é detalhe:** o Ciclo roda de madrugada, então o artefato carimbado com
09/08 contém o movimento **fechado de 08/08**. O rótulo é o dia da apuração, não o dia do
movimento.
*Evite:* data do relatório, competência, data de processamento.

**Data/hora de execução**
Quando a apuração de fato rodou. Serve à auditoria e à medição de duração. Não particiona nada.
*Evite:* usar como sinônimo de Data de referência.

**Reserva do ciclo**
A criação antecipada de uma Execução para cada relatório ativo, antes de qualquer apuração
começar. É o que impede que um relatório não apurado desapareça das métricas em vez de aparecer
como falha.
*Evite:* agendamento, enfileiramento.

**Execução**
Uma apuração do par *Relatório + Data de referência*. É um **registro imutável de um evento**:
uma vez concluída, não muda mais. Carrega status, início, fim, duração, origem e uma cópia do
tempo estimado vigente quando foi disparada.
*Evite:* processamento, run, tarefa.

**Execução vigente**
A Execução que **vale** para um par *Relatório + Data de referência*. É um ponteiro, não um
status: existem execuções não-vigentes — as que falharam antes de uma retentativa, e as
invalidadas por reprocessamento forçado.
*Evite:* execução atual, última execução.

**Origem da execução**
Por que a Execução existe: `agendada`, `retentativa` ou `reprocessamento forçado`. Sem esta
distinção, a métrica de apuração e a métrica de instabilidade se contaminam.
*Evite:* tipo de execução, motivo.

**Retentativa**
Nova Execução de um par cuja execução anterior falhou. Não reabre a execução anterior — cria uma
nova, porque uma Execução em status terminal não muda mais.
*Evite:* reprocessamento (esse termo é reservado ao forçado), retry.

**Reprocessamento forçado**
Nova apuração de um par que **já concluiu com sucesso ou alerta**, solicitada por um
ADMINISTRADOR com motivo obrigatório. Invalida a execução anterior e sobrescreve os artefatos.
*Evite:* refazer, reprocessar, rerun.

**Artefato**
O resultado gravado de uma Execução bem-sucedida. É o que a Exportação lê e o único insumo dela.
*Evite:* arquivo, saída, output.

**Expurgo**
A remoção automática dos artefatos após a janela de retenção. Atinge **só os artefatos** — a
Execução e o histórico de downloads sobrevivem a ele.
*Evite:* limpeza, purge, exclusão.

---

## Exportação

**Exportação**
Conversão síncrona e sob demanda de um Artefato para um formato de entrega — PDF, XLSX, DOCX ou
CSV. Nunca é armazenada e nunca tem status: o ciclo de vida de status pertence exclusivamente à
Execução.
*Evite:* geração, conversão, render.

**Download**
A entrega do arquivo exportado a um usuário. É registrado com uma cópia dos identificadores do
momento, e o registro **sobrevive** ao expurgo do Artefato e à inativação do Relatório.
*Evite:* usar como sinônimo de Exportação.

---

## Acesso

**Perfil**
Um de três: ADMINISTRADOR, GERENTE, RELATOR. Conjunto **fechado**, definido em código. Um
usuário tem exatamente um perfil.
*Evite:* papel, role, permissão.

**Role de relatório**
Permissão nomeada, criada pelo GERENTE, que agrupa acesso a relatórios. Conjunto **aberto**. É
objeto de tipo diferente do Perfil, e essa diferença é o que impede um GERENTE de se promover.
*Evite:* permissão, grupo de relatórios, perfil.

**Grupo**
Coleção de usuários que recebe roles de relatório. É o elo entre a permissão e a pessoa.
*Evite:* time, equipe, unidade.

**Cadeia de permissão**
O caminho **Relatório → Role de relatório → Grupo → Usuário**, com todos os elos N:N. O acesso
efetivo é a união de todos os caminhos. O ADMINISTRADOR é a única exceção: não passa por ela.
*Evite:* hierarquia de acesso, árvore de permissões.

**Pendente de vínculo**
RELATOR que se autocadastrou e ainda não pertence a nenhum grupo. Entra na aplicação e não
alcança nenhum relatório.
*Evite:* usuário inativo, usuário bloqueado.

---

## Diagnóstico

**Correlation ID**
O identificador exibido ao usuário na mensagem de erro e que localiza a mesma ocorrência nos
registros da operação. É um só, do começo ao fim da requisição.
*Evite:* id do erro, protocolo, ticket.
