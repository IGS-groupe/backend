package com.example.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.EchantillonDTO;
import com.example.backend.entity.Demande;
import com.example.backend.entity.Dispose;
import com.example.backend.entity.Echantillon;
import com.example.backend.entity.TypeEchantillon;
import com.example.backend.entity.Priorite;
import com.example.backend.entity.Return;
import com.example.backend.repository.ParameterRepository;
import com.example.backend.services.DemandeService;
import com.example.backend.services.EchantillonService;
import com.example.backend.entity.Parameter;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@AllArgsConstructor
@RequestMapping("/api/echantillons")
public class EchantillonController {

    private final EchantillonService echantillonService;
    private final DemandeService demandeService;
    @Autowired
    private ParameterRepository parameterRepository;
    @PostMapping
    @PreAuthorize("hasRole('USER')")
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
    @PostMapping("/All/{demandeId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> saveBatchEchantillons(@PathVariable Long demandeId, @RequestBody List<EchantillonDTO> echantillonDTOList) {
        try {
            Demande demande = demandeService.getDemandeByDemandeId(demandeId); // Assume there's a method to fetch Demande
            List<Echantillon> echantillons = new ArrayList<>();
            for (EchantillonDTO echantillonDTO : echantillonDTOList) {
                echantillonDTO.setDemandeId(demandeId);
                Echantillon echantillon = mapDtoToEntity(echantillonDTO);
                echantillon.setDemande(demande); // Set the existing Demande
                Echantillon savedEchantillon = echantillonService.saveEchantillon(echantillon);
                echantillons.add(savedEchantillon);
            }
            List<Map<String, Object>> response = new ArrayList<>();
            for (Echantillon savedEchantillon : echantillons) {
                Map<String, Object> echantillonResponse = new HashMap<>();
                echantillonResponse.put("message", "Echantillon created successfully!");
                echantillonResponse.put("id", savedEchantillon.getEchantillonId());
                response.add(echantillonResponse);
            }
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }




    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Echantillon>> getAllEchantillons() {
        return ResponseEntity.ok(echantillonService.getAllEchantillons());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getEchantillonById(@PathVariable Long id) {
        try {
            Echantillon echantillon = echantillonService.getEchantillonById(id);
            return ResponseEntity.ok(echantillon);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{echantillonId}/parameters")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Set<Parameter>> getParametersByEchantillonId(@PathVariable Long echantillonId) {
        Set<Parameter> parameters = echantillonService.getParametersByEchantillonId(echantillonId);
        if (parameters.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(parameters);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteEchantillon(@PathVariable Long id) {
        echantillonService.deleteEchantillon(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/priorite/{priorite}")
    public ResponseEntity<List<Echantillon>> getEchantillonsByPriorite(@PathVariable Priorite priorite) {
        return ResponseEntity.ok(echantillonService.getEchantillonsByPriorite(priorite));
    }

    @GetMapping("/by-demande/{demandeId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Echantillon>> getEchantillonsByDemandeId(@PathVariable Long demandeId) {
        List<Echantillon> echantillons = echantillonService.findAllByDemandeId(demandeId);
        if (echantillons.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(echantillons);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateEchantillon(@PathVariable Long id, @RequestBody EchantillonDTO echantillonDTO) {
        try {
            Echantillon updatedEchantillon = echantillonService.updateEchantillon(id, mapDtoToEntity(echantillonDTO));
            return ResponseEntity.ok(updatedEchantillon);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    public Echantillon mapDtoToEntity(EchantillonDTO dto) {
        Echantillon echantillon = new Echantillon();
        echantillon.setTypeEchantillon(TypeEchantillon.valueOf(dto.getTypeEchantillon().toUpperCase()));
        echantillon.setReturns(Return.valueOf(dto.getReturns().toUpperCase())); // Correct Enum conversion
        echantillon.setDisposes(Dispose.valueOf(dto.getDisposes().toUpperCase())); // Correct Enum conversion
        echantillon.setNomEchantillon(dto.getNomEchantillon());
        echantillon.setLieuPrelevement(dto.getLieuPrelevement());
        echantillon.setAddressRetourner(dto.getAddressRetourner());
        echantillon.setDateFinPrelevement(dto.getDateFinPrelevement());
        echantillon.setHeureFinPrelevement(dto.getHeureFinPrelevement());
        echantillon.setPriorite(Priorite.valueOf(dto.getPriorite().toUpperCase()));
        echantillon.setCommentairesInternes(dto.getCommentairesInternes());
        echantillon.setDemande(demandeService.getDemandeByDemandeId(dto.getDemandeId()));

        if (dto.getParameterIds() != null && !dto.getParameterIds().isEmpty()) {
            Set<Parameter> parameters = dto.getParameterIds().stream()
                .map(id -> parameterRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            echantillon.setParameter(parameters);
        }

        return echantillon;
    }

}
