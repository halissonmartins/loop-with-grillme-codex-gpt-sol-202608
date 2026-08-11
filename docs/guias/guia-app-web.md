# Guia Implementação Aplicação Web 

Guia de implementação organizado em dois eixos paralelos: **Produto/Design** e **Engenharia**.

---

## Como ler este guia

Os dois eixos não são sequenciais. Correm em paralelo, respondem a perguntas diferentes e se encontram em pontos de sincronização definidos.

| | Eixo Produto/Design | Eixo Engenharia |
|---|---|---|
| **Pergunta** | Isso resolve o problema e é usável? | Isso é construível, seguro e sustentável? |
| **Risco que mitiga** | Construir a coisa errada | Construir errado a coisa |
| **Artefato central** | `design-system.md` | `CLAUDE.md` |
| **Ciclo** | Divergir → prototipar → validar → convergir | Especificar → implementar → verificar → entregar |
| **Descartável?** | Wireframes e protótipos, sim | PoCs e spikes, sim; o resto, não |

Confundir os eixos é a origem dos dois erros mais caros: começar a codificar sem saber o que se está construindo, e prototipar indefinidamente sem nunca colocar nada em produção.

### Princípio orientador

- Documentação é entrada do sistema de produção 
- Testes são o contrato que impede o agente de quebrar o que já existe 
- Revisão é o único ponto onde erro é interceptado 
- Protótipo custa minutos, então pular é injustificável 
- Especificação e verificação são os artefatos caros 

Consequência prática: Guardrails determinísticos — no eixo de engenharia (tipos, lint, testes, CI) e no eixo de design (design system, tokens, componentes canônicos) — precisam existir *antes* da primeira feature.

---

# Parte I — Eixo de Produto e Design

## P0. Enquadramento

**Objetivo:** definir o que será construído antes de abrir um editor.

**Você faz:** decide o problema, o usuário e o corte do MVP.
**O agente faz:** estrutura sua descrição solta, questiona ambiguidades, deriva user stories do escopo.

### Artefatos

| Artefato | Conteúdo mínimo |
|---|---|
| `docs/prd.md` | Problema, usuário-alvo, escopo do MVP, **fora de escopo explícito**, métrica de sucesso |
| `docs/glossario.md` | Termos do domínio com definição única (linguagem ubíqua) |
| `docs/user-stories.md` | Histórias com critério de aceite testável (Given/When/Then) |

> **Por que o glossário importa mais:** não intui que "pedido", "ordem" e "compra" são a mesma entidade no seu domínio. Se você não fixar, pode criar três modelos — e o eixo de engenharia herda a ambiguidade como três tabelas.

**Checkpoint:** cada história tem critério de aceite que um teste automatizado conseguiria verificar. Se não conseguir, a história está vaga demais para ser delegada.

---

## P1. Fluxos e prototipação

**Objetivo:** responder "é usável e desejável?" antes de existir backend.

Define os fluxos principais, julga o resultado, decide o que fica, gera variações de wireframe e protótipo navegável em minutos, itera sob crítica.

### Fidelidade proporcional ao risco

| Situação | Fidelidade adequada |
|---|---|
| Fluxo crítico, ambíguo ou inédito | Protótipo navegável, testado com usuário real |
| Tela com muita regra de negócio | Wireframe + fluxo escrito |
| CRUD interno, tela administrativa | Nenhuma — vá direto ao código |

Prototipar o que não tem ambiguidade é a versão de design da PoC desnecessária: procrastinação disfarçada de diligência.

### Artefatos

| Artefato | Conteúdo mínimo |
|---|---|
| `docs/design/fluxos.md` | Os 3–5 fluxos principais do usuário, passo a passo, com estados de erro |
| Wireframes de baixa fidelidade | Estrutura antes de estética; descartáveis |
| Protótipo navegável | Figma ou código de fachada, sem backend real |
| `docs/design/decisoes-ux.md` | Por que o fluxo é esse, o que foi descartado e por quê |

> **Protótipo é descartável.** Código de protótipo gerado por agente é especialmente tentador de promover, porque já *parece* funcionar. Não tem validação, autorização, tratamento de erro nem testes. Promovê-lo é nascer com dívida técnica no dia zero — no único momento em que ela era evitável.

**Checkpoint:** você consegue percorrer os fluxos críticos ponta a ponta no protótipo e explicar cada tela sem hesitar.

---

## P2. Design system

**Objetivo:** fixar a linguagem visual antes que o agente a improvise.

Este é o artefato que mais falta em projetos. Sem ele, cada sessão inventa espaçamento, cor e comportamento de formulário — e você descobre a inconsistência só quando já tem vinte telas.

### Artefatos

| Artefato | Conteúdo mínimo |
|---|---|
| `docs/design/design-system.md` | Tokens (cor, tipografia, espaçamento, raio, sombra), regras de uso |
| Componentes canônicos implementados | Um exemplo real de botão, input, formulário, tabela, modal |
| Padrões de estado | Loading, vazio, erro, sucesso, desabilitado — definidos uma vez |
| Padrões de acessibilidade | Contraste, foco visível, navegação por teclado, rótulos |

### `design-system.md` — a constituição da interface

É para o frontend o que o `CLAUDE.md` é para o backend: o documento que o agente lê antes de gerar UI.

```markdown
# Design System

## Tokens
- Cores: [paleta, com nome semântico — primary, danger, surface...]
- Tipografia: [escala, pesos, famílias]
- Espaçamento: [escala, ex.: 4/8/12/16/24/32]
- Raio e sombra: [valores permitidos]

## Regras
- Nunca usar valor de cor ou espaçamento fora dos tokens
- Todo formulário segue o padrão em [caminho do componente canônico]
- Todo estado de carregamento usa [componente]
- Toda mensagem de erro aparece em [posição e formato]

## Acessibilidade obrigatória
- Contraste mínimo AA
- Foco visível em todo elemento interativo
- Todo input tem label associado

## Componentes canônicos
[lista com caminho no repositório — o agente copia o padrão que encontra]
```

**Checkpoint:** existe um componente real no repositório para cada padrão descrito. Documento sem implementação de referência não é seguido pelo agente.

---

## P3. Validação contínua

**Objetivo:** manter o eixo de produto vivo depois do primeiro release.

Roda em paralelo ao loop de engenharia (E4), não depois dele.

### Artefatos

| Artefato | Descrição |
|---|---|
| Métrica de sucesso instrumentada | A definida no PRD, medida de verdade |
| Registro de feedback de usuário | Fonte das próximas histórias |
| Backlog priorizado | Vivo, revisado a cada ciclo |
| Atualização do `design-system.md` | Sempre que um padrão novo se estabelecer |

---

# Parte II — Eixo de Engenharia

## E0. Decisões técnicas e spikes

**Objetivo:** eliminar incógnitas técnicas e registrar escolhas.

**Você faz:** define stack, hospedagem, banco, modelo de autenticação.
**O agente faz:** compara opções, gera PoCs descartáveis para cada risco, escreve os ADRs a partir da conversa.

### Artefatos

| Artefato | Conteúdo mínimo |
|---|---|
| `docs/adr/NNNN-titulo.md` | Um ADR por decisão relevante: contexto, opções, decisão, consequências |
| `docs/arquitetura/c4-contexto.md` | Diagrama C4 nível 1 e 2 em Mermaid (versionável; o agente lê e atualiza) |
| `docs/riscos.md` | Riscos técnicos abertos e como cada um foi ou será resolvido |

### ADRs mínimos para uma app web

1. Linguagem e framework (backend e frontend)
2. Banco de dados e estratégia de migrations
3. Autenticação e autorização
4. Ambiente
5. Monolito modular vs. serviços separados (comece monolito, salvo prova em contrário)
6. Estratégia de testes (o que é unitário, integração, e2e)

> **PoC é descartável, como o protótipo.** Se o spike do agente "deu certo", jogue fora e reimplemente dentro das fundações. Nem todo projeto precisa de PoC: sem incógnita técnica real, ela é procrastinação.

### Três artefatos de arquitetura, três funções

São frequentemente confundidos. Nenhum substitui o outro.

| Artefato | Pergunta que responde | Natureza | Atualização |
|---|---|---|---|
| **ADR** | *Por que* decidimos assim? | Imutável, ponto no tempo | Nunca se edita; supersede-se |
| **C4** | Como o sistema se relaciona com o mundo e quais são os contêineres? | Visual, nível de sistema, olha para fora | Quando muda um contêiner |
| **`ARCHITECTURE.md`** | *Onde* eu mexo para fazer X? | Mapa do código, olha para dentro | Uma ou duas vezes por ano |

O terceiro é criado em E1, preenchido em E3 e mantido em E4.

---

## E1. Fundações do repositório (Sprint 0)

**Objetivo:** montar os trilhos antes de qualquer feature. **É a fase mais importante do eixo de engenharia.**

### Artefatos

| Artefato | Função |
|---|---|
| `README.md` | Como rodar o projeto em 3 comandos |
| `CLAUDE.md` / `CLAUDE.md` | Constituição técnica do projeto (ver abaixo) |
| `ARCHITECTURE.md` | Mapa do código e invariantes; versão esquelética nesta fase (ver abaixo) |
| `.editorconfig`, linter, formatter | Padrão de código não negociável |
| Tipagem estrita (ex.: `tsconfig.json` com `strict: true`) | Primeiro filtro contra código alucinado |
| `docker-compose.yml` | Banco e dependências locais idênticos a produção |
| `.env.example` | Todas as variáveis, nenhum segredo real |
| `Makefile` ou scripts | `setup`, `dev`, `test`, `lint`, `build`, `migrate` |
| `.github/workflows/ci.yml` | Lint + tipos + testes + build a cada PR, bloqueante |
| Pre-commit hooks | Bloqueia commit que não passa no lint |
| Estrutura de pastas com 1 exemplo por camada | O agente copia o padrão que encontra |
| `.gitignore` + scanner de segredos | Evita chave de API commitada pelo agente |

### `CLAUDE.md` — a constituição técnica

```markdown
# Instruções do projeto

## Stack
[linguagens, frameworks, versões]

## Comandos
- Instalar: `make setup`
- Rodar: `make dev`
- Testar: `make test`
- Lint: `make lint`

## Onde as coisas ficam
- Leia `ARCHITECTURE.md` antes de criar arquivo novo
- Não duplicar aqui o que está lá

## Convenções
- Padrão de nomenclatura
- Como escrever testes (framework, localização, padrão de nome)
- Padrão de tratamento de erro
- Padrão de commit

## Design
- Toda UI segue `docs/design/design-system.md`
- Nunca introduzir valor de cor ou espaçamento fora dos tokens

## Regras invioláveis
- Nunca commitar segredos
- Nunca alterar migration já aplicada
- Nunca desabilitar regra de lint ou teste para fazer o build passar
- Toda mudança de schema exige migration
- Toda rota nova exige teste de autorização

## Fora de escopo
[o que o agente não deve tocar sem autorização explícita]
```

### `ARCHITECTURE.md` — o mapa do código

Responde "onde eu mexo para fazer X?", não "como o módulo Y funciona por dentro". Nesta fase nasce esquelético: bird's eye view, estrutura pretendida e os invariantes que já saíram dos ADRs. É preenchido de verdade em E3.

```markdown
# Arquitetura

## Visão geral
[Um parágrafo: o que este sistema faz e qual problema resolve.]

## Bird's eye view
[1–3 parágrafos: como o dado atravessa o sistema, do request ao banco e de volta.]

## Code map
[Por módulo/pasta: o que faz e o que NÃO faz. Cite nomes, não links — links envelhecem.]

- `src/domain/` — regras de negócio puras. Não conhece HTTP nem banco.
- `src/api/` — rotas e validação de entrada. Não contém regra de negócio.
- `src/infra/` — acesso a banco e serviços externos.
- ...

## Invariantes arquiteturais
[O mais valioso do documento. Escreva as PROIBIÇÕES.]

- A camada de domínio não importa nada de `infra/`
- Nenhum acesso ao banco fora de `infra/repositories/`
- Nenhuma rota acessa o banco diretamente
- ...

## Fronteiras entre camadas
[Onde estão as costuras e o que atravessa cada uma.]

## Pontos de entrada
[Arquivos por onde começar a ler.]
```

> **Por que isso importa dobrado:** o problema que o documento resolve — "sou novo aqui, onde fica a coisa que faz X?" — é a situação do agente em **toda sessão nova**. Sem o mapa, ele busca por palavra-chave, acha um padrão qualquer e cria o arquivo no lugar errado.
>
> E os invariantes são o item que mais falta: são expressos como a *ausência* de algo, e não consegue inferir uma proibição a partir da falta de exemplos. Se não estiver escrito, ele viola.

**Limite de tamanho:** algumas centenas de linhas, não milhares. Peça a um agente para gerar este arquivo e ele produz 800 linhas descrevendo cada função — o que envelhece em uma semana e vira ruído.

Referência canônica: https://matklad.github.io/2021/02/06/ARCHITECTURE.md.html

**Checkpoint:** clone limpo em máquina nova roda com um comando e o CI está verde. Só então comece features.

---

## E2. Contratos antes do código

**Objetivo:** fixar as fronteiras onde o agente mais improvisa.

### Artefatos

| Artefato | Por quê |
|---|---|
| Schema do banco + primeira migration | Sem schema fixo, cada sessão inventa colunas |
| `openapi.yaml` ou schema equivalente | Contrato entre frontend e backend; permite gerar tipos |
| Tipos gerados a partir do schema | Erro de contrato vira erro de compilação, não bug em produção |
| Seed de dados de desenvolvimento | Ambiente reproduzível para você e para o agente |

> Contratos explícitos são o mecanismo mais eficaz contra deriva de arquitetura em sessões longas — o equivalente técnico do que os tokens fazem pela UI.

---

## E3. Esqueleto do Projeto e seus Módulos 

**Objetivo:** uma fatia vertical fina do projeto e seus módulos.

### Artefatos

| Artefato | Descrição |
|---|---|
| Projeto e módulos compilando | Prova que o esqueleto de cada módulo está correto |
| Health check | Prova que o módulo inicializa com sucesso |
| `ARCHITECTURE.md` preenchido | Code map, agora que existe código no projeto |
| `*.feature` criados | Traduzem regras de negócio abstratas em exemplos concretos e estruturados |

---

## E4. Loop de implementação

A partir daqui, repetição do mesmo ciclo por feature.

```
Issue com critério de aceite (vem de P0) + referência de design (vem de P1/P2)
        ↓
Agente propõe plano  →  você aprova/corrige o PLANO, não o código
        ↓
Agente escreve teste que falha
        ↓
Agente implementa até o teste passar
        ↓
CI: lint + tipos + testes + build
        ↓
Você revisa o diff  →  aprova ou devolve
        ↓
      Merge
```

### Regras operacionais

- **Uma issue, um PR, uma sessão.** Sessões longas acumulam contexto ruim e o agente passa a contradizer decisões anteriores.
- **PR pequeno.** Se você não revisa em 15 minutos, o escopo estava errado. Diff grande não é revisado — é aprovado no escuro.
- **Aprove o plano, não só o resultado.** Corrigir direção custa uma frase; corrigir 800 linhas custa uma tarde.
- **Nunca faça merge de código que você não leu.**
- **Teste antes da implementação.** Se o agente escreve o teste depois, ele escreve um teste que passa — não um teste que verifica o critério de aceite.
- **Verifique o teste contra a história e cenários, não contra o código.**

### Artefatos por ciclo

| Artefato | Descrição |
|---|---|
| Issue com critério de aceite | A especificação da tarefa |
| PR com descrição do que muda e por quê | Rastreabilidade |
| Testes novos cobrindo o critério de aceite | Automáticos |
| ADR, quando a mudança for arquitetural | Só decisões, não toda feature |
| Atualização de `CLAUDE.md` ou `design-system.md` | Sempre que um padrão novo se estabelecer |
| Atualização de `ARCHITECTURE.md` | Só quando surge módulo novo, fronteira nova ou invariante novo — não a cada feature |

---

## E5. Segurança e observabilidade

Não é fase final — corre em paralelo desde E3. Listada separadamente porque é o que agentes mais omitem, já que raramente está no critério de aceite.

### Artefatos

| Artefato | Conteúdo |
|---|---|
| `docs/seguranca/threat-model.md` | O que protege, de quem, como |
| Checklist OWASP Top 10 aplicado | Verificado manualmente, feature a feature |
| Gestão de segredos | Cofre ou variáveis de ambiente; nunca no repositório |
| Scan de dependências no CI | Alerta de vulnerabilidade em pacote |
| Logs estruturados + correlação de requisição | Sem isso não há diagnóstico em produção |
| Rastreamento de erro | Você descobre o bug antes do usuário |
| Rotina de backup **testada** | Backup não restaurado não é backup |

> **Ponto cego mais comum:** autorização. O agente implementa a rota, o teste passa, e não há verificação de que o usuário A não acessa o dado do usuário B. Trate "teste de autorização" como item obrigatório da definition of done.

---

## E6. Release e medição

### Artefatos

| Artefato | Descrição |
|---|---|
| `CHANGELOG.md` | Gerado a partir dos commits |
| Runbook de operação | Como fazer rollback, restaurar backup, quem acionar |

As métricas de produto ficam no outro eixo (P3).

---

# Parte III — Sincronização entre os eixos

## Linha do tempo

```
PRODUTO/DESIGN   P0 ──── P1 ──── P2 ─────────────── P3 ────────────►
                  │       │       │                  ▲
                  │       │       │                  │
              [glossário][fluxos][tokens]        [feedback]
                  │       │       │                  │
                  ▼       ▼       ▼                  │
ENGENHARIA       E0 ── E1 ── E2 ── E3 ────── E4 ─────┘
                                              │
                                        E5 ───┴─── E6
```

Regra prática: **P0 → P1 → P2 precedem E3.** Você pode fazer E0, E1 e E2 em paralelo à prototipação — decidir stack e montar CI não depende do design final. Mas não construa o esqueleto sem tokens definidos, ou a primeira tela já nasce fora do padrão.

## Pontos de contato

| Artefato produzido em | Consumido em | Consequência de pular |
|---|---|---|
| `glossario.md` (P0) | E2 — schema do banco | Três tabelas para a mesma entidade |
| `user-stories.md` (P0) | E4 — issues e testes | Testes que verificam o código, não o requisito |
| Cenários `.feature` (E3) | E4 — fases seguintes | Funcioanlidade deixa de ser verificada de forma automatizada |
| `fluxos.md` (P1) | E2 — desenho das rotas | API que não atende o fluxo real |
| `design-system.md` (P2) | E3 e E4 — toda UI | Cada sessão do agente inventa espaçamento e cor |
| Métricas (E6) | P3 — priorização | Backlog dirigido por opinião |
| Feedback (P3) | E4 — próximas issues | Produto que não evolui |

## Os documentos que o agente lê em toda sessão

São três, e há uma distinção importante entre eles:

**Prescritivos — dizem o que fazer**

- **`CLAUDE.md`** — como escrever código neste projeto
- **`design-system.md`** — como desenhar interface neste projeto

**Descritivo — diz o que existe**

- **`ARCHITECTURE.md`** — onde as coisas ficam e quais fronteiras não se atravessa

Os dois primeiros são imperativos; o terceiro é indicativo. Por isso o `CLAUDE.md` deve **referenciar** o `ARCHITECTURE.md`, nunca duplicá-lo — duas fontes para a mesma informação divergem em uma semana.

Os três precisam de **implementação de referência** no repositório. Regra escrita sem exemplo canônico não é seguida pelo agente: ele copia o que encontra no código, não o que está no documento.

---

# Parte IV — Artefatos mínimos

## Eixo Produto/Design

- [ ] PRD com escopo e fora-de-escopo
- [ ] Glossário do domínio
- [ ] User stories com critério de aceite testável
- [ ] Fluxos dos 3–5 caminhos principais, com estados de erro
- [ ] Wireframes ou protótipo navegável dos fluxos críticos
- [ ] `design-system.md` com tokens e regras
- [ ] Componentes canônicos implementados
- [ ] Padrões de estado (loading, vazio, erro, sucesso)
- [ ] Requisitos de acessibilidade definidos

## Eixo Engenharia

**Decisões**
- [ ] ADRs das decisões estruturantes
- [ ] Diagrama C4 nível 1 e 2

**Repositório**
- [ ] `README.md` (rodar em 3 comandos)
- [ ] `CLAUDE.md`
- [ ] `ARCHITECTURE.md` com code map e invariantes declarados
- [ ] Cenários em Gherkin `.feature`
- [ ] Linter + formatter + tipagem estrita
- [ ] `docker-compose.yml` e `.env.example`
- [ ] Scripts padronizados
- [ ] CI bloqueante

**Contratos**
- [ ] Schema + migrations versionadas
- [ ] Especificação de API
- [ ] Seed de desenvolvimento

**Qualidade**
- [ ] Testes unitários das regras de negócio
- [ ] Testes de integração das rotas
- [ ] 1 a 3 testes e2e dos fluxos críticos
- [ ] Definition of done escrita

**Operação**
- [ ] Logs estruturados + rastreamento de erro
- [ ] Backup testado
- [ ] Runbook
- [ ] Releases
