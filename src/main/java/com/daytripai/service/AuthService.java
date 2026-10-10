package com.daytripai.service;

import com.daytripai.dto.request.LoginRequest;
import com.daytripai.dto.request.RefreshTokenRequest;
import com.daytripai.dto.request.RegisterRequest;
import com.daytripai.dto.request.UpdateProfileRequest;
import com.daytripai.dto.response.AuthResponse;
import com.daytripai.dto.response.UserProfileResponse;

/**
 * Authentication & User Service Contract.
 * Manages user registration, credential authentication, JWT token issuance,
 * token rotation, and profile management.
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(String refreshToken);

    UserProfileResponse getCurrentUserProfile(Long userId);

    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);
}
