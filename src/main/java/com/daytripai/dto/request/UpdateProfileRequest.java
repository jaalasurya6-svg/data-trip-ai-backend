package com.daytripai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * UpdateProfileRequest DTO.
 * Payload for updating a user's profile details.
 */
@Schema(description = "User profile update payload")
public class UpdateProfileRequest {

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Schema(description = "Updated full name", example = "Priya Sharma")
    private String fullName;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Schema(description = "Updated contact phone number", example = "+91 9123456789")
    private String phone;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
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
}
