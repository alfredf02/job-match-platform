package com.example.demo.matching;

import com.example.demo.matching.Match;
import com.example.demo.matching.MatchRequestDto;
import com.example.demo.matching.MatchResponseDto;
import com.example.demo.matching.MatchRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Business logic layer for creating and retrieving matches.
 */
@Service
@Validated
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    /**
     * Fetch matches for a user ordered by score and recency.
     */
    public List<MatchResponseDto> getMatchesByUserId(Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        return matchRepository.findByUserIdOrderByScoreDescCreatedAtDesc(userId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /**
     * Create a new match from the provided request DTO.
     */
    public MatchResponseDto createMatch(@Valid MatchRequestDto request) {
        Objects.requireNonNull(request, "request must not be null");

        Match match = toEntity(request);
        Match saved = matchRepository.save(match);
        return toResponseDto(saved);
    }

    private Match toEntity(MatchRequestDto request) {
        Match match = new Match();
        match.setUserId(request.getUserId());
        match.setJobId(request.getJobId());
        match.setScore(request.getScore());
        match.setStatus(request.getStatus());
        return match;
    }

    private MatchResponseDto toResponseDto(Match match) {
        return new MatchResponseDto(
                match.getId(),
                match.getUserId(),
                match.getJobId(),
                match.getScore(),
                match.getStatus(),
                match.getCreatedAt()
        );
    }
}