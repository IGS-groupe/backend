package com.example.backend.services;


import org.springframework.stereotype.Service;

import com.example.backend.entity.Demande;
import com.example.backend.repository.DemandeRepository;

import lombok.AllArgsConstructor;

import java.util.List;


@Service
@AllArgsConstructor
public class DemandeServiceImpl implements DemandeService {

    private  DemandeRepository demandeRepository;

    @Override
    public Demande saveDemande(Demande demande) {
        return demandeRepository.save(demande);
    }

    @Override
    public List<Demande> getAllDemandes() {
        return demandeRepository.findAll();
    }

    @Override
    public Demande getDemandeByDemandeId(Long demandeId) {
        return demandeRepository.findByDemandeId(demandeId);
    }

    @Override
    public Demande updateDemande(Long id ,Demande demande) {
    Demande existingDemande = demandeRepository.findById(demande.getDemandeId()).orElse(null);
    if (existingDemande != null) {
        existingDemande.setDemandePour(demande.getDemandePour());
        existingDemande.setEnvoyeAuLaboratoire(demande.getEnvoyeAuLaboratoire());
        existingDemande.setCourrielsSupplementaires(demande.getCourrielsSupplementaires());
        existingDemande.setBonDeCommande(demande.getBonDeCommande());
        existingDemande.setUnEchantillon(demande.isUnEchantillon());
        existingDemande.setLangueDuCertificat(demande.getLangueDuCertificat());
        existingDemande.setCommentairesInternes(demande.getCommentairesInternes());

        return demandeRepository.save(existingDemande);
    }
    return null; // Or throw an exception indicating the demande is not found
}

    @Override
    public void deleteDemande(Long id) {
        demandeRepository.deleteById(id);
    }


    // Add more methods if needed
}
