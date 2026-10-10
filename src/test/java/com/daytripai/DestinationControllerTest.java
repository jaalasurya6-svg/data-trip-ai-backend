package com.daytripai;

import com.daytripai.dto.request.DestinationRequest;
import com.daytripai.enums.DestinationCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin@daytripai.com", authorities = {"ROLE_ADMIN"})
class DestinationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/destinations returns seeded destinations with pagination")
    void testGetAllDestinations() throws Exception {
        mockMvc.perform(get("/api/v1/destinations")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonExpect(true))
                .andExpect(jsonPath("$.data.content", hasSize(5)))
                .andExpect(jsonPath("$.data.totalElements", greaterThanOrEqualTo(10)));
    }

    @Test
    @DisplayName("GET /api/v1/destinations with city filter returns only that city")
    void testFilterByCity() throws Exception {
        mockMvc.perform(get("/api/v1/destinations")
                        .param("city", "Hyderabad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[*].city", everyItem(equalToIgnoringCase("Hyderabad"))));
    }

    @Test
    @DisplayName("GET /api/v1/destinations/cities returns distinct city list")
    void testGetAvailableCities() throws Exception {
        mockMvc.perform(get("/api/v1/destinations/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasItem("Hyderabad")))
                .andExpect(jsonPath("$.data", hasItem("Mumbai")));
    }

    @Test
    @DisplayName("GET /api/v1/destinations/99999 returns 404 ResourceNotFoundException")
    void testGetNonExistentDestination() throws Exception {
        mockMvc.perform(get("/api/v1/destinations/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Destination not found with id: '99999'")));
    }

    @Test
    @DisplayName("POST /api/v1/destinations with invalid payload triggers GlobalExceptionHandler validation map")
    void testValidationFailure() throws Exception {
        DestinationRequest invalidRequest = new DestinationRequest();
        // Missing name, city, state, category, estimatedCost

        mockMvc.perform(post("/api/v1/destinations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.data.name", notNullValue()))
                .andExpect(jsonPath("$.data.city", notNullValue()))
                .andExpect(jsonPath("$.data.category", notNullValue()));
    }

    private org.springframework.test.web.servlet.ResultMatcher jsonExpect(boolean success) {
        return jsonPath("$.success", is(success));
    }
}
