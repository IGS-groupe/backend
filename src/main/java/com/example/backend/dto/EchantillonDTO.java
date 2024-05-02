package com.example.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import lombok.Data;

@Data
public class EchantillonDTO {
    private String gabarit;
    private String typeEchantillon;
    private String normeEchantillon;
    private String nomEchantillon;
    private String lieuPrelevement;
    private LocalDate dateFinPrelevement;
    private LocalTime heureFinPrelevement;
    private String priorite;
    private String commentairesInternes;
    private Long demandeId;
    private Set<Long> parameterIds;  // IDs of associated parameters
}