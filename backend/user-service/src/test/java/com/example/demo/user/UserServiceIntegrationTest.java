// src/test/java/com/example/demo/user/UserServiceIntegrationTest.java
package com.example.demo.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.user.dto.LoginRequest;
import com.example.demo.user.dto.RegisterRequest;
import com.example.demo.user.dto.UpdateProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest // Boots the full Spring context with real beans, repositories, and security.
@ActiveProfiles("test")

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // Reset context between tests.
class UserServiceIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUpMockMvc() {
        // Build MockMvc manually and register the Spring Security filter chain to exercise real authentication.
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void register_login_and_profile_flow() throws Exception {
    // Use a unique email so we don't hit "Email is already registered"
    String testEmail = "integration+" + System.currentTimeMillis() + "@example.com";

    // Arrange register payload
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setEmail(testEmail);
    registerRequest.setPassword("Password123!");
    registerRequest.setFullName("Int Test");

    // Act: call register endpoint
    String registerResponseBody = mockMvc.perform(
                    post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
            .andDo(print()) // keep while debugging; you can remove later
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.email").value(testEmail))
            .andExpect(jsonPath("$.token").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JsonNode registerJson = objectMapper.readTree(registerResponseBody);
    String registerToken = registerJson.get("token").asText();
    assertThat(registerToken).isNotBlank();

        // Arrange login payload
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);       
        loginRequest.setPassword("Password123!");

        // Act: call login endpoint
        String loginResponseBody = mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode loginJson = objectMapper.readTree(loginResponseBody);
        String loginToken = loginJson.get("token").asText();
        assertThat(loginToken).isNotBlank();

        // Act: fetch profile with JWT from login
        mockMvc.perform(
                        get("/api/profile/me")
                                .header("Authorization", "Bearer " + loginToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testEmail));

        // Arrange profile update payload
        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setLocation("Sydney");
        updateProfileRequest.setSkills("Java, Spring");
        updateProfileRequest.setMinSalary(80000);
        updateProfileRequest.setMaxSalary(120000);
        updateProfileRequest.setDesiredRoles("Backend Developer");

        // Act: update profile
        mockMvc.perform(
                        put("/api/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + loginToken)
                                .content(objectMapper.writeValueAsString(updateProfileRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Sydney"))
                .andExpect(jsonPath("$.skills").value("Java, Spring"));

        // Assert: fetch profile again to verify persisted changes
        mockMvc.perform(
                        get("/api/profile/me")
                                .header("Authorization", "Bearer " + loginToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Sydney"))
                .andExpect(jsonPath("$.skills").value("Java, Spring"));
    }
}