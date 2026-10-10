package com.daytripai;

import com.daytripai.dto.request.TripRequest;
import com.daytripai.dto.request.TripStatusUpdateRequest;
import com.daytripai.enums.TripStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "traveler@daytripai.com", authorities = {"ROLE_USER"})
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/trips creates trip and returns 201 Created")
    void testCreateTrip() throws Exception {
        TripRequest request = new TripRequest(
                "Bandra, Mumbai",
                "Mumbai",
                LocalDate.now().plusDays(5),
                3500.0,
                10,
                2,
                "Seaside & Heritage"
        );

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.destinationCity", is("Mumbai")))
                .andExpect(jsonPath("$.data.budget", is(3500.0)))
                .andExpect(jsonPath("$.data.status", is("PLANNED")));
    }

    @Test
    @DisplayName("GET /api/v1/trips returns seeded trip with pagination")
    void testGetTrips() throws Exception {
        mockMvc.perform(get("/api/v1/trips")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", not(empty())))
                .andExpect(jsonPath("$.data.totalElements", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("GET /api/v1/trips/{id}/summary returns budget analytics")
    void testGetTripSummary() throws Exception {
        mockMvc.perform(get("/api/v1/trips/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.tripId", is(1)))
                .andExpect(jsonPath("$.data.actualTotalSpent", greaterThanOrEqualTo(440.0)))
                .andExpect(jsonPath("$.data.remainingBudget", notNullValue()))
                .andExpect(jsonPath("$.data.isOverBudget", is(false)));
    }

    @Test
    @DisplayName("PATCH /api/v1/trips/{id}/status updates status successfully")
    void testUpdateTripStatus() throws Exception {
        TripStatusUpdateRequest updateRequest = new TripStatusUpdateRequest(TripStatus.IN_PROGRESS);

        mockMvc.perform(patch("/api/v1/trips/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("IN_PROGRESS")));
    }

    @Test
    @DisplayName("POST /api/v1/trips with past date is rejected with 400 Bad Request")
    void testRejectPastDateTrip() throws Exception {
        TripRequest pastRequest = new TripRequest(
                "Connaught Place, Delhi",
                "Delhi",
                LocalDate.now().minusDays(2),
                2000.0,
                8,
                1,
                "Food Walk"
        );

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pastRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.data.tripDate", is("Trip date cannot be in the past")));
    }
}
