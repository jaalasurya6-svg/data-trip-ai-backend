package com.daytripai.dto.response;

import com.daytripai.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * UserProfileResponse DTO.
 * Publicly safe user profile representation excluding password hashes.
 */
@Schema(description = "User profile response")
public class UserProfileResponse {

    @Schema(description = "User unique ID", example = "1")
    private Long id;

    @Schema(description = "Registered email address", example = "traveler@daytripai.com")
    private String email;

    @Schema(description = "Full name", example = "Alex Traveler")
    private String fullName;

    @Schema(description = "Phone number", example = "+91 9123456780")
    private String phone;

    @Schema(description = "Role", example = "ROLE_USER")
    private RoleType role;

    @Schema(description = "Account registration timestamp")
    private LocalDateTime createdAt;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long id, String email, String fullName, String phone, RoleType role, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
