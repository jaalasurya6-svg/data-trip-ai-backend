package com.daytripai.service.impl;

import com.daytripai.dto.request.LoginRequest;
import com.daytripai.dto.request.RefreshTokenRequest;
import com.daytripai.dto.request.RegisterRequest;
import com.daytripai.dto.request.UpdateProfileRequest;
import com.daytripai.dto.response.AuthResponse;
import com.daytripai.dto.response.UserProfileResponse;
import com.daytripai.entity.RefreshToken;
import com.daytripai.entity.User;
import com.daytripai.enums.RoleType;
import com.daytripai.exception.BadRequestException;
import com.daytripai.exception.DuplicateResourceException;
import com.daytripai.exception.ResourceNotFoundException;
import com.daytripai.repository.RefreshTokenRepository;
import com.daytripai.repository.UserRepository;
import com.daytripai.security.jwt.JwtTokenProvider;
import com.daytripai.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Concrete Implementation of AuthService.
 * Handles user onboarding, secure credential verification, JWT issuance,
 * cryptographic refresh token rotation, and profile maintenance.
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Value("${app.jwt.refresh-expiration-days:7}")
    private int refreshExpirationDays;

    public AuthServiceImpl(UserRepository userRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            log.warn("Registration rejected: Email '{}' already registered", normalizedEmail);
            throw new DuplicateResourceException("User", "email", normalizedEmail);
        }

        RoleType role = request.getRole() != null ? request.getRole() : RoleType.ROLE_USER;

        User user = new User(
                normalizedEmail,
                passwordEncoder.encode(request.getPassword()),
                request.getFullName().trim(),
                role
        );
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);

        User savedUser = userRepository.save(user);
        log.info("Registered new user ID {} with email '{}' and role {}", savedUser.getId(), savedUser.getEmail(), role);

        String accessToken = tokenProvider.generateToken(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole().name()
        );

        RefreshToken refreshToken = createRefreshToken(savedUser);

        return new AuthResponse(
                accessToken,
                tokenProvider.getExpirationMs(),
                refreshToken.getToken(),
                mapToProfile(savedUser)
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            log.warn("Failed authentication attempt for email '{}'", normalizedEmail);
            throw new BadRequestException("Invalid email or password");
        }

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", normalizedEmail));

        if (!user.isEnabled()) {
            throw new BadRequestException("User account is disabled. Please contact support.");
        }

        String accessToken = tokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name()
        );

        RefreshToken refreshToken = createRefreshToken(user);
        log.info("User '{}' (ID: {}) logged in successfully", user.getEmail(), user.getId());

        return new AuthResponse(
                accessToken,
                tokenProvider.getExpirationMs(),
                refreshToken.getToken(),
                mapToProfile(user)
        );
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String tokenStr = request.getRefreshToken().trim();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            log.warn("Refresh token expired or revoked for user ID {}", refreshToken.getUser().getId());
            refreshTokenRepository.delete(refreshToken);
            throw new BadRequestException("Refresh token has expired. Please sign in again.");
        }

        User user = refreshToken.getUser();

        // Token Rotation: revoke old token and create a fresh one
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        RefreshToken newRefreshToken = createRefreshToken(user);

        String newAccessToken = tokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name()
        );

        log.info("Rotated refresh token and generated new access token for user '{}'", user.getEmail());

        return new AuthResponse(
                newAccessToken,
                tokenProvider.getExpirationMs(),
                newRefreshToken.getToken(),
                mapToProfile(user)
        );
    }

    @Override
    public void logout(String refreshToken) {
        if (StringUtils.hasText(refreshToken)) {
            refreshTokenRepository.findByToken(refreshToken.trim()).ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
                log.info("Revoked refresh token for user ID {}", token.getUser().getId());
            });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return mapToProfile(user);
    }

    @Override
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }

        User updated = userRepository.save(user);
        log.info("Updated profile for user ID {}", updated.getId());
        return mapToProfile(updated);
    }

    // Helper: Creates and persists a cryptographically secure refresh token
    private RefreshToken createRefreshToken(User user) {
        RefreshToken token = new RefreshToken(
                user,
                UUID.randomUUID().toString(),
                Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS)
        );
        return refreshTokenRepository.save(token);
    }

    // Helper: Map JPA entity to public UserProfileResponse
    private UserProfileResponse mapToProfile(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
