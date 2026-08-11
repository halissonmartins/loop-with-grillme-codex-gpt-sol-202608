#!/usr/bin/env bash
set -euo pipefail

./mvnw --batch-mode --no-transfer-progress test
npm test
./scripts/test-compose.sh
