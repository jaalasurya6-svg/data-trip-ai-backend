package com.daytripai;

import com.daytripai.dto.request.LoginRequest;
import com.daytripai.dto.request.RefreshTokenRequest;
import com.daytripai.dto.request.RegisterRequest;
import com.daytripai.enums.RoleType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/auth/register successfully registers new user and issues JWT")
    void testRegisterSuccess() throws Exception {
        String uniqueEmail = "explorer_" + System.currentTimeMillis() + "@daytripai.com";
        RegisterRequest request = new RegisterRequest(
                uniqueEmail,
                "StrongPass@123",
                "Maya Patil",
                "+91 9988776655",
                RoleType.ROLE_USER
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.data.user.email", is(uniqueEmail)))
                .andExpect(jsonPath("$.data.user.fullName", is("Maya Patil")));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register fails with 409 Conflict on existing email")
    void testRegisterDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "traveler@daytripai.com",
                "Traveler@123",
                "Alex Traveler",
                "+91 9123456780",
                RoleType.ROLE_USER
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("User already exists")));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login succeeds with correct credentials")
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("traveler@daytripai.com", "Traveler@123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.data.user.email", is("traveler@daytripai.com")));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login fails with 400 Bad Request on invalid password")
    void testLoginBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("traveler@daytripai.com", "WrongPassword!999");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Invalid email or password")));
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh rotates token and returns new JWT access token")
    void testRefreshTokenSuccess() throws Exception {
        // Step 1: Login to get refresh token
        LoginRequest loginRequest = new LoginRequest("traveler@daytripai.com", "Traveler@123");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String refreshToken = loginJson.path("data").path("refreshToken").asText();

        // Step 2: Use refresh token
        RefreshTokenRequest refreshReq = new RefreshTokenRequest(refreshToken);
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.refreshToken", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/auth/me returns 401 Unauthorized without JWT")
    void testGetCurrentUserUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Unauthorized access")));
    }

    @Test
    @DisplayName("GET /api/v1/auth/me returns user profile when valid Bearer token provided")
    void testGetCurrentUserWithJwt() throws Exception {
        // Step 1: Login to get JWT access token
        LoginRequest loginRequest = new LoginRequest("traveler@daytripai.com", "Traveler@123");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = loginJson.path("data").path("accessToken").asText();

        // Step 2: Access protected /me endpoint with Authorization: Bearer <token>
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.email", is("traveler@daytripai.com")))
                .andExpect(jsonPath("$.data.fullName", is("Alex Traveler")));
    }
}
