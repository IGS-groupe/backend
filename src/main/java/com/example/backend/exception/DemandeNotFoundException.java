package com.example.backend.exception;

public class DemandeNotFoundException extends RuntimeException {
    public DemandeNotFoundException(String message) {
        super(message);
    }
}
