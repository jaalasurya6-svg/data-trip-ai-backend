package com.daytripai.security;

import com.daytripai.security.jwt.JwtAuthenticationEntryPoint;
import com.daytripai.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 6 & JWT Configuration.
 * Configures stateless session management, BCrypt password encoder,
 * CORS policies, endpoint role authorization, and the JWT filter pipeline.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Public Documentation & Health Endpoints
                        .requestMatchers(
                                "/api/v1/health/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 2. Public Authentication Endpoints
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/auth/logout").permitAll()

                        // 3. Public Discovery & Information Endpoints
                        .requestMatchers(HttpMethod.GET, "/api/v1/destinations/**").permitAll()
                        .requestMatchers("/api/v1/ai/status").permitAll()
                        .requestMatchers("/api/v1/weather/**").permitAll()
                        .requestMatchers("/api/v1/maps/**").permitAll()

                        // 4. Authenticated Review Creation & Deletion (precedes destination wildcard)
                        .requestMatchers(HttpMethod.POST, "/api/v1/destinations/*/reviews").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/reviews/*").authenticated()

                        // 5. Admin Destination Modification Endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/destinations/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/destinations/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/destinations/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")

                        // 6. User Protected Endpoints
                        .requestMatchers("/api/v1/auth/me", "/api/v1/auth/profile").authenticated()
                        .requestMatchers("/api/v1/trips/**").authenticated()
                        .requestMatchers("/api/v1/favorites/**").authenticated()
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow localhost, Vercel deployments, Render, and custom domains
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://*.vercel.app",
                "https://*.onrender.com",
                "*"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "X-User-Id"
        ));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
