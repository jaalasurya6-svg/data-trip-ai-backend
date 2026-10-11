# Day Trip AI — Backend REST API Engine

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security 6](https://img.shields.io/badge/Spring%20Security-6.3-blue.svg)](https://spring.io/projects/spring-security)
[![MySQL 8.0](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Tests Passing](https://img.shields.io/badge/Tests-38%2F38%20Passing-success.svg)](https://maven.apache.org/)

Enterprise-grade backend service for **Day Trip AI**, an autonomous one-day travel discovery, itinerary scheduling, and expense management platform.

---

## 🚀 Key Architectural Capabilities

- **Knapsack Time-Packing Solver**: $O(N)$ priority-scored heuristic packing destinations into tight 8-to-12-hour day constraints while factoring in opening hours, transit hops, and a dedicated 45-minute lunch break.
- **Geospatial Transit Engine**: Haversine geodesic routing formula with a $1.30\times$ road curvature detour coefficient, multi-modal speeds (Driving 35 km/h, Transit 24 km/h, Walking 4.5 km/h), and realistic local fare matrices.
- **Meteorological Intelligence**: Live integration with the Open-Meteo REST service with hourly temperatures, rain probabilities, and AI packing advice.
- **Multi-Provider AI Studio**: Dynamic prompt orchestration with Google Gemini 1.5 Flash, OpenAI GPT-4o Mini, and deterministic rule-engine fallback.
- **Stateless JWT Security & Refresh Rotation**: HMAC-SHA256 access tokens and 7-day single-use UUID refresh token rotation with BCrypt password hashing.

---

## 🛠️ Technology Stack

- **Language & Runtime**: Java 21 (LTS)
- **Framework**: Spring Boot 3.3.4
- **ORM / Persistence**: Spring Data JPA / Hibernate 6.5
- **Database**: MySQL 8.0 with HikariCP connection pooling
- **Security**: Spring Security 6, JJWT 0.12.6
- **Documentation**: Springdoc OpenAPI 2.6.0 (Swagger UI)
- **Build Tool**: Apache Maven 3.9+

---

## 📦 Getting Started

### Prerequisites
- JDK 21+ installed
- MySQL 8.0 running locally on port 3306 with database `daytrip_ai`

### Configuration
Update `src/main/resources/application.properties` if needed:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/daytrip_ai?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

### Build & Run
```bash
# Run unit & integration test suite (38 tests)
mvn test

# Package executable JAR
mvn package -DskipTests

# Run application
java -jar target/day-trip-ai-backend-1.0.0.jar
```

Server starts on port **8080**.

---

## ☁️ Deploying to Render

This repository is pre-configured for **zero-config 1-click deployment on Render**:

### Method A: Docker Web Service (Recommended)
1. On **Render Dashboard**, click **New +** $\rightarrow$ **Web Service**.
2. Connect your GitHub repository: `https://github.com/jaalasurya6-svg/data-trip-ai-backend`.
3. Select **Docker** environment. Render will automatically detect the multi-stage `Dockerfile`.
4. Click **Create Web Service**. Render builds the image and boots the service!

### Method B: Native Java Service
1. **Environment**: Java
2. **Build Command**: `mvn clean package -DskipTests`
3. **Start Command**: `java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Xmx350m -jar target/app.jar`

### 💡 Database Flexibility:
- **Zero-Config Default**: Out of the box, the backend starts with an embedded in-memory **H2 database (MySQL compatibility mode)** seeded with 15 tourist spots, users, and trips. No external DB setup required!
- **Persistent Cloud MySQL (Optional)**: If you want persistent cloud storage across container reboots, simply add these Environment Variables in your Render Service Dashboard:
  - `SPRING_DATASOURCE_URL`: `jdbc:mysql://<your-db-host>:<port>/<db_name>?useSSL=true&allowPublicKeyRetrieval=true`
  - `SPRING_DATASOURCE_USERNAME`: `<your_db_username>`
  - `SPRING_DATASOURCE_PASSWORD`: `<your_db_password>`

---

## 📖 API Documentation & Swagger UI
Access the interactive OpenAPI Swagger UI at:  
👉 **`http://localhost:8080/swagger-ui/index.html`**

Health check endpoint:  
👉 **`http://localhost:8080/api/v1/health`**
