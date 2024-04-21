package com.example.backend.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Echantillon;
import com.example.backend.entity.Priorite;


public interface EchantillonRepository extends JpaRepository<Echantillon, Long> {
    Echantillon findByEchantillonId(Long echantillonId);
    List<Echantillon> findByPriorite(Priorite priorite);
}
