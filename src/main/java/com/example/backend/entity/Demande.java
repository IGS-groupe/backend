package com.example.backend.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;

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
    @Column(name = "Etat")
    private AnalysisStatus etat;

    @Enumerated(EnumType.STRING)
    @Column(name = "LangueDuCertificat")
    private Langue langueDuCertificat;
    
    @Column(name = "CommentairesInternes", columnDefinition = "TEXT")
    private String commentairesInternes;
    
    // Many-to-Many relationship with Users (clients)
   @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "demande_clients",
        joinColumns = @JoinColumn(name = "demande_id", referencedColumnName = "DemandeID"),
        inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    @JsonIgnore
    private Set<User> clients = new HashSet<>();


    
    
    // Helper methods
    public void addClient(User client) {
        this.clients.add(client);
    }
    
    public void removeClient(User client) {
        this.clients.remove(client);
    }
    
    // Getters and setters
}
