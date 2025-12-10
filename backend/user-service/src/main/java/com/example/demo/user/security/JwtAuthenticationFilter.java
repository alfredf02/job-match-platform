package com.example.demo.user.security;

import com.example.demo.user.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component // Registers the filter so it can be injected and added to the security chain.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Extract the Authorization header which should carry the bearer token.
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Strip the "Bearer " prefix.

            if (jwtService.isTokenValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                Long userId = jwtService.extractUserId(token);
                String email = jwtService.extractEmail(token);

                // Build a simple principal holding both id and email; no authorities for now.
                JwtUserPrincipal principal = new JwtUserPrincipal(userId, email);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Store the authentication so downstream filters/controllers see an authenticated user.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continue the filter chain regardless of authentication outcome.
        filterChain.doFilter(request, response);
    }

    /**
     * Simple principal type that carries the authenticated user's id/email.
     */
    public static class JwtUserPrincipal extends User {

        private final Long userId;

        public JwtUserPrincipal(Long userId, String email) {
            // Delegate to Spring Security's built-in User for compatibility with Authentication objects.
            super(email, "", java.util.Collections.emptyList());
            this.userId = userId;
        }

        public Long getUserId() {
            return userId;
        }
    }
}

/*
Explanation
- Reads bearer tokens from incoming requests, validating them through JwtService.
- On success, seeds the SecurityContext with an Authentication that carries the user id/email for controllers.
- Runs once per request before hitting protected endpoints, ensuring stateless JWT-based authentication.
*/