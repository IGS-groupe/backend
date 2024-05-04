package com.example.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.DemandeDTO;
import com.example.backend.entity.Demande;
import com.example.backend.entity.Langue;
import com.example.backend.services.DemandeService;
import com.example.backend.services.MailService;
import com.example.backend.services.UserService;

import jakarta.mail.MessagingException;

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
    @Autowired
    private MailService mailService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
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
    @PreAuthorize("hasRole('USER','ADMIN')")
    public ResponseEntity<Demande> getDemandeById(@PathVariable Long demandeId) {
        Demande demande = demandeService.getDemandeByDemandeId(demandeId);
        return ResponseEntity.ok(demande);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER','ADMIN')")
    public ResponseEntity<List<Demande>> getAllDemandes() {
        List<Demande> demandes = demandeService.getAllDemandes();
        return ResponseEntity.ok(demandes);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
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

    @PutMapping("/etat/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateState(@PathVariable Long id, @RequestBody String etat) {
        demandeService.updateState(id, etat);
        try {
            Demande demande  = demandeService.getDemandeByDemandeId(id);
            String email = demande.getCourrielsSupplementaires();
            mailService.sendStatusEmail( email,  etat);
            Map<String, Object> response = new HashMap<>();
            response.put("message","Demande update successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User registered but failed to send activation email");
        }
        
        
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER','ADMIN')")
    public ResponseEntity<String> deleteDemande(@PathVariable Long id) {
        demandeService.deleteDemande(id);
        return ResponseEntity.ok("Demande successfully deleted!");
    }
}
