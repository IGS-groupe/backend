package com.example.backend.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Demande;

public interface DemandeRepository extends JpaRepository<Demande, Long> {
    Demande findByDemandeId(Long demandeId);
    Optional<Demande> findByDemandePour(String nom);
}