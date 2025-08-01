package com.example.backend.services;


import org.springframework.stereotype.Service;

import com.example.backend.entity.AnalysisStatus;
import com.example.backend.entity.Demande;
import com.example.backend.entity.Echantillon;
import com.example.backend.entity.User;
import com.example.backend.exception.DemandeNotFoundException;
import com.example.backend.repository.DemandeRepository;
import com.example.backend.repository.EchantillonRepository;
import com.example.backend.repository.UserRepository;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class DemandeServiceImpl implements DemandeService {

    private  DemandeRepository demandeRepository;
    private  EchantillonRepository echantillonRepository;
    private  UserRepository userRepository;
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
    public List<Demande> getDemandesByUserId(Long userId) {
        return demandeRepository.findByUserId(userId);
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
            
            // Update clients if provided
            if (demande.getClients() != null && !demande.getClients().isEmpty()) {
                existingDemande.getClients().clear();
                existingDemande.getClients().addAll(demande.getClients());
            }
            
            return demandeRepository.save(existingDemande);
        } else {
            throw new DemandeNotFoundException("Demande with ID " + id + " not found");
        }
    }
    @Override
    public void  updateState(Long demandeId , AnalysisStatus etat){
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
    
    // New methods for managing multiple clients
    @Override
    public void addClientToDemande(Long demandeId, Long clientId) {
        Optional<Demande> optionalDemande = demandeRepository.findById(demandeId);
        Optional<User> optionalClient = userRepository.findById(clientId);
        
        if (optionalDemande.isPresent() && optionalClient.isPresent()) {
            Demande demande = optionalDemande.get();
            User client = optionalClient.get();
            demande.addClient(client);
            demandeRepository.save(demande);
        } else {
            throw new DemandeNotFoundException("Demande or Client not found");
        }
    }
    
    @Override
    public void removeClientFromDemande(Long demandeId, Long clientId) {
        Optional<Demande> optionalDemande = demandeRepository.findById(demandeId);
        Optional<User> optionalClient = userRepository.findById(clientId);
        
        if (optionalDemande.isPresent() && optionalClient.isPresent()) {
            Demande demande = optionalDemande.get();
            User client = optionalClient.get();
            demande.removeClient(client);
            demandeRepository.save(demande);
        } else {
            throw new DemandeNotFoundException("Demande or Client not found");
        }
    }
    
    @Override
    public List<Demande> getDemandesByClientId(Long clientId) {
        return demandeRepository.findByClientsId(clientId);
    }
    
    // Add more methods if needed
}
