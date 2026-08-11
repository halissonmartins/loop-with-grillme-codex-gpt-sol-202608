#!/usr/bin/env bash
set -euo pipefail

./mvnw --batch-mode --no-transfer-progress verify
npm run typecheck
npm run build
