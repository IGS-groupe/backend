package com.example.backend.dto;

import lombok.Data;

@Data
public class SignUpDto {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String phoneNumber; 
    private String genre;
}
