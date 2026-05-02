package com.example.demo.matching.controller;

import com.example.demo.matching.dto.MatchResponse;
import com.example.demo.matching.service.MatchingService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")
@Validated
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping("/{userId}")
    public List<MatchResponse> getMatchesForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit) {
        return matchingService.getMatchesForUser(userId, limit);
    }
}