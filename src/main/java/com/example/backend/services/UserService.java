package com.example.backend.services;
import java.util.List;

import com.example.backend.entity.User;

public interface UserService {
    User createUser(User user);

    User getUserById(Long userId);

    List<User> getAllUsers();

    User updateUser(User user);

    void deleteUser(Long userId);
    
    User getUserByEmail(String email);
    
} 
