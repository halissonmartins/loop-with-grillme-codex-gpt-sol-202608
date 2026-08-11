#!/usr/bin/env bash
set -euo pipefail

compose_file=docker-compose.yml
environment_file=.env.example

docker compose --env-file "$environment_file" -f "$compose_file" config --quiet

configuration=$(docker compose --env-file "$environment_file" -f "$compose_file" config)

required_services=(
  postgres keycloak minio airflow traefik mailpit otel-collector mongodb opensearch graylog prometheus grafana jaeger
)

for service in "${required_services[@]}"; do
  if ! grep -q "^  $service:$" <<<"$configuration"; then
    echo "Serviço obrigatório ausente: $service" >&2
    exit 1
  fi
done

if grep -Eq 'image: .+:latest($|[[:space:]])' <<<"$configuration"; then
  echo 'As imagens do Compose devem ter tags fixadas.' >&2
  exit 1
fi

if [[ $(grep -Fc 'TZ: America/Sao_Paulo' <<<"$configuration") -lt ${#required_services[@]} ]]; then
  echo 'Todos os contêineres devem usar America/Sao_Paulo.' >&2
  exit 1
fi

while IFS= read -r variable; do
  if ! grep -q "^$variable=" "$environment_file"; then
    echo "Variável ausente em $environment_file: $variable" >&2
    exit 1
  fi
done < <(grep -oE '\$\{[A-Z0-9_]+\}' "$compose_file" | tr -d "\${}" | sort -u)
