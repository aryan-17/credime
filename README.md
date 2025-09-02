# CC AutoPay System - Phase 1

## Overview
Credit Card Auto-Payment System built with Spring Boot microservices architecture.

## Tech Stack
- Java 17
- Spring Boot 3.2.x
- Gradle 8.5
- PostgreSQL 15
- Redis
- Apache Kafka
- Docker & Kubernetes

## Project Structure
```
cc-autopay-system/
├── common/          - Shared utilities and DTOs
├── auth-service/    - Authentication & Authorization
├── user-service/    - User Management
├── wallet-service/  - Wallet Management
├── api-gateway/     - API Gateway
└── docker/          - Docker configurations
```

## Prerequisites
- JDK 17
- Docker & Docker Compose
- Gradle 8.5+

## Quick Start

1. Clone the repository
```bash
git clone <repository-url>
cd cc-autopay-system
```

2. Build the project
```bash
./gradlew clean build
```

3. Run tests
```bash
./gradlew test
```

4. Start services with Docker Compose
```bash
docker-compose -f docker/docker-compose.yml up -d
```

5. Access services:
- API Gateway: http://localhost:8080
- Auth Service: http://localhost:8081
- User Service: http://localhost:8082
- Wallet Service: http://localhost:8083
- Swagger UI: http://localhost:8081/swagger-ui.html
- MailHog UI: http://localhost:8025

## Development

### Running locally
```bash
# Start infrastructure
docker-compose -f docker/docker-compose.yml up postgres redis kafka -d

# Run service
./gradlew :auth-service:bootRun
```

### Running tests
```bash
# All tests
./gradlew test

# Service specific
./gradlew :auth-service:test
```

### Database migrations
```bash
./gradlew flywayMigrate
```

## API Documentation
- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI Spec: http://localhost:8081/v3/api-docs

## Monitoring
- Health: http://localhost:8081/actuator/health
- Metrics: http://localhost:8081/actuator/metrics
- Prometheus: http://localhost:8081/actuator/prometheus

## Security
- JWT-based authentication
- Rate limiting enabled
- CORS configured
- Input validation on all endpoints

## License
Proprietary - All rights reserved