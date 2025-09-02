package com.example.backend.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UserDTO  {
     private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber; 
    private List<String> roles;
    private boolean active;
    private MultipartFile image;
}
