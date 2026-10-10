package com.daytripai;

import com.daytripai.dto.request.AiItineraryGenerateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "traveler@daytripai.com", authorities = {"ROLE_USER"})
class AiItineraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/ai/status returns provider registry and model metadata")
    void testGetAiStatus() throws Exception {
        mockMvc.perform(get("/api/v1/ai/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.providers.localFallback.configured", is(true)))
                .andExpect(jsonPath("$.data.providers.gemini", notNullValue()))
                .andExpect(jsonPath("$.data.providers.openai", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/trips/1/itinerary/ai-generate produces full itinerary with metadata and fallback")
    void testAiGenerateItineraryWithFallback() throws Exception {
        AiItineraryGenerateRequest request = new AiItineraryGenerateRequest(
                "fallback",
                "Focus on photography angles and historic architecture",
                "CAB"
        );

        mockMvc.perform(post("/api/v1/trips/1/itinerary/ai-generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.itinerary.items", not(empty())))
                .andExpect(jsonPath("$.data.metadata.providerUsed", notNullValue()))
                .andExpect(jsonPath("$.data.metadata.latencyMs", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.aiNarrative", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/trips/1/itinerary/ai-generate with Gemini provider executes gracefully with fallback if key is missing")
    void testAiGenerateWithGeminiGracefulFallback() throws Exception {
        AiItineraryGenerateRequest request = new AiItineraryGenerateRequest(
                "gemini",
                "Avoid steep staircases, family friendly",
                "CAB"
        );

        mockMvc.perform(post("/api/v1/trips/1/itinerary/ai-generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.itinerary.items", not(empty())))
                .andExpect(jsonPath("$.data.metadata", notNullValue()));
    }
}
