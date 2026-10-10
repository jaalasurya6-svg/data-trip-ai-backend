package com.daytripai.controller;

import com.daytripai.ai.service.AiItineraryOrchestrator;
import com.daytripai.dto.request.AiItineraryGenerateRequest;
import com.daytripai.dto.response.AiItineraryResponse;
import com.daytripai.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "AI Itinerary Engine", description = "Endpoints for AI-powered personalized itinerary generation with fallback")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AiItineraryController {

    private final AiItineraryOrchestrator aiOrchestrator;

    public AiItineraryController(AiItineraryOrchestrator aiOrchestrator) {
        this.aiOrchestrator = aiOrchestrator;
    }

    @PostMapping("/trips/{tripId}/itinerary/ai-generate")
    @Operation(summary = "Generate a personalized itinerary using external AI (Gemini/OpenAI) with seamless local fallback")
    public ResponseEntity<ApiResponse<AiItineraryResponse>> generateAiItinerary(
            @PathVariable Long tripId,
            @RequestBody(required = false) AiItineraryGenerateRequest request,
            @RequestAttribute(value = "userId", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        AiItineraryResponse response = aiOrchestrator.generateItinerary(tripId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("AI Itinerary generated successfully", response));
    }

    @GetMapping("/ai/status")
    @Operation(summary = "Check configured AI providers, availability, and active models")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAiStatus() {
        Map<String, Object> status = aiOrchestrator.getAiStatus();
        return ResponseEntity.ok(ApiResponse.success("AI service status retrieved", status));
    }
}
