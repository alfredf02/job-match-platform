package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class JobServiceIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    void employerJobLifecycle_flow() throws Exception {
        String createEmployerBody = mockMvc.perform(post("/api/employers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Acme Inc",
                                "website", "https://acme.test",
                                "description", "Platform company",
                                "location", "Remote"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long employerId = objectMapper.readTree(createEmployerBody).get("id").asLong();
        assertThat(employerId).isPositive();

        String createJobBody = mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "employerId", employerId,
                                "title", "Backend Engineer",
                                "description", "Build APIs",
                                "location", "Remote",
                                "salaryMin", 100000,
                                "salaryMax", 150000,
                                "workType", "REMOTE",
                                "seniority", "MID",
                                "skills", List.of("Java", "Spring Boot")
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.skills[0]").value("java"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long jobId = objectMapper.readTree(createJobBody).get("id").asLong();

        mockMvc.perform(get("/api/jobs/{jobId}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(jobId))
                .andExpect(jsonPath("$.title").value("Backend Engineer"))
                .andExpect(jsonPath("$.location").value("Remote"));

        mockMvc.perform(get("/api/jobs")
                        .param("location", "Remote")
                        .param("skill", "java")
                        .param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(jobId));

        mockMvc.perform(put("/api/jobs/{jobId}", jobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Senior Backend Engineer",
                                "seniority", "SENIOR",
                                "skills", List.of("Kotlin", "PostgreSQL")
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Senior Backend Engineer"))
                .andExpect(jsonPath("$.seniority").value("SENIOR"))
                .andExpect(jsonPath("$.skills[0]").value("kotlin"));

        mockMvc.perform(delete("/api/jobs/{jobId}", jobId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/jobs/{jobId}", jobId))
                .andExpect(status().isNotFound());
    }
}