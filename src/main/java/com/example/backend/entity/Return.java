package com.example.backend.entity;

public enum Return {
    RETURN_60_DAYS("After 60 days ($0.30/sample/month)"),
    RETURN_90_DAYS("After 90 days ($0.15/sample/month)"),
    RETURN_3_MONTHS("After 3 months ($0.20/sample/month)"),
    RETURN_30_DAYS("After 30 days ($0.20/sample/month)");

    private final String description;

    Return(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
