package com.example.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.DemandeDTO;
import com.example.backend.entity.AnalysisStatus;
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
    private final UserService userService;
    private final MailService mailService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createDemande(@RequestBody DemandeDTO demandeDTO) {
        try {
            Demande demande = new Demande();
            demande.setDemandePour(demandeDTO.getDemandePour());
            demande.setEnvoyeAuLaboratoire(demandeDTO.getEnvoyeAuLaboratoire());
            demande.setCourrielsSupplementaires(demandeDTO.getCourrielsSupplementaires());
            demande.setBonDeCommande(demandeDTO.getBonDeCommande());
            demande.setUnEchantillon(demandeDTO.isUnEchantillon());
            demande.setLangueDuCertificat(Langue.valueOf(demandeDTO.getLangueDuCertificat().toUpperCase()));  // Assuming Langue is also an enum
            demande.setCommentairesInternes(demandeDTO.getCommentairesInternes());
            demande.setEtat(AnalysisStatus.REQUEST_SUBMITTED);  // Set initial state using enum
            demande.setUser(userService.getUserById(demandeDTO.getUserId()));  // Assuming getUserById is correctly implemented

            Demande savedDemande = demandeService.saveDemande(demande);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Demande created successfully!");
            response.put("demandeId", savedDemande.getDemandeId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid language or status specified.");
        }
    }


    @GetMapping("/{demandeId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Demande> getDemandeById(@PathVariable Long demandeId) {
        Demande demande = demandeService.getDemandeByDemandeId(demandeId);
        if (demande == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(demande);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Demande>> getAllDemandes() {
        List<Demande> demandes = demandeService.getAllDemandes();
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Demande>> getDemandesByUserId(@PathVariable Long userId) {
        List<Demande> demandes = demandeService.getDemandesByUserId(userId);
        return ResponseEntity.ok(demandes);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateState(@PathVariable Long id, @RequestBody Map<String, String> etat) {
        try {
            AnalysisStatus status = AnalysisStatus.valueOf(etat.get("etat"));
            demandeService.updateState(id, status);
            Demande demande = demandeService.getDemandeByDemandeId(id);
            mailService.sendStatusEmail(demande.getCourrielsSupplementaires(), etat.get("etat"));
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Demande updated successfully");
            return ResponseEntity.ok(response);
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send status email.");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> deleteDemande(@PathVariable Long id) {
        demandeService.deleteDemande(id);
        return ResponseEntity.ok("Demande successfully deleted!");
    }
}
