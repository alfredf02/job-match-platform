package com.example.demo.matching.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.demo.matching.domain.MatchingJob;
import com.example.demo.matching.domain.MatchingUserProfile;
import com.example.demo.matching.dto.MatchResponse;
import com.example.demo.matching.repository.MatchingJobRepository;
import com.example.demo.matching.repository.MatchingUserProfileRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private MatchingUserProfileRepository matchingUserProfileRepository;

    @Mock
    private MatchingJobRepository matchingJobRepository;

    @InjectMocks
    private MatchingService matchingService;

    @Test
    void returnsRankedMatchesByScoreDescending() {
        MatchingUserProfile user = userProfile(1L, "Sydney", 80000, 120000,
                Set.of("Java", "Spring Boot", "PostgreSQL"), Set.of("Backend Developer"));

        MatchingJob best = job(101L, "Backend Developer", "TechCorp", "Sydney", 90000, 120000,
                Set.of("Java", "Spring Boot", "PostgreSQL"));
        MatchingJob middle = job(102L, "Software Engineer", "StartupHub", "Sydney", 90000, 120000,
                Set.of("Java", "Git", "SQL"));
        MatchingJob worst = job(103L, "Frontend Developer", "Webify", "Melbourne", 70000, 90000,
                Set.of("React", "TypeScript", "CSS"));

        when(matchingUserProfileRepository.findByExternalUserId(1L)).thenReturn(Optional.of(user));
        when(matchingJobRepository.findAll()).thenReturn(List.of(worst, middle, best));

        List<MatchResponse> results = matchingService.getMatchesForUser(1L, 10);

        assertEquals(3, results.size());
        assertEquals(101L, results.get(0).getJobId());
        assertEquals(102L, results.get(1).getJobId());
        assertEquals(103L, results.get(2).getJobId());
    }

    @Test
    void calculatesMatchedAndMissingSkillsCaseInsensitively() {
        MatchingUserProfile user = userProfile(1L, "Sydney", 80000, 120000,
                Set.of("java", "SPRING BOOT"), Set.of("Backend Developer"));

        MatchingJob job = job(101L, "Backend Developer", "TechCorp", "Sydney", 90000, 120000,
                Set.of("Java", "Spring Boot", "PostgreSQL"));

        when(matchingUserProfileRepository.findByExternalUserId(1L)).thenReturn(Optional.of(user));
        when(matchingJobRepository.findAll()).thenReturn(List.of(job));

        MatchResponse response = matchingService.getMatchesForUser(1L, 10).get(0);

        assertEquals(List.of("java", "spring boot"), response.getMatchedSkills());
        assertEquals(List.of("postgresql"), response.getMissingSkills());
    }

    @Test
    void salaryOverlapIncreasesScore() {
        MatchingUserProfile user = userProfile(1L, "Sydney", 80000, 120000,
                Set.of("Java"), Set.of("Engineer"));

        MatchingJob overlap = job(101L, "Engineer", "TechCorp", "Sydney", 90000, 100000,
                Set.of("Java"));
        MatchingJob noOverlap = job(102L, "Engineer", "TechCorp", "Sydney", 130000, 150000,
                Set.of("Java"));

        when(matchingUserProfileRepository.findByExternalUserId(1L)).thenReturn(Optional.of(user));
        when(matchingJobRepository.findAll()).thenReturn(List.of(noOverlap, overlap));

        List<MatchResponse> results = matchingService.getMatchesForUser(1L, 10);

        assertEquals(101L, results.get(0).getJobId());
        assertEquals(102L, results.get(1).getJobId());
        assertEquals(true, results.get(0).getScore() > results.get(1).getScore());
    }

    @Test
    void locationMatchIncreasesScore() {
        MatchingUserProfile user = userProfile(1L, "Sydney", 80000, 120000,
                Set.of("Java"), Set.of("Engineer"));

        MatchingJob sydney = job(101L, "Engineer", "TechCorp", "Sydney", 90000, 100000,
                Set.of("Java"));
        MatchingJob melbourne = job(102L, "Engineer", "TechCorp", "Melbourne", 90000, 100000,
                Set.of("Java"));

        when(matchingUserProfileRepository.findByExternalUserId(1L)).thenReturn(Optional.of(user));
        when(matchingJobRepository.findAll()).thenReturn(List.of(melbourne, sydney));

        List<MatchResponse> results = matchingService.getMatchesForUser(1L, 10);

        assertEquals(101L, results.get(0).getJobId());
        assertEquals(102L, results.get(1).getJobId());
    }

    @Test
    void desiredRoleMatchIncreasesScore() {
        MatchingUserProfile user = userProfile(1L, "Sydney", 80000, 120000,
                Set.of("Java"), Set.of("Backend Developer"));

        MatchingJob related = job(101L, "Senior Backend Developer", "TechCorp", "Sydney", 90000, 100000,
                Set.of("Java"));
        MatchingJob unrelated = job(102L, "Finance Analyst", "BizOps", "Sydney", 90000, 100000,
                Set.of("Java"));

        when(matchingUserProfileRepository.findByExternalUserId(1L)).thenReturn(Optional.of(user));
        when(matchingJobRepository.findAll()).thenReturn(List.of(unrelated, related));

        List<MatchResponse> results = matchingService.getMatchesForUser(1L, 10);

        assertEquals(101L, results.get(0).getJobId());
        assertEquals(102L, results.get(1).getJobId());
    }

    @Test
    void throwsNotFoundWhenUserProfileMissing() {
        when(matchingUserProfileRepository.findByExternalUserId(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> matchingService.getMatchesForUser(999L, 10));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void respectsLimit() {
        MatchingUserProfile user = userProfile(1L, "Sydney", 80000, 120000,
                Set.of("Java"), Set.of("Engineer"));

        MatchingJob job1 = job(101L, "Engineer 1", "A", "Sydney", 90000, 100000, Set.of("Java"));
        MatchingJob job2 = job(102L, "Engineer 2", "B", "Sydney", 90000, 100000, Set.of("Java"));
        MatchingJob job3 = job(103L, "Engineer 3", "C", "Sydney", 90000, 100000, Set.of("Java"));
        MatchingJob job4 = job(104L, "Engineer 4", "D", "Sydney", 90000, 100000, Set.of("Java"));
        MatchingJob job5 = job(105L, "Engineer 5", "E", "Sydney", 90000, 100000, Set.of("Java"));

        when(matchingUserProfileRepository.findByExternalUserId(1L)).thenReturn(Optional.of(user));
        when(matchingJobRepository.findAll()).thenReturn(List.of(job1, job2, job3, job4, job5));

        List<MatchResponse> results = matchingService.getMatchesForUser(1L, 2);

        assertEquals(2, results.size());
    }

    private static MatchingUserProfile userProfile(Long externalUserId,
                                                   String location,
                                                   Integer salaryMin,
                                                   Integer salaryMax,
                                                   Set<String> skills,
                                                   Set<String> desiredRoles) {
        MatchingUserProfile profile = new MatchingUserProfile();
        profile.setExternalUserId(externalUserId);
        profile.setLocation(location);
        profile.setSalaryMin(salaryMin);
        profile.setSalaryMax(salaryMax);
        profile.setSkills(skills);
        profile.setDesiredRoles(desiredRoles);
        return profile;
    }

    private static MatchingJob job(Long externalJobId,
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