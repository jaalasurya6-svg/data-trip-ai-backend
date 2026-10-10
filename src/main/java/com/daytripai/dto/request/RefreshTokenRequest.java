package com.daytripai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * RefreshTokenRequest DTO.
 * Payload containing the refresh token used to request a new access token.
 */
@Schema(description = "Token refresh payload")
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token cannot be blank")
    @Schema(description = "Opaque refresh token previously issued", example = "d9f8e4b1-8b2c-4f9e-a89c-07e3b8a1f2c4")
    private String refreshToken;

    public RefreshTokenRequest() {
    }

    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
