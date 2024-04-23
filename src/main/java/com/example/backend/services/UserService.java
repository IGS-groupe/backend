package com.example.backend.services;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.example.backend.entity.User;

public interface UserService {
    User createUser(User user);

    User getUserById(Long userId);

    UserDetails loadUserByUsername(String username);
    
    List<User> getAllUsers();

    User updateUser(User user);

    void deleteUser(Long userId);
} 
