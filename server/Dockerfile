# Stage 1: Build
FROM eclipse-temurin:24-jdk AS build

WORKDIR /app

# Copy Maven wrapper and pom.xml first (for dependency caching)
COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY pom.xml .

# Download dependencies (cached if pom.xml doesn't change)
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copy source code
COPY src/ src/

# Build the JAR
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]