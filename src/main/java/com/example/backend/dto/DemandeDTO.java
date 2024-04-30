package com.example.backend.dto;

import lombok.Data;

@Data
public class DemandeDTO {
    private String demandePour;
    private String envoyeAuLaboratoire;
    private String courrielsSupplementaires;
    private String bonDeCommande;
    private boolean unEchantillon;
    private String langueDuCertificat;
    private String commentairesInternes;
    private Long userId;

    }
