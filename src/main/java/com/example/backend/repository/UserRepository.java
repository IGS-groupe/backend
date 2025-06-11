package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByUsername(String username);
    List<User> findByRoles_Name(String roleName);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByActivationToken(String activationToken);
    Optional<User> findByResetToken(String resetToken);
    long countByRoles_Name(String roleName);

    
} 