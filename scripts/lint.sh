#!/usr/bin/env bash
set -euo pipefail

./mvnw --batch-mode --no-transfer-progress -DskipTests validate
npm run lint
npm audit --audit-level=moderate
shellcheck scripts/*.sh .githooks/pre-commit

maven_version=$(./mvnw --quiet help:evaluate -Dexpression=project.version -DforceStdout)
root_npm_version=$(node --print "require('./package.json').version")
frontend_version=$(node --print "require('./frontend/package.json').version")

if [[ "$maven_version" != "$root_npm_version" ]]; then
  echo "Versões divergentes: Maven=$maven_version npm=$root_npm_version" >&2
  exit 1
fi

if [[ "$maven_version" != "$frontend_version" ]]; then
  echo "Versões divergentes: Maven=$maven_version npm=$root_npm_version frontend=$frontend_version" >&2
  exit 1
fi
