# Instruções do projeto

## Stack

- Java 25, Maven Wrapper e Spring Boot 4.1 para o backend.
- Spring Web para a API e Spring Batch para os processadores.
- Node.js 24, npm 11, TypeScript 6 e Angular 22 para o frontend.
- JUnit 5, Vitest, JaCoCo, Checkstyle, Spotless, ESLint, Prettier e ShellCheck para qualidade.

## Comandos

- Instalar: `make setup`.
- Rodar: `make dev`.
- Testar: `make test`.
- Lint e formato: `make lint`.
- Construir: `make build`.
- Aplicar migrations: `make migrate`.

## Onde as coisas ficam

- Glossário, fonte única dos termos do domínio: `docs/glossario.md`.
- PRD: `docs/prd.md`.
- Arquitetura inicial: `docs/arquitetura-inicial.md`.
- Decisões: `docs/adr/`.
- Guia de implementação: `docs/guias/guia-app-web.md`.
- Tracker: `docs/implementacao/issues/`.
- Leia `ARCHITECTURE.md` antes de criar arquivo novo; não duplique aqui seu conteúdo descritivo.

Um termo do domínio é definido apenas no glossário. Os demais documentos usam os termos e não os
redefinem.

## Convenções

- Implemente uma ISSUE por vez e marque seus critérios somente depois da validação e revisão.
- Use TDD em seams públicos acordados: teste falhando, implementação mínima e novo ciclo.
- Java usa pacotes sob `br.com.exemplo.relatorios`; testes ficam no módulo observado.
- Angular usa componentes standalone, tipagem estrita e interface em pt-BR.
- Commits são pequenos, atômicos e focados em uma tarefa lógica.

## Design

- Toda UI segue os artefatos de `docs/design/` quando introduzidos pela ISSUE 05.
- Não introduza cores, espaçamentos ou componentes visuais fora dos tokens do design system.

## Regras invioláveis

- Nunca permita que algo além da Coleta leia schemas transacionais de produto.
- Nunca permita que a API acesse schemas transacionais de produto.
- Nunca altere uma migration já aplicada; crie outra migration.
- Nunca crie rota sem teste de autorização.
- Nunca desabilite lint, formatter, tipagem ou teste para fazer o build passar.
- Nunca coloque segredo real no repositório.
- Nunca redefina fora do glossário um termo do domínio.

## Fora de escopo

- Não antecipe ISSUEs, infraestrutura ou abstrações sem requisito atual.
- Não mude decisões arquiteturais sem registrar ou atualizar o ADR correspondente.
- Não faça deploy, alteração de ambiente compartilhado ou operação destrutiva sem autorização.
