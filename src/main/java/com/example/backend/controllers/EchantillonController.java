package com.example.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.EchantillonDTO;
import com.example.backend.entity.Echantillon;
import com.example.backend.entity.Gabarit;
import com.example.backend.entity.Priorite;
import com.example.backend.entity.TypeEchantillon;
import com.example.backend.services.DemandeService;
import com.example.backend.services.EchantillonService;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@AllArgsConstructor
@RequestMapping("/api/echantillons")
public class EchantillonController {

    private final EchantillonService echantillonService;
    private final DemandeService demandeService;

    @PostMapping
    public ResponseEntity<?> saveEchantillon(@RequestBody EchantillonDTO echantillonDTO) {
        Echantillon echantillon = new Echantillon();
        echantillon.setGabarit(Gabarit.valueOf(echantillonDTO.getGabarit().toUpperCase()));
        echantillon.setTypeEchantillon(TypeEchantillon.valueOf(echantillonDTO.getTypeEchantillon().toUpperCase()));
        echantillon.setNormeEchantillon(echantillonDTO.getNormeEchantillon());
        echantillon.setNomEchantillon(echantillonDTO.getNomEchantillon());
        echantillon.setLieuPrelevement(echantillonDTO.getLieuPrelevement());
        echantillon.setDateFinPrelevement(echantillonDTO.getDateFinPrelevement());
        echantillon.setHeureFinPrelevement(echantillonDTO.getHeureFinPrelevement());
        echantillon.setPriorite(Priorite.valueOf(echantillonDTO.getPriorite().toUpperCase()));
        echantillon.setCommentairesInternes(echantillonDTO.getCommentairesInternes());
        echantillon.setDemande(demandeService.getDemandeByDemandeId(echantillonDTO.getDemandeId()));

        // Save the new echantillon
        Echantillon savedEchantillon = echantillonService.saveEchantillon(echantillon);

        // Create a response with the ID of the newly created echantillon
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Echantillon created successfully!");
        response.put("echantillonId", savedEchantillon.getEchantillonId()); // Return only the echantillon ID

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<Echantillon>> getAllEchantillons() {
        List<Echantillon> echantillons = echantillonService.getAllEchantillons();
        return ResponseEntity.ok(echantillons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Echantillon> getEchantillonById(@PathVariable Long id) {
        Echantillon echantillon = echantillonService.getEchantillonById(id);
        return ResponseEntity.ok(echantillon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEchantillon(@PathVariable Long id) {
        echantillonService.deleteEchantillon(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/priorite/{priorite}")
    public ResponseEntity<List<Echantillon>> getEchantillonsByPriorite(@PathVariable Priorite priorite) {
        List<Echantillon> echantillons = echantillonService.getEchantillonsByPriorite(priorite);
        return ResponseEntity.ok(echantillons);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Echantillon> UpdateEnchantillion(@PathVariable Long id, @RequestBody EchantillonDTO echantillonDTO) {
        Echantillon echantillon = new Echantillon();
        echantillon.setGabarit(Gabarit.valueOf(echantillonDTO.getGabarit().toUpperCase()));
        echantillon.setTypeEchantillon(TypeEchantillon.valueOf(echantillonDTO.getTypeEchantillon().toUpperCase()));
        echantillon.setNormeEchantillon(echantillonDTO.getNormeEchantillon());
        echantillon.setNomEchantillon(echantillonDTO.getNomEchantillon());
        echantillon.setLieuPrelevement(echantillonDTO.getLieuPrelevement());
        echantillon.setDateFinPrelevement(echantillonDTO.getDateFinPrelevement());
        echantillon.setHeureFinPrelevement(echantillonDTO.getHeureFinPrelevement());
        echantillon.setPriorite(Priorite.valueOf(echantillonDTO.getPriorite().toUpperCase()));
        echantillon.setCommentairesInternes(echantillonDTO.getCommentairesInternes());
        echantillon.setDemande(demandeService.getDemandeByDemandeId(echantillonDTO.getDemandeId()));
        Echantillon savedEchantillon = echantillonService.updatedEnchantillion(id, echantillon);
        return ResponseEntity.ok(savedEchantillon);
    }

}
