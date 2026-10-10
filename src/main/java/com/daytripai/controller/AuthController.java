package com.daytripai.controller;

import com.daytripai.dto.request.LoginRequest;
import com.daytripai.dto.request.RefreshTokenRequest;
import com.daytripai.dto.request.RegisterRequest;
import com.daytripai.dto.request.UpdateProfileRequest;
import com.daytripai.dto.response.ApiResponse;
import com.daytripai.dto.response.AuthResponse;
import com.daytripai.dto.response.UserProfileResponse;
import com.daytripai.exception.BadRequestException;
import com.daytripai.security.SecurityUtils;
import com.daytripai.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication & User Profile REST Controller.
 * Handles user onboarding, login with JWT issuance, refresh token rotation,
 * session logout, and user profile inspection/editing.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication & User", description = "User registration, JWT login, token refresh, and profile management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register New User", description = "Creates a new user account and returns JWT access and refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate User", description = "Authenticates user credentials and issues JWT Bearer token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Access Token", description = "Rotates refresh token and issues a new JWT access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout User", description = "Invalidates the supplied refresh token")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        if (request != null) {
            authService.logout(request.getRefreshToken());
        }
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @GetMapping("/me")
    @Operation(summary = "Get Current User Profile", description = "Retrieves sanitized profile of the currently authenticated user")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BadRequestException("No authenticated user session found"));

        UserProfileResponse profile = authService.getCurrentUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update User Profile", description = "Updates full name or contact number for the currently authenticated user")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BadRequestException("No authenticated user session found"));

        UserProfileResponse updated = authService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }
}
