package com.daytripai.security;

import com.daytripai.enums.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * SecurityUtils Helper.
 * Provides static access to the currently authenticated user in the Spring SecurityContext.
 * Supports CustomUserDetails as well as standard Spring Security UserDetails from tests.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Retrieves the database ID of the currently authenticated user, if present on CustomUserDetails.
     */
    public static Optional<Long> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            return Optional.ofNullable(customUserDetails.getId());
        }

        return Optional.empty();
    }

    /**
     * Retrieves the email / username of the currently authenticated user.
     */
    public static Optional<String> getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return Optional.ofNullable(userDetails.getUsername());
        } else if (principal instanceof String email && !"anonymousUser".equals(email)) {
            return Optional.of(email);
        }

        return Optional.empty();
    }

    /**
     * Checks if current authenticated user has administrative privileges.
     */
    public static boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getRole() == RoleType.ROLE_ADMIN;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(RoleType.ROLE_ADMIN.name()) ||
                               a.getAuthority().equals("ROLE_ADMIN"));
    }
}
