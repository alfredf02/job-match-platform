package com.example.demo.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service // Marks this as a Spring-managed component for JWT operations.
public class JwtService {

    // Secret key used to sign/verify JWTs (should be overridden via externalized config).
    private final String secret;

    // Token lifetime in seconds; controls how long issued tokens remain valid.
    private final long expirationSeconds;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expirationSeconds:3600}") long expirationSeconds) {
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(Long userId, String email) {
        // Subject is set to the user id to uniquely identify the principal; email is added as a claim.
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);

        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(expirationSeconds);

        return Jwts.builder()
                .setClaims(claims) // Custom claims include the email for downstream access.
                .setSubject(String.valueOf(userId)) // Use user id as the JWT subject.
                .setIssuedAt(Date.from(now)) // Standard issued-at timestamp.
                .setExpiration(Date.from(expiry)) // Enforces token expiry on validation.
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // HMAC SHA-256 signature.
                .compact();
    }

    public boolean isTokenValid(String token) {
        // Parsing the token will throw if the signature is invalid or expired; catching implies invalid.
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        // Subject was stored as the user id string.
        return Long.parseLong(extractAllClaims(token).getSubject());
    }

    public String extractEmail(String token) {
        // Email is stored as a custom claim.
        return extractAllClaims(token).get("email", String.class);
    }

    private Claims extractAllClaims(String token) {
        // Parses the JWT and verifies the signature using the configured secret key.
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        // The secret is expected to be Base64-encoded for proper key length; decode before building the key.
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

/*
Explanation
- Issues JWTs with the user id as the subject and email as a custom claim, signed with the configured HMAC secret.
- Validates and parses tokens to expose helper methods for extracting the authenticated user's id/email.
- Relies on externally configured secret/expiration to allow different environments to manage keys securely.
*/