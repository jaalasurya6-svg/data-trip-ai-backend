package com.daytripai;

import com.daytripai.dto.request.ItineraryItemRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;

import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "traveler@daytripai.com", authorities = {"ROLE_USER"})
class ItineraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/trips/1/itinerary returns seeded itinerary with ordered stops")
    void testGetItinerary() throws Exception {
        mockMvc.perform(get("/api/v1/trips/1/itinerary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.tripId", is(1)))
                .andExpect(jsonPath("$.data.items", not(empty())))
                .andExpect(jsonPath("$.data.items[0].stopOrder", is(1)))
                .andExpect(jsonPath("$.data.budgetBreakdown", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/trips/1/itinerary/generate regenerates smart itinerary")
    void testGenerateItinerary() throws Exception {
        mockMvc.perform(post("/api/v1/trips/1/itinerary/generate"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.tripId", is(1)))
                .andExpect(jsonPath("$.data.items", not(empty())))
                .andExpect(jsonPath("$.data.totalDurationMinutes", greaterThan(0)));
    }

    @Test
    @DisplayName("POST /api/v1/trips/1/itinerary/items appends a custom stop")
    void testAddItineraryItem() throws Exception {
        ItineraryItemRequest itemRequest = new ItineraryItemRequest(
                null,
                "Chai Break at Nimrah Cafe",
                "Evening Irani chai and Osmania biscuits next to Charminar",
                LocalTime.of(17, 0),
                LocalTime.of(17, 45),
                100.0,
                "WALK",
                10,
                0.5
        );

        mockMvc.perform(post("/api/v1/trips/1/itinerary/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.items", hasItem(hasEntry("placeName", "Chai Break at Nimrah Cafe"))));
    }
}
