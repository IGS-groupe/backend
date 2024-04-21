package com.example.backend.entity;

import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Demande")
public class Demande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DemandeID")
    private Long demandeId;
    
    @Column(name = "DemandePour")
    private String demandePour;
    
    @Column(name = "EnvoyeAuLaboratoire")
    private String envoyeAuLaboratoire;
    
    @Column(name = "CourrielsSupplementaires", columnDefinition = "TEXT")
    private String courrielsSupplementaires;
    
    @Column(name = "BonDeCommande")
    private String bonDeCommande;
    
    @Column(name = "UnEchantillon")
    private boolean unEchantillon;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "LangueDuCertificat")
    private Langue langueDuCertificat;
    
    @Column(name = "CommentairesInternes", columnDefinition = "TEXT")
    private String commentairesInternes;
    
    // Getters and setters
}
