package com.daytripai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest DTO.
 * Payload for authenticating an existing user.
 */
@Schema(description = "User login credentials payload")
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email format")
    @Schema(description = "Registered email address", example = "traveler@daytripai.com")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Schema(description = "Account password", example = "Traveler@123")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
