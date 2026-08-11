#!/usr/bin/env bash
set -euo pipefail

if [[ ! -f .env ]]; then
  cp .env.example .env
fi

RUN_INFRASTRUCTURE_TESTS=true ./mvnw --batch-mode --no-transfer-progress -pl backend/common test
