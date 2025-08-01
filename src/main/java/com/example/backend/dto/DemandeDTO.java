package com.example.backend.dto;

import com.example.backend.entity.AnalysisStatus;
import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class DemandeDTO {
    private String demandePour;
    private String envoyeAuLaboratoire;
    private String courrielsSupplementaires;
    private String bonDeCommande;
    private boolean unEchantillon;
    private String langueDuCertificat;
    private String commentairesInternes;
    private AnalysisStatus etat = AnalysisStatus.REQUEST_SUBMITTED;
    
    // Keep original userId for backward compatibility
    private Long userId;
    
    // Add support for multiple client IDs
    private List<Long> clientIds;
    
    // Optional: Client names for display purposes
    private List<String> clientNames;
}
