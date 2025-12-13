// src/test/java/com/example/demo/user/security/JwtServiceTest.java
package com.example.demo.user.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // Base64-encoded secret for signing; in real deployments this should be provided securely via env/config.
        String secret =
                Base64.getEncoder()
                        .encodeToString("test-secret-key-test-secret-key".getBytes(StandardCharsets.UTF_8));
        jwtService = new JwtService(secret, 3600L);
    }

    @Test
    void generated_token_can_be_validated_and_parsed() {
        // arrange
        Long userId = 7L;
        String email = "user@example.com";

        // act
        String token = jwtService.generateToken(userId, email);

        // assert
        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractUserId(token)).isEqualTo(userId);
        assertThat(jwtService.extractEmail(token)).isEqualTo(email);
    }

    @Test
    void tampered_token_is_invalid() {
        // arrange
        String token = jwtService.generateToken(8L, "another@example.com");
        String tampered = token.substring(0, token.length() - 2) + "aa"; // Slight modification.

        // assert
        assertThat(jwtService.isTokenValid(tampered)).isFalse();
    }
}