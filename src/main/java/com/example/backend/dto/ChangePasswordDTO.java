package com.example.backend.dto;
import lombok.Data;
@Data
public class ChangePasswordDTO {
    private String usernameOrEmail; 
    private String password;
    private String newPassword;
}