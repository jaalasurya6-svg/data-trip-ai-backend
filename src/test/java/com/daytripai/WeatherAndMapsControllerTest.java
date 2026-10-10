package com.daytripai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WeatherAndMapsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/weather/forecast returns temperature, condition, and recommendation")
    void testWeatherForecastEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/weather/forecast")
                        .param("lat", "18.9220")
                        .param("lon", "72.8347")
                        .param("city", "Gateway of India, Mumbai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.city", containsString("Mumbai")))
                .andExpect(jsonPath("$.data.currentTemperature", notNullValue()))
                .andExpect(jsonPath("$.data.condition", notNullValue()))
                .andExpect(jsonPath("$.data.recommendation", notNullValue()))
                .andExpect(jsonPath("$.data.provider", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/weather/destinations/1 returns weather for seeded destination")
    void testWeatherForDestinationEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/weather/destinations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.currentTemperature", notNullValue()))
                .andExpect(jsonPath("$.data.condition", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/maps/route calculates route distance, duration, steps, and fare")
    void testMapsRouteEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/maps/route")
                        .param("fromLat", "18.9220")
                        .param("fromLon", "72.8347")
                        .param("toLat", "18.9633")
                        .param("toLon", "72.8311")
                        .param("mode", "DRIVING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.distanceKm", greaterThan(0.0)))
                .andExpect(jsonPath("$.data.durationMinutes", greaterThan(0)))
                .andExpect(jsonPath("$.data.transportMode", is("DRIVING")))
                .andExpect(jsonPath("$.data.estimatedFare", greaterThan(50.0)))
                .andExpect(jsonPath("$.data.steps", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /api/v1/maps/distance computes road-adjusted distance")
    void testMapsDistanceEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/maps/distance")
                        .param("lat1", "18.9220")
                        .param("lon1", "72.8347")
                        .param("lat2", "18.9633")
                        .param("lon2", "72.8311"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.distanceKm", greaterThan(0.0)))
                .andExpect(jsonPath("$.data.unit", is("kilometers")));
    }
}
