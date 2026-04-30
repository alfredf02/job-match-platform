package com.example.demo.job.config;

import com.example.demo.job.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/jobs/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/employers/*").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/jobs/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/jobs/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/jobs/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/employers").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/employers/*/jobs").authenticated()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}