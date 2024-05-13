package com.example.backend.entity;

public enum Dispose {
    DISPOSE_60_DAYS("Dispose after 60 days ($0.30/sample/month)"),
    ANALYSIS_90_DAYS("Analysis after 90 days ($0.15/sample/month)"),
    DISPOSE_3_MONTHS("Dispose after 3 months ($0.20/sample/month)"),
    DISPOSE_30_DAYS("Dispose after 30 days ($0.20/sample/month)");

    private final String description;

    Dispose(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
