package com.example.demo;

import com.example.demo.matching.domain.MatchingJob;
import com.example.demo.matching.domain.MatchingUserProfile;
import com.example.demo.matching.repository.MatchingJobRepository;
import com.example.demo.matching.repository.MatchingUserProfileRepository;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MatchingServiceIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MatchingUserProfileRepository matchingUserProfileRepository;

    @Autowired
    private MatchingJobRepository matchingJobRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        seedReadModelData();
    }

    @Test
    void getMatches_returnsRankedResultsWithLimit() throws Exception {
        mockMvc.perform(get("/api/matches/1").param("limit", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.lessThanOrEqualTo(3)))
                .andExpect(jsonPath("$[0].score").exists())
                .andExpect(jsonPath("$[0].matchedSkills").isArray())
                .andExpect(jsonPath("$[0].missingSkills").isArray());
    }

    private void seedReadModelData() {
        MatchingUserProfile profile = new MatchingUserProfile();
        profile.setExternalUserId(1L);
        profile.setLocation("Sydney");
        profile.setSalaryMin(80000);
        profile.setSalaryMax(120000);
        profile.setSkills(Set.of("Java", "Spring Boot", "PostgreSQL", "Docker"));
        profile.setDesiredRoles(Set.of("Backend Developer", "Software Engineer"));
        matchingUserProfileRepository.save(profile);

        matchingJobRepository.save(job(101L, "Backend Developer", "TechCorp", "Sydney", 90000, 120000,
                Set.of("Java", "Spring Boot", "PostgreSQL")));
        matchingJobRepository.save(job(102L, "Frontend Developer", "Webify", "Sydney", 70000, 100000,
                Set.of("React", "TypeScript", "CSS")));
        matchingJobRepository.save(job(103L, "Data Engineer", "DataWorks", "Melbourne", 100000, 140000,
                Set.of("Python", "PostgreSQL", "AWS")));
        matchingJobRepository.save(job(104L, "Junior Software Engineer", "StartupHub", "Sydney", 65000, 85000,
                Set.of("Java", "Git", "SQL")));
        matchingJobRepository.save(job(105L, "DevOps Engineer", "CloudOps", "Remote", 110000, 150000,
                Set.of("Docker", "AWS", "Kubernetes")));
    }

    private MatchingJob job(Long externalJobId,
                            String title,
                            String company,
                            String location,
                            Integer salaryMin,
                            Integer salaryMax,
                            Set<String> requiredSkills) {
        MatchingJob job = new MatchingJob();
        job.setExternalJobId(externalJobId);
        job.setTitle(title);
        job.setCompany(company);
        job.setLocation(location);
        job.setSalaryMin(salaryMin);
        job.setSalaryMax(salaryMax);
        job.setRequiredSkills(requiredSkills);
        return job;
    }
}