package com.example.demo.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // Marks this class as a source of bean definitions for the application context.
public class SecurityBeansConfig {

    @Bean // Exposes a PasswordEncoder bean for injection across the application.
    public PasswordEncoder passwordEncoder() {
        // BCrypt is a strong adaptive hashing algorithm suitable for passwords.
        return new BCryptPasswordEncoder();
    }
}

/*
Explanation
- Provides a shared PasswordEncoder bean so services can hash and verify passwords consistently.
- Centralizes security-related bean creation, making it easy for other components to inject the encoder.
*/