package com.example.backend.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.entity.AnalysisStatus;
import com.example.backend.entity.Demande;

import jakarta.transaction.Transactional;

public interface DemandeRepository extends JpaRepository<Demande, Long> {
    Demande findByDemandeId(Long demandeId);
    @Query("SELECT d FROM Demande d WHERE d.user.id = :userId")
    List<Demande> findAllByUserId(@Param("userId") Long userId);    
    List<Demande> findByUserId(Long userId);
    Optional<Demande> findByDemandePour(String nom);
    @Transactional
    @Modifying
    @Query("UPDATE Demande d SET d.etat = :etat WHERE d.demandeId = :demandeId")
    void updateState(@Param("demandeId") Long demandeId, @Param("etat") String etat);
    long countByEtat(AnalysisStatus etat);
    
    // New method for many-to-many relationship
    @Query("SELECT d FROM Demande d JOIN d.clients c WHERE c.id = :clientId")
    List<Demande> findByClientsId(@Param("clientId") Long clientId);
}
