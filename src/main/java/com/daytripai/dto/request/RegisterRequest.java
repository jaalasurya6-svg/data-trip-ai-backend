package com.daytripai.dto.request;

import com.daytripai.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * RegisterRequest DTO.
 * Payload for registering a new user account with Day Trip AI.
 */
@Schema(description = "User registration payload")
public class RegisterRequest {

    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email format")
    @Schema(description = "Unique user email address", example = "wanderlust@example.com")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters")
    @Schema(description = "Account password (min 6 characters)", example = "SecretPass@123")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Schema(description = "Full name of the traveler", example = "Priya Sharma")
    private String fullName;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Schema(description = "Contact phone number", example = "+91 9876543210")
    private String phone;

    @Schema(description = "Account role type (default: ROLE_USER)", example = "ROLE_USER")
    private RoleType role = RoleType.ROLE_USER;

    public RegisterRequest() {
    }

    public RegisterRequest(String email, String password, String fullName, String phone, RoleType role) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role != null ? role : RoleType.ROLE_USER;
    }

    // Getters and Setters
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }
}
