package com.daytripai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AuthResponse DTO.
 * Successful authentication response containing JWT access token,
 * refresh token, expiration time, and sanitized user profile.
 */
@Schema(description = "Authentication response with JWT tokens")
public class AuthResponse {

    @Schema(description = "JWT Bearer access token")
    private String accessToken;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Access token expiry duration in milliseconds", example = "86400000")
    private Long expiresIn;

    @Schema(description = "Opaque refresh token for session renewals")
    private String refreshToken;

    @Schema(description = "Authenticated user profile")
    private UserProfileResponse user;

    public AuthResponse() {
    }

    public AuthResponse(String accessToken, Long expiresIn, String refreshToken, UserProfileResponse user) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserProfileResponse getUser() {
        return user;
    }

    public void setUser(UserProfileResponse user) {
        this.user = user;
    }
}
