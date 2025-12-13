// src/test/java/com/jobmatch/user/controller/AuthControllerTest.java
package com.example.demo.user.controller;

import com.example.demo.user.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.user.dto.AuthResponse;
import com.example.demo.user.dto.LoginRequest;
import com.example.demo.user.dto.RegisterRequest;
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
    void setUp() {
        // Build a standalone MockMvc instance so we don't rely on @WebMvcTest or application context.
        this.mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void register_returns_201_and_body() throws Exception {
        // arrange: stub the service to return a successful auth response
        AuthResponse response = new AuthResponse();
        response.setUserId(1L);
        response.setEmail("new@example.com");
        response.setToken("fake-jwt");
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setFullName("New User");

        // act/assert: perform POST and verify status + payload
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
        // arrange: service throws when credentials are bad
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("wrong");

        // act/assert: POST login should surface 401
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authService).login(any(LoginRequest.class));
        verify(authService, never()).register(any(RegisterRequest.class));
    }
}