package com.example.backend.services;


import org.springframework.stereotype.Service;

import com.example.backend.entity.Demande;
import com.example.backend.entity.Echantillon;
import com.example.backend.exception.DemandeNotFoundException;
import com.example.backend.repository.DemandeRepository;
import com.example.backend.repository.EchantillonRepository;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class DemandeServiceImpl implements DemandeService {

    private  DemandeRepository demandeRepository;
    private  EchantillonRepository echantillonRepository;
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
    public Demande updateDemande(Long id, Demande demande) {
        Optional<Demande> optionalDemande = demandeRepository.findById(id);
        if (optionalDemande.isPresent()) {
            Demande existingDemande = optionalDemande.get();
            existingDemande.setDemandePour(demande.getDemandePour());
            existingDemande.setEnvoyeAuLaboratoire(demande.getEnvoyeAuLaboratoire());
            existingDemande.setCourrielsSupplementaires(demande.getCourrielsSupplementaires());
            existingDemande.setBonDeCommande(demande.getBonDeCommande());
            existingDemande.setUnEchantillon(demande.isUnEchantillon());
            existingDemande.setLangueDuCertificat(demande.getLangueDuCertificat());
            existingDemande.setCommentairesInternes(demande.getCommentairesInternes());
            existingDemande.setUser(demande.getUser());
            return demandeRepository.save(existingDemande);
        } else {
            throw new DemandeNotFoundException("Demande with ID " + id + " not found");
        }
    }
    @Override
    public void  updateState(Long demandeId , String etat){
        Optional<Demande> optionalDemande = demandeRepository.findById(demandeId);
        if (optionalDemande.isPresent()) {
            Demande demande = optionalDemande.get();
            demande.setEtat(etat);
            demandeRepository.save(demande);
        }
    }
    @Override
    public List<Demande> findAllByUserId(Long userId)   {
        return demandeRepository.findAllByUserId(userId);
    }
    @Override
    public void deleteDemande(Long id) {
        List<Echantillon> echantillons = echantillonRepository.findAllByDemandeId(id);
        echantillonRepository.deleteAll(echantillons);
        demandeRepository.deleteById(id);
    }
    
    // Add more methods if needed
}
