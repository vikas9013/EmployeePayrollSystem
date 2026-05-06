# Multi-stage build for optimal image size
# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
LABEL description="Employee Payroll System - Spring Boot REST API"
LABEL version="1.0"

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /build/target/EmployeePayrollSystem-1.0-SNAPSHOT.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Run the Java application
ENTRYPOINT ["java", "-jar", "app.jar"]