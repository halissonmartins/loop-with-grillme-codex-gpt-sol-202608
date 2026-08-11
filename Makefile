.PHONY: setup dev test lint build migrate verify infra-up infra-down infra-test

setup:
	./scripts/setup.sh

dev:
	./scripts/dev.sh

test:
	./scripts/test.sh

lint:
	./scripts/lint.sh

build:
	./scripts/build.sh

migrate:
	./scripts/migrate.sh

verify: lint test build

infra-up:
	docker compose up --detach --wait

infra-down:
	docker compose down --volumes --remove-orphans

infra-test:
	./scripts/test-infrastructure.sh
