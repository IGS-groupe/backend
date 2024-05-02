package com.example.backend.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.entity.Echantillon;
import com.example.backend.entity.Priorite;


public interface EchantillonRepository extends JpaRepository<Echantillon, Long> {
    Echantillon findByEchantillonId(Long echantillonId);
    @Query("SELECT e FROM Echantillon e WHERE e.demande.demandeId = :demandeId")
    List<Echantillon> findAllByDemandeId(@Param("demandeId") Long demandeId);
    List<Echantillon> findByPriorite(Priorite priorite);
}
