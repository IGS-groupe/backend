package com.example.backend.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "Parameter")
public class Parameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ParameterID")
    private Long parameterId;
    
    @Column(name = "Name")
    private String name;
    
    @Column(name = "RDL")
    private BigDecimal rdl;
    
    @Column(name = "Unit")
    private String unit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EchantillonID")
    private Echantillon echantillon;  
    
    // Getters and setters
}