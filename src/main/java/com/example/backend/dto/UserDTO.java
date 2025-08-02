package com.example.backend.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UserDTO  {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber; 
    private MultipartFile image;
}
