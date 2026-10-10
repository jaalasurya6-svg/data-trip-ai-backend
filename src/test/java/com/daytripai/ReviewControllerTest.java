package com.daytripai;

import com.daytripai.dto.request.ReviewRequest;
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
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/destinations/1/reviews returns paginated reviews")
    void testGetReviews() throws Exception {
        mockMvc.perform(get("/api/v1/destinations/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", not(empty())))
                .andExpect(jsonPath("$.data.content[0].rating", is(5)));
    }

    @Test
    @DisplayName("POST /api/v1/destinations/1/reviews creates review and updates destination rating")
    void testCreateReview() throws Exception {
        ReviewRequest request = new ReviewRequest(
                5,
                "The evening illumination and vibrant bazaar streets make this an absolute must-visit!"
        );

        mockMvc.perform(post("/api/v1/destinations/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.destinationId", is(1)))
                .andExpect(jsonPath("$.data.rating", is(5)))
                .andExpect(jsonPath("$.data.authorName", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/destinations/1/reviews with rating 6 fails validation")
    void testCreateReviewValidationFailure() throws Exception {
        ReviewRequest invalidRequest = new ReviewRequest(
                6,
                "Exceeds max rating"
        );

        mockMvc.perform(post("/api/v1/destinations/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }
}
