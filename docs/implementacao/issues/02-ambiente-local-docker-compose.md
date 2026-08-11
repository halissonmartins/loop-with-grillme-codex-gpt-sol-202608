# 02 — Ambiente local em Docker Compose com as dependências reais

**O que construir:** o ambiente inteiro subindo por um comando, com as dependências **reais** e não
dublês (RA-51). Ao fim deste ticket dá para levantar banco, identidade, repositório de artefatos,
orquestrador, ingress, SMTP e a pilha de telemetria, e apontar um navegador para cada um. É o mesmo
conjunto que as costuras de teste levantam em contêiner — metade das regras deste sistema *é*
comportamento de infraestrutura, e um dublê afirmaria a nossa suposição sobre ela em vez de
verificá-la.

**Bloqueado por:** 01.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] PostgreSQL, Keycloak, MinIO, Airflow, Traefik, Mailpit, OTel Collector, Graylog, Prometheus,
      Grafana e Jaeger sobem juntos e ficam saudáveis (RA-51).
- [ ] **Toda** a pilha executa em `America/Sao_Paulo` (RA-52, RNF-14) — é o fuso em que a data de
      referência é resolvida, e um contêiner em UTC carimbaria a data errada.
- [ ] As imagens têm **tag fixada**, nunca `latest`: o comportamento do expurgo depende da versão
      (RA-21) e um contêiner que muda sozinho quebra o ticket 20 sem aviso.
- [ ] Toda variável de ambiente aparece no `.env.example`, com valor de exemplo e sem segredo real.
- [ ] O Testcontainers dos módulos Java levanta o mesmo conjunto, com as mesmas versões — o
      ambiente de teste espelha o Compose, não diverge dele.
- [ ] Um comando derruba tudo e limpa o estado, para que a suíte seja reproduzível.
