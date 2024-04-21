package com.example.backend.services;



import java.util.List;

import com.example.backend.entity.Echantillon;
import com.example.backend.entity.Priorite;

public interface EchantillonService {
    List<Echantillon> getAllEchantillons();
    Echantillon getEchantillonById(Long id);
    Echantillon saveEchantillon(Echantillon echantillon);
    void deleteEchantillon(Long id);
    Echantillon updatedEnchantillion(Long id ,Echantillon echantillon);
    List<Echantillon> getEchantillonsByPriorite(Priorite priorite);
    // Add more methods if needed
}

