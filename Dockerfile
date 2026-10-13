# ===================================================================
# Stage 1: Build the Spring Boot application with Maven
# ===================================================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy dependency descriptor first to leverage Docker layer caching
COPY pom.xml .

# Pre-fetch project dependencies in an isolated layer
RUN mvn dependency:go-offline -B

# Copy application source code
COPY src ./src

# Package executable JAR (skipping tests for container image build)
RUN mvn clean package -DskipTests -B

# ===================================================================
# Stage 2: Minimal, secure JRE runtime container
# ===================================================================
FROM eclipse-temurin:17-jre-alpine AS runner

WORKDIR /app

# Create a non-root system user and group for security compliance
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the built JAR artifact from the builder stage
COPY --from=builder /app/target/backend-*.jar app.jar

# Transfer ownership to non-root user
RUN chown -R spring:spring /app

USER spring

# Expose Spring Boot server port
EXPOSE 8080

# Production JVM flags & environment defaults
ENV PORT=8080 \
    SPRING_PROFILES_ACTIVE=default \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# Container healthcheck against /api/health endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

# Launch Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
