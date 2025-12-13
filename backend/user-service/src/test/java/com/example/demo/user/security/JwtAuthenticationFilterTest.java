// src/test/java/com/jobmatch/user/security/JwtAuthenticationFilterTest.java
package com.jobmatch.user.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.user.security.JwtService;
import com.jobmatch.user.security.JwtAuthenticationFilter.JwtUserPrincipal;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock private JwtService jwtService;
    @Mock private FilterChain filterChain;

    @InjectMocks private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void valid_bearer_token_sets_authentication() throws Exception {
        // arrange
        String token = "valid-token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUserId(token)).thenReturn(123L);
        when(jwtService.extractEmail(token)).thenReturn("user@example.com");

        // act
        filter.doFilter(request, response, filterChain);

        // assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isInstanceOf(JwtUserPrincipal.class);
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        assertThat(principal.getUserId()).isEqualTo(123L);
        assertThat(principal.getUsername()).isEqualTo("user@example.com");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void missing_token_leaves_context_empty() throws Exception {
        // arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // act
        filter.doFilter(request, response, filterChain);

        // assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();
        verify(jwtService, never()).isTokenValid(any());
        verify(filterChain).doFilter(request, response);
    }
}