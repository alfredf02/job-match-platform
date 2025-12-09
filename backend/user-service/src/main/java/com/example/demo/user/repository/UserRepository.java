package com.example.demo.user.repository;

import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository interface for User entity
public interface UserRepository extends JpaRepository<User, Long> {
}