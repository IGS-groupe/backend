package com.example.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.DemandeDTO;
import com.example.backend.entity.Demande;
import com.example.backend.entity.Langue;
import com.example.backend.services.DemandeService;
import com.example.backend.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/demandes")
public class DemandeController {
    private final DemandeService demandeService;
    private UserService userService;
    @PostMapping
    public ResponseEntity<?> createDemande(@RequestBody DemandeDTO demandeDTO) {
        Demande demande = new Demande();
        demande.setDemandePour(demandeDTO.getDemandePour());
        demande.setEnvoyeAuLaboratoire(demandeDTO.getEnvoyeAuLaboratoire());
        demande.setCourrielsSupplementaires(demandeDTO.getCourrielsSupplementaires());
        demande.setBonDeCommande(demandeDTO.getBonDeCommande());
        demande.setUnEchantillon(demandeDTO.isUnEchantillon());
        demande.setLangueDuCertificat(Langue.valueOf(demandeDTO.getLangueDuCertificat().toUpperCase())); // Convert to uppercase
        demande.setCommentairesInternes(demandeDTO.getCommentairesInternes());
        
        // Retrieve user by ID and set to demande
        demande.setUser(userService.getUserById(demandeDTO.getUserId()));

        // Save the new demande
        Demande savedDemande = demandeService.saveDemande(demande);

        // Create a response with the ID of the newly created demande
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Demande created successfully!");
        response.put("demandeId", savedDemande.getDemandeId()); // Return only the demande ID

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
