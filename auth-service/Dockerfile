# Multi-stage build
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Copy buildSrc
COPY buildSrc ./buildSrc

# Copy common module
COPY common ./common

# Download dependencies
RUN gradle dependencies --no-daemon

# Copy source code
COPY auth-service ./auth-service

# Build the application
RUN gradle :auth-service:bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser
WORKDIR /app

# Copy the jar file from build stage
COPY --from=build /app/auth-service/build/libs/*.jar app.jar

# Create necessary directories
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]