package com.example.demo.matching;

import com.example.demo.matching.Match;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository that handles persistence for Match entities.
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByUserIdOrderByScoreDescCreatedAtDesc(Long userId);
}