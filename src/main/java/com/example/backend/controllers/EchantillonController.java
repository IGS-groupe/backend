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

@RestController
@AllArgsConstructor
@RequestMapping("/api/echantillons")
public class EchantillonController {

    private final EchantillonService echantillonService;
    private final DemandeService demandeService;

    @PostMapping
    public ResponseEntity<?> saveEchantillon(@RequestBody EchantillonDTO echantillonDTO) {
        try {
            Echantillon echantillon = mapDtoToEntity(echantillonDTO);
            Echantillon savedEchantillon = echantillonService.saveEchantillon(echantillon);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Echantillon created successfully!");
            response.put("echantillonId", savedEchantillon.getEchantillonId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Echantillon>> getAllEchantillons() {
        return ResponseEntity.ok(echantillonService.getAllEchantillons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEchantillonById(@PathVariable Long id) {
        try {
            Echantillon echantillon = echantillonService.getEchantillonById(id);
            return ResponseEntity.ok(echantillon);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEchantillon(@PathVariable Long id) {
        echantillonService.deleteEchantillon(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/priorite/{priorite}")
    public ResponseEntity<List<Echantillon>> getEchantillonsByPriorite(@PathVariable Priorite priorite) {
        return ResponseEntity.ok(echantillonService.getEchantillonsByPriorite(priorite));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEchantillon(@PathVariable Long id, @RequestBody EchantillonDTO echantillonDTO) {
        try {
            Echantillon updatedEchantillon = echantillonService.updateEchantillon(id, mapDtoToEntity(echantillonDTO));
            return ResponseEntity.ok(updatedEchantillon);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    private Echantillon mapDtoToEntity(EchantillonDTO dto) {
        Echantillon echantillon = new Echantillon();
        echantillon.setGabarit(Gabarit.valueOf(dto.getGabarit().toUpperCase()));
        echantillon.setTypeEchantillon(TypeEchantillon.valueOf(dto.getTypeEchantillon().toUpperCase()));
        echantillon.setNormeEchantillon(dto.getNormeEchantillon());
        echantillon.setNomEchantillon(dto.getNomEchantillon());
        echantillon.setLieuPrelevement(dto.getLieuPrelevement());
        echantillon.setDateFinPrelevement(dto.getDateFinPrelevement());
        echantillon.setHeureFinPrelevement(dto.getHeureFinPrelevement());
        echantillon.setPriorite(Priorite.valueOf(dto.getPriorite().toUpperCase()));
        echantillon.setCommentairesInternes(dto.getCommentairesInternes());
        echantillon.setDemande(demandeService.getDemandeByDemandeId(dto.getDemandeId()));
        return echantillon;
    }
}
