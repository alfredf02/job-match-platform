package com.example.demo.matching.service;

import com.example.demo.matching.domain.MatchingJob;
import com.example.demo.matching.domain.MatchingUserProfile;
import com.example.demo.matching.dto.MatchResponse;
import com.example.demo.matching.repository.MatchingJobRepository;
import com.example.demo.matching.repository.MatchingUserProfileRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MatchingService {

    private static final double SKILL_WEIGHT = 0.60;
    private static final double LOCATION_WEIGHT = 0.15;
    private static final double SALARY_WEIGHT = 0.15;
    private static final double ROLE_WEIGHT = 0.10;

    private final MatchingUserProfileRepository matchingUserProfileRepository;
    private final MatchingJobRepository matchingJobRepository;

    public MatchingService(MatchingUserProfileRepository matchingUserProfileRepository,
                           MatchingJobRepository matchingJobRepository) {
        this.matchingUserProfileRepository = matchingUserProfileRepository;
        this.matchingJobRepository = matchingJobRepository;
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getMatchesForUser(Long userId, int limit) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId must be a positive number");
        }
        if (limit <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be greater than zero");
        }

        MatchingUserProfile userProfile = matchingUserProfileRepository.findByExternalUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));

        return matchingJobRepository.findAll().stream()
                .map(job -> buildMatchResponse(userProfile, job))
                .sorted(Comparator.comparingDouble(MatchResponse::getScore).reversed()
                        .thenComparing(MatchResponse::getJobId, Comparator.nullsLast(Long::compareTo)))
                .limit(limit)
                .toList();
    }

    private MatchResponse buildMatchResponse(MatchingUserProfile userProfile, MatchingJob job) {
        List<String> matchedSkills = matchedSkills(userProfile, job);
        List<String> missingSkills = missingSkills(userProfile, job);

        double skillScore = computeSkillScore(job, matchedSkills.size());
        double locationScore = computeLocationScore(userProfile.getLocation(), job.getLocation());
        double salaryScore = computeSalaryScore(
                userProfile.getSalaryMin(), userProfile.getSalaryMax(), job.getSalaryMin(), job.getSalaryMax());
        double roleScore = computeRoleScore(userProfile.getDesiredRoles(), job.getTitle());

        double finalScore = roundTo4(
                (SKILL_WEIGHT * skillScore)
                        + (LOCATION_WEIGHT * locationScore)
                        + (SALARY_WEIGHT * salaryScore)
                        + (ROLE_WEIGHT * roleScore));

        return new MatchResponse(
                job.getExternalJobId(),
                job.getTitle(),
                job.getCompany(),
                finalScore,
                matchedSkills,
                missingSkills
        );
    }

    private List<String> matchedSkills(MatchingUserProfile userProfile, MatchingJob job) {
        Set<String> userSkills = normalize(userProfile.getSkills());
        Set<String> jobSkills = normalize(job.getRequiredSkills());

        List<String> matched = jobSkills.stream()
                .filter(userSkills::contains)
                .sorted()
                .toList();

        return new ArrayList<>(matched);
    }

    private List<String> missingSkills(MatchingUserProfile userProfile, MatchingJob job) {
        Set<String> userSkills = normalize(userProfile.getSkills());
        Set<String> jobSkills = normalize(job.getRequiredSkills());

        List<String> missing = jobSkills.stream()
                .filter(skill -> !userSkills.contains(skill))
                .sorted()
                .toList();

        return new ArrayList<>(missing);
    }

    private double computeSkillScore(MatchingJob job, int matchedSkillsCount) {
        Set<String> jobSkills = normalize(job.getRequiredSkills());
        if (jobSkills.isEmpty()) {
            return 0.0;
        }
        return (double) matchedSkillsCount / jobSkills.size();
    }

    private double computeLocationScore(String userLocation, String jobLocation) {
        String normalizedUserLocation = normalizeText(userLocation);
        String normalizedJobLocation = normalizeText(jobLocation);

        if (normalizedUserLocation == null || normalizedJobLocation == null) {
            return 0.5;
        }
        return normalizedUserLocation.equals(normalizedJobLocation) ? 1.0 : 0.0;
    }

    private double computeSalaryScore(Integer userSalaryMin, Integer userSalaryMax, Integer jobSalaryMin, Integer jobSalaryMax) {
        if (userSalaryMin == null || userSalaryMax == null || jobSalaryMin == null || jobSalaryMax == null) {
            return 0.5;
        }

        boolean overlaps = userSalaryMin <= jobSalaryMax && jobSalaryMin <= userSalaryMax;
        return overlaps ? 1.0 : 0.0;
    }

    private double computeRoleScore(Set<String> desiredRoles, String jobTitle) {
        Set<String> normalizedDesiredRoles = normalize(desiredRoles);
        String normalizedJobTitle = normalizeText(jobTitle);

        if (normalizedDesiredRoles.isEmpty()) {
            return 0.5;
        }
        if (normalizedJobTitle == null) {
            return 0.0;
        }

        boolean anyMatch = normalizedDesiredRoles.stream()
                .anyMatch(normalizedJobTitle::contains);

        return anyMatch ? 1.0 : 0.0;
    }

    private Set<String> normalize(Set<String> input) {
        Set<String> normalized = new LinkedHashSet<>();
        if (input == null) {
            return normalized;
        }

        for (String value : input) {
            String normalizedValue = normalizeText(value);
            if (normalizedValue != null) {
                normalized.add(normalizedValue);
            }
        }

        return normalized;
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }

    private double roundTo4(double value) {
        return BigDecimal.valueOf(value)
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue();
    }
}