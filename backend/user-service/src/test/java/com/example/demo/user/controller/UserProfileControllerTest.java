// src/test/java/com/jobmatch/user/controller/UserProfileControllerTest.java
package com.example.demo.user.controller;

import com.example.demo.user.service.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.user.dto.UpdateProfileRequest;
import com.example.demo.user.dto.UserProfileResponse;
import com.example.demo.user.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private UserProfileController userProfileController;

    @BeforeEach
    void setUpSecurityContext() {
        // Seed SecurityContext with the JWT principal the controller expects.
        JwtAuthenticationFilter.JwtUserPrincipal principal =
                new JwtAuthenticationFilter.JwtUserPrincipal(1L, "test@example.com");
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Build standalone MockMvc instance without loading full context or filters.
        this.mockMvc = MockMvcBuilders.standaloneSetup(userProfileController).build();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void get_profile_me_returns_profile() throws Exception {
        // arrange: stub profile lookup for current user id
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(1L);
        response.setEmail("test@example.com");
        response.setLocation("NYC");
        when(userProfileService.getCurrentUserProfile(1L)).thenReturn(response);

        // act/assert: GET /me should return profile data
        mockMvc.perform(get("/api/profile/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.location").value("NYC"));
    }

    @Test
    void update_profile_updates_and_returns() throws Exception {
        // arrange: build request and stub service update
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setLocation("SF");
        request.setSkills("Java,Spring");
        request.setMinSalary(100000);
        request.setMaxSalary(150000);
        request.setDesiredRoles("Backend Engineer");

        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(1L);
        response.setEmail("test@example.com");
        response.setLocation("SF");
        response.setSkills("Java,Spring");
        response.setMinSalary(100000);
        response.setMaxSalary(150000);
        response.setDesiredRoles("Backend Engineer");
        when(userProfileService.updateProfile(eq(1L), any(UpdateProfileRequest.class))).thenReturn(response);

        // act/assert: PUT update should echo back updated fields
        mockMvc.perform(put("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.location").value("SF"))
                .andExpect(jsonPath("$.skills").value("Java,Spring"))
                .andExpect(jsonPath("$.minSalary").value(100000))
                .andExpect(jsonPath("$.maxSalary").value(150000))
                .andExpect(jsonPath("$.desiredRoles").value("Backend Engineer"));
    }
}