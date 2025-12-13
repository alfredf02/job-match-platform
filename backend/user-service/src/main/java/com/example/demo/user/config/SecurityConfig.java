package com.example.demo.user.config;

import com.example.demo.user.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration // Declares this class contains Spring Security configuration.
@EnableWebSecurity // Enables Spring Security's web integration for the application.
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configure stateless JWT-based security for API endpoints.
        http
                .csrf(csrf -> csrf.disable()) // CSRF disabled because we are using stateless tokens.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow unauthenticated registration and login requests.
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        // All other API routes require authentication.
                        .requestMatchers("/api/**").authenticated()
                        // Non-API paths can remain accessible (adjust later as needed).
                        .anyRequest().permitAll())
                // Ensure JWT filter runs before the standard username/password filter.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

/*
Explanation
- Sets up stateless security: CSRF disabled, sessions off, and JWT filter inserted before UsernamePasswordAuthenticationFilter.
- Permits anonymous access to /api/auth/register and /api/auth/login while protecting remaining /api/** endpoints.
- Injects JwtAuthenticationFilter so validated tokens populate the SecurityContext for downstream controllers.
*/