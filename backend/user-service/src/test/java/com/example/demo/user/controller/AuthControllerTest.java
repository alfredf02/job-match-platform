package com.example.demo.user.controller;

import com.example.demo.user.dto.AuthResponse;
import com.example.demo.user.dto.LoginRequest;
import com.example.demo.user.dto.RegisterRequest;
import com.example.demo.user.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new TestResponseStatusExceptionAdvice())
                .build();
    }

    /**
     * Standalone MockMvc does not always map ResponseStatusException the same way as a full Spring Boot app.
     * This advice makes the mapping explicit for this unit-style controller test.
     */
    @RestControllerAdvice
    static class TestResponseStatusExceptionAdvice {
        @ExceptionHandler(ResponseStatusException.class)
        public org.springframework.http.ResponseEntity<Void> handle(ResponseStatusException ex) {
            return org.springframework.http.ResponseEntity.status(ex.getStatusCode()).build();
        }
    }

    @Test
    void register_returns_201_and_body() throws Exception {
        AuthResponse response = new AuthResponse();
        response.setUserId(1L);
        response.setEmail("new@example.com");
        response.setToken("fake-jwt");

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setFullName("New User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.token").value("fake-jwt"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void login_invalid_credentials_returns_401() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authService).login(any(LoginRequest.class));
        verify(authService, never()).register(any(RegisterRequest.class));
    }
}
