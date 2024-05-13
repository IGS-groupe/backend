package com.example.backend.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Echantillon")
public class Echantillon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EchantillonID")
    private Long echantillonId;
    
    @Enumerated(EnumType.STRING)
    private TypeEchantillon typeEchantillon;
    

    @Enumerated(EnumType.STRING)
    private Return returns;  
    
    @Enumerated(EnumType.STRING)
    private Dispose disposes; 
    
    @Column(name = "NomEchantillon")
    private String nomEchantillon;
    
    @Column(name = "LieuPrelevement")
    private String lieuPrelevement;
    
    @Column(name = "AddressRetourner") 
    private String addressRetourner;

    @Column(name = "DateFinPrelevement")
    private LocalDate dateFinPrelevement;
    
    @Column(name = "HeureFinPrelevement")
    private LocalTime heureFinPrelevement;
    
    @Enumerated(EnumType.STRING)
    private Priorite priorite;
    
    @Column(name = "CommentairesInternes")
    private String commentairesInternes;
    
    @ManyToOne
    @JoinColumn(name = "DemandeID")
    private Demande demande;    

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "echantillon_parameters",
        joinColumns = @JoinColumn(name = "echantillon_id", referencedColumnName = "echantillonId"),
        inverseJoinColumns = @JoinColumn(name = "parameter_id", referencedColumnName = "parameterId"))
    private Set<Parameter> parameter;
}
