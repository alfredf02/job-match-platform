package com.example.demo.matching;

import com.example.demo.matching.MatchRequestDto;
import com.example.demo.matching.MatchResponseDto;
import com.example.demo.matching.MatchService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing match operations.
 */
@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    /**
     * GET /api/matches?userId=... -> list of matches for a user.
     */
    @GetMapping
    public ResponseEntity<List<MatchResponseDto>> getMatchesByUserId(@RequestParam("userId") Long userId) {
        List<MatchResponseDto> matches = matchService.getMatchesByUserId(userId);
        return ResponseEntity.ok(matches);
    }

    /**
     * POST /api/matches -> create a new match.
     */
    @PostMapping
    public ResponseEntity<MatchResponseDto> createMatch(@Valid @RequestBody MatchRequestDto request) {
        MatchResponseDto created = matchService.createMatch(request);
        return ResponseEntity.status(201).body(created);
    }
}
