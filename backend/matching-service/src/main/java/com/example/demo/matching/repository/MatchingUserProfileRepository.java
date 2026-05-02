package com.example.demo.matching.repository;

import com.example.demo.matching.domain.MatchingUserProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingUserProfileRepository extends JpaRepository<MatchingUserProfile, Long> {

    Optional<MatchingUserProfile> findByExternalUserId(Long externalUserId);
}