#!/usr/bin/env bash
set -euo pipefail

if [[ ! -f .env ]]; then
  cp .env.example .env
fi

./mvnw --batch-mode --no-transfer-progress -DskipTests install
npm ci
git config core.hooksPath .githooks
