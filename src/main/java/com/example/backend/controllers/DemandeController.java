package com.example.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.DemandeDTO;
import com.example.backend.entity.Demande;
import com.example.backend.entity.Langue;
import com.example.backend.services.DemandeService;
import com.example.backend.services.UserService;

import java.util.List;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/demandes")
public class DemandeController {
    private final DemandeService demandeService;
    private UserService userService;
    @PostMapping
    public ResponseEntity<Demande> createDemande(@RequestBody DemandeDTO demandeDTO) {
        Demande demande = new Demande();
        demande.setDemandePour(demandeDTO.getDemandePour());
        demande.setEnvoyeAuLaboratoire(demandeDTO.getEnvoyeAuLaboratoire());
        demande.setCourrielsSupplementaires(demandeDTO.getCourrielsSupplementaires());
        demande.setBonDeCommande(demandeDTO.getBonDeCommande());
        demande.setUnEchantillon(demandeDTO.isUnEchantillon());
        demande.setLangueDuCertificat(Langue.valueOf(demandeDTO.getLangueDuCertificat())); // Convert to uppercase
        demande.setCommentairesInternes(demandeDTO.getCommentairesInternes());
        demande.setUser(userService.getUserById(demandeDTO.getUserId()));
        Demande savedDemande = demandeService.saveDemande(demande);
        return new ResponseEntity<>(savedDemande, HttpStatus.CREATED);
    }

    @GetMapping("/{demandeId}")
    public ResponseEntity<Demande> getDemandeById(@PathVariable Long demandeId) {
        Demande demande = demandeService.getDemandeByDemandeId(demandeId);
        return ResponseEntity.ok(demande);
    }

    @GetMapping
    public ResponseEntity<List<Demande>> getAllDemandes() {
        List<Demande> demandes = demandeService.getAllDemandes();
        return ResponseEntity.ok(demandes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Demande> updateDemande(@PathVariable Long id, @RequestBody DemandeDTO demandeDTO) {
        Demande demande = new Demande();
        demande.setDemandePour(demandeDTO.getDemandePour());
        demande.setEnvoyeAuLaboratoire(demandeDTO.getEnvoyeAuLaboratoire());
        demande.setCourrielsSupplementaires(demandeDTO.getCourrielsSupplementaires());
        demande.setBonDeCommande(demandeDTO.getBonDeCommande());
        demande.setUnEchantillon(demandeDTO.isUnEchantillon());
        demande.setLangueDuCertificat(Langue.valueOf(demandeDTO.getLangueDuCertificat()));
        demande.setCommentairesInternes(demandeDTO.getCommentairesInternes());
        Demande updatedDemande = demandeService.updateDemande(id, demande);
        return ResponseEntity.ok(updatedDemande);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDemande(@PathVariable Long id) {
        demandeService.deleteDemande(id);
        return ResponseEntity.ok("Demande successfully deleted!");
    }
}
