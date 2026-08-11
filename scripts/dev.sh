#!/usr/bin/env bash
set -euo pipefail

if [[ -f .env ]]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
fi

api_pid=""
web_pid=""
export NG_FORCE_AUTOCOMPLETE=false

cleanup() {
  if [[ -n "$api_pid" ]]; then
    kill "$api_pid" 2>/dev/null || true
  fi
  if [[ -n "$web_pid" ]]; then
    kill "$web_pid" 2>/dev/null || true
  fi
}

trap cleanup EXIT INT TERM

./mvnw --projects backend/api spring-boot:run -Dspring-boot.run.arguments="--server.port=${API_PORT:-8080}" &
api_pid=$!

npm run start --workspace @relatorios/frontend -- --port "${WEB_PORT:-4200}" &
web_pid=$!

wait -n "$api_pid" "$web_pid"
