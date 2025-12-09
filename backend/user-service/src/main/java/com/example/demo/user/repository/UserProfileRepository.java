package com.example.demo.user.repository;

import com.example.demo.user.domain.UserProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository dedicated to the UserProfile aggregate.
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    // Lookup profile by owning user id for profile retrieval/update endpoints.
    Optional<UserProfile> findByUserId(Long userId);
}