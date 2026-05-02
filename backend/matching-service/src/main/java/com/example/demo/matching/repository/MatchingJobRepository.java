package com.example.demo.matching.repository;

import com.example.demo.matching.domain.MatchingJob;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingJobRepository extends JpaRepository<MatchingJob, Long> {

    Optional<MatchingJob> findByExternalJobId(Long externalJobId);
}