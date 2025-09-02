.PHONY: help build test run clean stop logs db-migrate format

help:
	@echo "Available commands:"
	@echo "  make build    - Build all services"
	@echo "  make test     - Run all tests"
	@echo "  make run      - Run with docker-compose"
	@echo "  make stop     - Stop all services"
	@echo "  make clean    - Clean build artifacts"
	@echo "  make logs     - Show docker logs"
	@echo "  make db-migrate - Run database migrations"

build:
	./gradlew clean build

test:
	./gradlew test

run:
	docker-compose -f docker/docker-compose.yml up -d

stop:
	docker-compose -f docker/docker-compose.yml down

clean:
	./gradlew clean
	docker-compose -f docker/docker-compose.yml down -v

logs:
	docker-compose -f docker/docker-compose.yml logs -f

db-migrate:
	./gradlew flywayMigrate

format:
	./gradlew spotlessApply