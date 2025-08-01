package com.example.backend.services;



import java.util.List;
import java.util.Optional;

import com.example.backend.entity.AnalysisStatus;
import com.example.backend.entity.Demande;
import com.example.backend.entity.User;

public interface DemandeService {
    Demande saveDemande(Demande demande);
    Demande getDemandeByDemandeId(Long demandeId);
    List<Demande> getAllDemandes();
    List<Demande> findAllByUserId(Long userId);  
    List<Demande> getDemandesByUserId(Long userId);
    Demande updateDemande(Long id ,Demande demande);
    void deleteDemande(Long id);
    void updateState(Long demandeId , AnalysisStatus etat);
    
    // New methods for managing multiple clients
    void addClientToDemande(Long demandeId, Long clientId);
    void removeClientFromDemande(Long demandeId, Long clientId);
    List<Demande> getDemandesByClientId(Long clientId);
}
