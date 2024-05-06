package com.example.backend.services;



import java.util.List;
import java.util.Set;

import com.example.backend.entity.Echantillon;
import com.example.backend.entity.Parameter;
import com.example.backend.entity.Priorite;

public interface EchantillonService {
    List<Echantillon> getAllEchantillons();
    List<Echantillon> findAllByDemandeId(Long demandeId);
    Echantillon getEchantillonById(Long id);
    Set<Parameter> getParametersByEchantillonId(Long echantillonId);
    Echantillon saveEchantillon(Echantillon echantillon);
    void deleteEchantillon(Long id);
    Echantillon updateEchantillon(Long id, Echantillon echantillon);
    List<Echantillon> getEchantillonsByPriorite(Priorite priorite);
    // Add more methods if needed
}

