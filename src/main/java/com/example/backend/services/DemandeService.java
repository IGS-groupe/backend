package com.example.backend.services;



import java.util.List;

import com.example.backend.entity.Demande;

public interface DemandeService {
    Demande saveDemande(Demande demande);

    Demande getDemandeByDemandeId(Long demandeId);


    List<Demande> getAllDemandes();

    Demande updateDemande(Long id ,Demande demande);
    
    void deleteDemande(Long id);
    // Add more methods if needed
}
