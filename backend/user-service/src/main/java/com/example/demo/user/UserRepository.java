package com.example.demo.user;

import org.springframework.data.jpa.repository.JpaRepository;

// Repository interface for User entity
public interface UserRepository extends JpaRepository<User, Long> {
}