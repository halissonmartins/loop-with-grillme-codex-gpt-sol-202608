.PHONY: setup dev test lint build migrate verify

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
