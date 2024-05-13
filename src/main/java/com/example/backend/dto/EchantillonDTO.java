package com.example.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import lombok.Data;

@Data
public class EchantillonDTO {
    private String disposes;
    private String returns;
    private String typeEchantillon;
    private String nomEchantillon;
    private String lieuPrelevement;
    private String addressRetourner;
    private LocalDate dateFinPrelevement;
    private LocalTime heureFinPrelevement;
    private String priorite;
    private String commentairesInternes;
    private Long demandeId;
    private Set<Long> parameterIds;  // IDs of associated parameters
}