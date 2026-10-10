package com.daytripai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Entry Point for Day Trip AI Backend.
 * Enables Spring Boot auto-configuration, component scanning, and JPA Auditing
 * for tracking creation and modification timestamps automatically.
 */
@SpringBootApplication
@EnableJpaAuditing
public class DayTripAiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DayTripAiBackendApplication.class, args);
        System.out.println("==================================================");
        System.out.println("   DAY TRIP AI BACKEND RUNNING SUCCESSFULLY!     ");
        System.out.println("   Health API:  http://localhost:8080/api/v1/health ");
        System.out.println("   Swagger UI:  http://localhost:8080/swagger-ui/index.html ");
        System.out.println("==================================================");
    }
}
