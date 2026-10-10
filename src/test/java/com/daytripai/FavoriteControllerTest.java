package com.daytripai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "traveler@daytripai.com", authorities = {"ROLE_USER"})
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/favorites returns bookmarked destinations for traveler")
    void testGetFavorites() throws Exception {
        mockMvc.perform(get("/api/v1/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", not(empty())));
    }

    @Test
    @DisplayName("POST and DELETE /api/v1/favorites/{destinationId} adds and removes bookmark")
    void testAddAndRemoveFavorite() throws Exception {
        // Add Destination ID 2 (Golconda Fort) to favorites
        mockMvc.perform(post("/api/v1/favorites/2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.destinationId", is(2)));

        // Check favorite status
        mockMvc.perform(get("/api/v1/favorites/2/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.isFavorite", is(true)));

        // Remove from favorites
        mockMvc.perform(delete("/api/v1/favorites/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // Re-check favorite status
        mockMvc.perform(get("/api/v1/favorites/2/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isFavorite", is(false)));
    }
}
