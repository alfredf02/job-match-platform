package com.example.demo.user.repository;

import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Repository interface for User entity
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}