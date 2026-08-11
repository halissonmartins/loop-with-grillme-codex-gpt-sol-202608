# Relatórios agendados

Mono repositório do sistema de coleta, armazenamento e exportação de relatórios.

## Validar um clone limpo em três comandos

Pré-requisitos: Java 25, Node.js 24, npm 11 e Bash.

```bash
cp .env.example .env
make setup
make verify
```

Esses comandos instalam dependências, compilam todos os módulos e executam o mesmo lint, verificação
de tipos, testes e build usados no CI.

Para iniciar a API em `http://localhost:8080` e o frontend em `http://localhost:4200`:

```bash
make dev
```

## Ambiente local de dependências

Após criar o `.env` pelo `make setup`, suba PostgreSQL, Keycloak, MinIO, Airflow, Traefik, Mailpit,
OpenTelemetry Collector, Graylog, Prometheus, Grafana e Jaeger com:

```bash
make infra-up
```

Os consoles locais ficam nas portas definidas em `.env.example`: Keycloak (`8081`), MinIO (`9001`),
Airflow (`8082`), Traefik (`8083`), Mailpit (`8025`), Graylog (`9002`), Prometheus (`9090`), Grafana
(`3000`) e Jaeger (`16686`). Todos os contêineres usam `America/Sao_Paulo` e imagens de versão
fixada. Para executar o teste Testcontainers que sobe esse mesmo Compose, rode:

```bash
make infra-test
```

Remova contêineres, redes e volumes para retornar ao estado limpo:

```bash
make infra-down
```

## Qualidade e build

```bash
make lint
make test
make build
```

Os comandos usam o Maven Wrapper e o lockfile do npm, de modo que um clone limpo não dependa do
estado global da máquina. `make migrate` é um comando estável desde a fundação; ele passa a aplicar
Flyway quando o schema de controle for introduzido.

## Estrutura

- `backend/common`: biblioteca comum a todo backend.
- `backend/processor-starter`: convenções compartilhadas dos processadores Spring Batch.
- `backend/processors`: um processador por Produto.
- `backend/api`: API REST para usuários e integrações.
- `frontend`: aplicação Angular.
- `docs`: requisitos, arquitetura, ADRs e tracker de implementação.

Leia `CLAUDE.md` para regras prescritivas e `ARCHITECTURE.md` para o mapa descritivo do código.
