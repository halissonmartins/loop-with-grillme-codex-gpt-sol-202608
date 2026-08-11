# C4 — Contexto e contêineres

Este documento mostra a fronteira do sistema e seus contêineres. As decisões e consequências
permanecem em [`../arquitetura-inicial.md`](../arquitetura-inicial.md) e nos
[`../adr/`](../adr/).

## Nível 1 — Contexto

```mermaid
flowchart LR
  usuarios["Usuários<br/>RELATOR, GERENTE e ADMINISTRADOR"]
  operacao["Operação"]
  sistema["Relatórios agendados<br/>Coleta, catálogo e exportação de relatórios"]
  produto["Schemas transacionais dos Produtos<br/>Poupança, Cliente, Conta Corrente, Consórcio e Empréstimo"]
  keycloak["Keycloak<br/>Identidade e autorização"]
  minio["MinIO<br/>Repositório de artefatos"]
  smtp["Mailpit<br/>SMTP local"]
  observabilidade["Pilha de observabilidade<br/>Logs, métricas e traces"]

  usuarios -->|"consulta, exporta e administra"| sistema
  operacao -->|"acompanha o ambiente"| sistema
  sistema -->|"lê somente pela Coleta"| produto
  sistema -->|"autentica e autoriza"| keycloak
  sistema -->|"armazena e lê artefatos"| minio
  sistema -->|"envia e inspeciona e-mails"| smtp
  sistema -->|"emite telemetria"| observabilidade
```

## Nível 2 — Contêineres

```mermaid
flowchart LR
  usuarios["Usuários"] --> traefik["Traefik<br/>Ingress local"]
  traefik --> web["Frontend Angular<br/>Interface web"]
  web -->|"JWT e HTTP"| api["API REST Spring Boot<br/>Catálogo, acesso, histórico e exportação"]
  web -->|"login"| keycloak["Keycloak<br/>Identidade e roles"]

  api -->|"valida JWT e administra identidades"| keycloak
  api -->|"lê metadados"| controle["PostgreSQL<br/>Schema de controle"]
  api -->|"lê artefatos"| minio["MinIO<br/>Artefatos"]
  api -->|"envia e-mails"| mailpit["Mailpit<br/>SMTP local"]

  airflow["Apache Airflow<br/>Reserva e orquestração"] -->|"dispara"| coleta["Processadores Spring Batch<br/>Coleta por Produto"]
  airflow -->|"reserva e encerra"| controle
  coleta -->|"única fronteira de leitura"| transacional["PostgreSQL<br/>Schemas transacionais dos Produtos"]
  coleta -->|"grava metadados"| controle
  coleta -->|"grava artefatos"| minio

  api -. "nunca acessa" .-> transacional

  api --> otel["OTel Collector<br/>Caminho único de telemetria"]
  coleta --> otel
  airflow --> otel
  otel --> graylog["Graylog<br/>Logs"]
  otel --> prometheus["Prometheus e Grafana<br/>Métricas e painéis"]
  otel --> jaeger["Jaeger<br/>Traces"]
```

O diagrama torna explícitas as fronteiras de RA-10 e RA-29: os processadores da Coleta são os
únicos leitores dos schemas transacionais; a API REST só lê o schema de controle e os artefatos já
apurados.
