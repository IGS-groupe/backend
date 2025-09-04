package com.example.backend.entity;
public enum AnalysisStatus {
    REQUEST_SUBMITTED("Analysis request submitted and awaiting acceptance", "✔"),
    PARTIAL_RESULTS("Partial results", "⚪"),
    SAMPLE_REJECTED("Sample rejected", "❌"),
    EXCEEDS_NORM("Exceeds standard", "!"),
    RECEIVED_IN_PROGRESS("Received at laboratory, analysis in progress", "🕒"),
    COMPLETE_RESULTS("Complete results", "✔"),
    NOT_POTABLE("Not potable", "‼");

    private final String description;
    private final String symbol;

    AnalysisStatus(String description, String symbol) {
        this.description = description;
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public String getSymbol() {
        return symbol;
    }
}
