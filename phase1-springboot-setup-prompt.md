# Claude Code Prompt: Phase 1 Spring Boot Project Setup with Gradle

## Project Context
I need you to create a production-ready Spring Boot project for a credit card auto-payment system. This is Phase 1 of the development, focusing on the foundation including infrastructure setup, core services development, database design, and authentication system. Use Gradle as the build tool.

## Project Requirements

### 1. Project Structure
Create a multi-module Gradle project with the following structure:
```
cc-autopay-system/
├── settings.gradle.kts
├── build.gradle.kts (root build file)
├── gradle.properties
├── gradlew
├── gradlew.bat
├── gradle/
│   └── wrapper/
├── buildSrc/
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       ├── dependencies.gradle.kts
│       └── common-conventions.gradle.kts
├── common/
│   ├── build.gradle.kts
│   └── src/main/java/com/ccpay/common/
│       ├── dto/
│       ├── exceptions/
│       ├── utils/
│       ├── constants/
│       └── config/
├── auth-service/
│   ├── build.gradle.kts
│   └── src/main/java/com/ccpay/auth/
├── user-service/
│   ├── build.gradle.kts
│   └── src/main/java/com/ccpay/user/
├── wallet-service/
│   ├── build.gradle.kts
│   └── src/main/java/com/ccpay/wallet/
├── api-gateway/
│   ├── build.gradle.kts
│   └── src/main/java/com/ccpay/gateway/
├── docker/
│   └── docker-compose.yml
└── k8s/
    ├── configmaps/
    ├── deployments/
    └── services/
```

### 2. Technology Stack
Please use the following versions and dependencies:
- Java 17 LTS
- Spring Boot 3.2.x
- Spring Cloud 2023.0.x
- Gradle 8.5+
- PostgreSQL 15
- Redis for caching and sessions
- Apache Kafka for event streaming
- Docker for containerization

### 3. Root Build Configuration (build.gradle.kts)
Create a root build.gradle.kts with:
```kotlin
plugins {
    java
    id("org.springframework.boot") version "3.2.1" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    id("com.google.cloud.tools.jib") version "3.4.0" apply false
    id("org.sonarqube") version "4.4.1.3373"
    id("com.gorylenko.gradle-git-properties") version "2.4.1" apply false
}

allprojects {
    group = "com.ccpay"
    version = "1.0.0-SNAPSHOT"
    
    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")
    
    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
```

### 4. BuildSrc Configuration
Create buildSrc for centralized dependency management:

**buildSrc/src/main/kotlin/dependencies.gradle.kts**:
```kotlin
object Versions {
    const val springBoot = "3.2.1"
    const val springCloud = "2023.0.0"
    const val lombok = "1.18.30"
    const val mapstruct = "1.5.5.Final"
    const val jjwt = "0.12.3"
    const val testcontainers = "1.19.3"
    const val postgresql = "42.7.1"
    const val flyway = "10.4.1"
    const val redis = "3.1.6"
    const val kafka = "3.6.1"
    const val micrometerTracing = "1.2.1"
    const val resilience4j = "2.1.0"
}

object Deps {
    // Spring Boot Starters
    const val springBootWeb = "org.springframework.boot:spring-boot-starter-web"
    const val springBootSecurity = "org.springframework.boot:spring-boot-starter-security"
    const val springBootData = "org.springframework.boot:spring-boot-starter-data-jpa"
    const val springBootRedis = "org.springframework.boot:spring-boot-starter-data-redis"
    const val springBootValidation = "org.springframework.boot:spring-boot-starter-validation"
    const val springBootActuator = "org.springframework.boot:spring-boot-starter-actuator"
    
    // Add all other dependencies...
}
```

**buildSrc/src/main/kotlin/common-conventions.gradle.kts**:
```kotlin
plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.cloud.tools.jib")
    jacoco
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
```

### 5. Common Module (build.gradle.kts)
```kotlin
plugins {
    id("common-conventions")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.mapstruct:mapstruct:${Versions.mapstruct}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${Versions.mapstruct}")
    
    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    
    // Apache Commons
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("commons-codec:commons-codec:1.16.0")
}
```

Create the common module with:
- **DTOs**: BaseResponse, ApiResponse, PagedResponse, ErrorResponse
- **Exceptions**: Custom exception hierarchy
- **Global Exception Handler**: @RestControllerAdvice
- **Utils**: SecurityUtils, DateUtils, ValidationUtils, CryptoUtils
- **Constants**: ErrorCodes, ApplicationConstants, SecurityConstants
- **Audit**: BaseEntity with JPA Auditing
- **Configurations**: JacksonConfig, AsyncConfig, CacheConfig

### 6. Authentication Service Module (build.gradle.kts)
```kotlin
plugins {
    id("common-conventions")
}

dependencies {
    implementation(project(":common"))
    
    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    
    // JWT
    implementation("io.jsonwebtoken:jjwt-api:${Versions.jjwt}")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${Versions.jjwt}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${Versions.jjwt}")
    
    // Redis for session management
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.session:spring-session-data-redis")
    
    // Database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core:${Versions.flyway}")
    runtimeOnly("org.postgresql:postgresql:${Versions.postgresql}")
    
    // TOTP for MFA
    implementation("com.warrenstrange:googleauth:1.5.0")
    
    // Email
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    
    // Rate Limiting
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:7.6.0")
    
    // Testing
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:testcontainers")
}
```

Implement complete JWT authentication with:

**Entities**:
```java
@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    private String firstName;
    private String lastName;
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus = KycStatus.PENDING;
    
    @Column(nullable = false)
    private boolean mfaEnabled = false;
    
    private String mfaSecret;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;
    
    private LocalDateTime lastLoginAt;
    private Integer failedLoginAttempts = 0;
    private LocalDateTime lockedUntil;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<RefreshToken> refreshTokens = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
```

**Security Configuration**:
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
            )
            .build();
    }
}
```

**REST Endpoints**:
- Complete implementation of all auth endpoints
- Rate limiting per IP/user
- Email verification flow
- Password reset with secure tokens
- MFA setup and verification

### 7. User Service Module (build.gradle.kts)
```kotlin
plugins {
    id("common-conventions")
}

dependencies {
    implementation(project(":common"))
    
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // Database
    implementation("org.flywaydb:flyway-core:${Versions.flyway}")
    runtimeOnly("org.postgresql:postgresql:${Versions.postgresql}")
    
    // AWS SDK for S3 (document storage)
    implementation(platform("software.amazon.awssdk:bom:2.22.0"))
    implementation("software.amazon.awssdk:s3")
    
    // Kafka for events
    implementation("org.springframework.kafka:spring-kafka")
    
    // MapStruct for DTO mapping
    implementation("org.mapstruct:mapstruct:${Versions.mapstruct}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${Versions.mapstruct}")
}
```

Implement user management with:
- Profile CRUD operations
- KYC document upload workflow
- User preferences
- Audit logging for all actions

### 8. Wallet Service Module (build.gradle.kts)
```kotlin
plugins {
    id("common-conventions")
}

dependencies {
    implementation(project(":common"))
    
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // Database with optimistic locking
    implementation("org.flywaydb:flyway-core:${Versions.flyway}")
    runtimeOnly("org.postgresql:postgresql:${Versions.postgresql}")
    
    // Redis for idempotency
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    
    // Kafka for event publishing
    implementation("org.springframework.kafka:spring-kafka")
    
    // Distributed locking
    implementation("net.javacrumbs.shedlock:shedlock-spring:5.10.0")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:5.10.0")
}
```

Create wallet management foundation:
- Wallet creation with ACID compliance
- Balance management with optimistic locking
- Transaction history
- Event publishing for all operations

### 9. API Gateway Module (build.gradle.kts)
```kotlin
plugins {
    id("common-conventions")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${Versions.springCloud}")
    }
}

dependencies {
    implementation(project(":common"))
    
    // Spring Cloud Gateway
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
    
    // Redis for rate limiting
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    
    // JWT validation
    implementation("io.jsonwebtoken:jjwt-api:${Versions.jjwt}")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${Versions.jjwt}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${Versions.jjwt}")
    
    // Monitoring
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
}
```

Configure API Gateway with:
- Dynamic routing
- JWT validation filter
- Rate limiting
- Circuit breaker
- Request/Response logging

### 10. Database Migrations Structure
Create Flyway migrations for each service:

**auth-service/src/main/resources/db/migration/**:
```sql
-- V1__create_users_table.sql
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone_number VARCHAR(20),
    kyc_status VARCHAR(50) DEFAULT 'PENDING',
    mfa_enabled BOOLEAN DEFAULT false,
    mfa_secret VARCHAR(255),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    last_login_at TIMESTAMP,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);

-- V2__create_roles_table.sql
-- V3__create_refresh_tokens_table.sql
-- etc.
```

### 11. Application Configuration Files

**application.yml (for each service)**:
```yaml
spring:
  application:
    name: ${SERVICE_NAME}
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
    
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
        
  flyway:
    enabled: true
    baseline-on-migrate: true
    
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: ${spring.application.name}
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.ccpay.*"

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
  tracing:
    sampling:
      probability: 1.0

logging:
  level:
    com.ccpay: DEBUG
    org.springframework.security: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 12. Docker Configuration

**Dockerfile (for each service)**:
```dockerfile
# Multi-stage build
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY --chown=gradle:gradle . .
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

**docker-compose.yml**:
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: ccpay_db
      POSTGRES_USER: ccpay_user
      POSTGRES_PASSWORD: ccpay_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init-scripts:/docker-entrypoint-initdb.d
    networks:
      - ccpay-network

  redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - ccpay-network

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    networks:
      - ccpay-network

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    networks:
      - ccpay-network

  api-gateway:
    build:
      context: ./api-gateway
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
    depends_on:
      - redis
    networks:
      - ccpay-network

  auth-service:
    build:
      context: ./auth-service
      dockerfile: Dockerfile
    ports:
      - "8081:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      DB_HOST: postgres
      REDIS_HOST: redis
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    depends_on:
      - postgres
      - redis
      - kafka
    networks:
      - ccpay-network

  user-service:
    build:
      context: ./user-service
      dockerfile: Dockerfile
    ports:
      - "8082:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      DB_HOST: postgres
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    depends_on:
      - postgres
      - kafka
    networks:
      - ccpay-network

  wallet-service:
    build:
      context: ./wallet-service
      dockerfile: Dockerfile
    ports:
      - "8083:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      DB_HOST: postgres
      REDIS_HOST: redis
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    depends_on:
      - postgres
      - redis
      - kafka
    networks:
      - ccpay-network

networks:
  ccpay-network:
    driver: bridge

volumes:
  postgres_data:
  redis_data:
```

### 13. Testing Configuration

**Test Configuration (src/test/resources/application-test.yml)**:
```yaml
spring:
  datasource:
    url: jdbc:tc:postgresql:15:///testdb
    driver-class-name: org.testcontainers.jdbc.ContainerDatabaseDriver
  
  jpa:
    hibernate:
      ddl-auto: create-drop
      
  test:
    database:
      replace: none
```

**Integration Test Base Class**:
```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public abstract class IntegrationTestBase {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }
}
```

### 14. Gradle Tasks and Scripts

**gradle.properties**:
```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
```

**Custom Gradle Tasks (in root build.gradle.kts)**:
```kotlin
tasks.register("runAllTests") {
    dependsOn(subprojects.map { it.tasks.named("test") })
}

tasks.register("buildAllImages") {
    dependsOn(subprojects.map { it.tasks.named("jibDockerBuild") })
}

tasks.register("generateReport") {
    doLast {
        println("Generating project report...")
        // Add report generation logic
    }
}

tasks.wrapper {
    gradleVersion = "8.5"
    distributionType = Wrapper.DistributionType.ALL
}
```

### 15. Development Tools and Scripts

**Makefile**:
```makefile
.PHONY: help build test run clean

help:
	@echo "Available commands:"
	@echo "  make build    - Build all services"
	@echo "  make test     - Run all tests"
	@echo "  make run      - Run with docker-compose"
	@echo "  make clean    - Clean build artifacts"

build:
	./gradlew clean build

test:
	./gradlew test

run:
	docker-compose up -d

stop:
	docker-compose down

clean:
	./gradlew clean
	docker-compose down -v

logs:
	docker-compose logs -f

db-migrate:
	./gradlew flywayMigrate

format:
	./gradlew spotlessApply
```

### 16. API Documentation Setup

Add OpenAPI documentation:
```kotlin
// In each service's build.gradle.kts
dependencies {
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
}
```

**OpenAPI Configuration**:
```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("CC AutoPay System API")
                .version("1.0.0")
                .description("Credit Card Auto-Payment System API Documentation")
                .contact(new Contact()
                    .name("API Support")
                    .email("api@ccpay.com")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }
}
```

### 17. GitHub Actions CI/CD

**.github/workflows/ci.yml**:
```yaml
name: CI Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
          
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Setup Gradle
      uses: gradle/gradle-build-action@v2
      
    - name: Run tests
      run: ./gradlew test
      
    - name: Generate test report
      uses: dorny/test-reporter@v1
      if: success() || failure()
      with:
        name: Test Results
        path: '**/build/test-results/test/TEST-*.xml'
        reporter: java-junit
        
    - name: Run SonarQube analysis
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      run: ./gradlew sonarqube
      
    - name: Build Docker images
      run: ./gradlew jibDockerBuild
      
    - name: Upload artifacts
      uses: actions/upload-artifact@v3
      with:
        name: jar-artifacts
        path: '**/build/libs/*.jar'
```

### 18. README.md Template

```markdown
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
docker-compose up -d
```

5. Access services:
- API Gateway: http://localhost:8080
- Auth Service: http://localhost:8081
- User Service: http://localhost:8082
- Wallet Service: http://localhost:8083
- Swagger UI: http://localhost:8080/swagger-ui.html

## Development

### Running locally
```bash
# Start infrastructure
docker-compose up postgres redis kafka -d

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
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI Spec: http://localhost:8080/v3/api-docs

## Monitoring
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Prometheus: http://localhost:8080/actuator/prometheus

## Security
- JWT-based authentication
- Rate limiting enabled
- CORS configured
- Input validation on all endpoints

## Contributing
Please read CONTRIBUTING.md for details on our code of conduct and process.

## License
Proprietary - All rights reserved
```

## Expected Deliverables

1. Complete Gradle multi-module project structure
2. Fully functional authentication system with JWT and MFA
3. User management with KYC workflow foundation
4. Basic wallet service with transaction management
5. API Gateway with routing and security
6. PostgreSQL schemas with Flyway migrations
7. Docker Compose setup for local development
8. Comprehensive test coverage (>80% for critical paths)
9. API documentation with Swagger/OpenAPI
10. CI/CD pipeline with GitHub Actions

## Implementation Notes

- Use Gradle Kotlin DSL for all build files
- Implement proper error handling and validation
- Follow Spring Boot best practices
- Ensure all financial operations are ACID compliant
- Add comprehensive logging with correlation IDs
- Implement graceful shutdown for all services
- Use dependency injection consistently
- Follow RESTful API design principles
- Implement idempotency for all state-changing operations
- Add proper database indexes for performance

Start by creating the complete project structure, then implement each module incrementally, ensuring each service can run independently but also work together when deployed.