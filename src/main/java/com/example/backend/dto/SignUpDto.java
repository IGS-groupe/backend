package com.example.backend.dto;

import lombok.Data;

@Data
public class SignUpDto {
    private String nom;
    private String prenom;
    private String email;
    private String motdepasse;
    private String genre;
}
