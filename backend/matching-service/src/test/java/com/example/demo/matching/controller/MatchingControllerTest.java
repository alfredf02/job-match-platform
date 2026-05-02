package com.example.demo.matching.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.matching.dto.MatchResponse;
import com.example.demo.matching.service.MatchingService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class MatchingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MatchingService matchingService;

    @InjectMocks
    private MatchingController matchingController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(matchingController)
                .build();
    }

    @Test
    void getMatches_returns200AndJsonArray() throws Exception {
        MatchResponse response = new MatchResponse(
                101L,
                "Backend Developer",
                "TechCorp",
                0.95,
                List.of("java", "spring boot"),
                List.of("postgresql")
        );

        when(matchingService.getMatchesForUser(1L, 20)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/matches/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jobId").value(101L));

        verify(matchingService).getMatchesForUser(1L, 20);
    }

    @Test
    void getMatches_withLimit_passesLimitToServiceAndReturnsOneResult() throws Exception {
        MatchResponse response = new MatchResponse(
                101L,
                "Backend Developer",
                "TechCorp",
                0.95,
                List.of("java"),
                List.of()
        );

        when(matchingService.getMatchesForUser(1L, 1)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/matches/1").param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(matchingService).getMatchesForUser(1L, 1);
    }

    @Test
    void getMatches_responseContainsExpectedFields() throws Exception {
        MatchResponse response = new MatchResponse(
                101L,
                "Backend Developer",
                "TechCorp",
                0.95,
                List.of("java", "spring boot"),
                List.of("postgresql")
        );

        when(matchingService.getMatchesForUser(1L, 20)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/matches/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jobId").value(101L))
                .andExpect(jsonPath("$[0].title").value("Backend Developer"))
                .andExpect(jsonPath("$[0].company").value("TechCorp"))
                .andExpect(jsonPath("$[0].score").value(0.95))
                .andExpect(jsonPath("$[0].matchedSkills[0]").value("java"))
                .andExpect(jsonPath("$[0].missingSkills[0]").value("postgresql"));

        verify(matchingService).getMatchesForUser(1L, 20);
    }

    @Test
    void getMatches_notFoundFromService_returns404() throws Exception {
        when(matchingService.getMatchesForUser(eq(404L), eq(20)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));

        mockMvc.perform(get("/api/matches/404"))
                .andExpect(status().isNotFound());

        verify(matchingService).getMatchesForUser(404L, 20);
    }
}