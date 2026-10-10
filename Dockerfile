# Multi-stage build for optimal image size and security
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build production jar
COPY src ./src
RUN mvn clean package -DskipTests

# Production runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy jar from build stage
COPY --from=build /app/target/day-trip-ai-backend-*.jar app.jar

EXPOSE 8080
ENV PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]
