package com.daytripai.controller;

import com.daytripai.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Health Check & System Status REST Controller.
 * Verifies backend connectivity, environment status, and runtime metrics.
 */
@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health Check", description = "System readiness and heartbeat endpoints")
public class HealthCheckController {

    @Value("${spring.application.name:day-trip-ai-backend}")
    private String applicationName;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @GetMapping
    @Operation(summary = "Check Backend Health", description = "Returns system status, Java version, and operational heartbeat")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHealthStatus() {
        Map<String, Object> healthData = new LinkedHashMap<>();
        healthData.put("status", "UP");
        healthData.put("application", applicationName);
        healthData.put("environment", activeProfile);
        healthData.put("javaVersion", System.getProperty("java.version"));
        healthData.put("os", System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")");
        healthData.put("serverTime", LocalDateTime.now().toString());
        healthData.put("message", "Day Trip AI Backend is operating normally.");

        return ResponseEntity.ok(ApiResponse.success("Day Trip AI Backend service is healthy", healthData));
    }
}
