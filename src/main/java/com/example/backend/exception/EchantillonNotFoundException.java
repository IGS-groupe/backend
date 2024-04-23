package com.example.backend.exception;

public class EchantillonNotFoundException extends RuntimeException {
    public EchantillonNotFoundException(String message) {
        super(message);
    }
}
