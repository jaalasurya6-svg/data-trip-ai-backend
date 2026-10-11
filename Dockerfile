# Multi-stage build for optimal image size and Render 512MB RAM compatibility
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Package production application without running unit tests during Docker build
RUN mvn clean package -DskipTests

# Production runtime image (lightweight Alpine JRE)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy exact repackaged fat jar from build stage
COPY --from=build /app/target/app.jar app.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod

# Container-aware JVM memory configuration for Render 512MB Free Tier
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Xmx350m", "-jar", "app.jar"]
