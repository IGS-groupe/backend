package com.example.backend.entity;
public enum AnalysisStatus {
    REQUEST_SUBMITTED("Demande d'analyse transmise et en attente d'acceptation", "✔"),
    PARTIAL_RESULTS("Résultats partiels", "⚪"),
    SAMPLE_REJECTED("Échantillon rejeté", "❌"),
    EXCEEDS_NORM("Dépassement de norme", "!"),
    RECEIVED_IN_PROGRESS("Reçu au laboratoire, en cours d'analyse", "🕒"),
    COMPLETE_RESULTS("Résultats complets", "✔"),
    NOT_POTABLE("Non-Potable", "‼");

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
