package com.example.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.ParameterDTO;
import com.example.backend.entity.Parameter;
import com.example.backend.services.EchantillonService;
import com.example.backend.services.ParameterService;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@AllArgsConstructor
@RequestMapping("/api/parameters")
public class ParameterController {

    private final ParameterService parameterService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveParameters(@RequestBody ParameterDTO parameterDTO) {
        Parameter parameter = new Parameter();
        parameter.setName(parameterDTO.getName());
        parameter.setRdl(parameterDTO.getRdl());
        parameter.setUnit(parameterDTO.getUnit());
        
        Parameter savedParameter = parameterService.saveParameter(parameter); // Returns the saved Parameter object

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Parameter saved successfully!");
        response.put("savedParameter", savedParameter); // Optionally include the saved parameter details
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<Parameter>> getAllParameter() {
        List<Parameter> parameters = parameterService.getAll();
        return ResponseEntity.ok(parameters);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Parameter> getParameterById(@PathVariable Long id) {
        Optional<Parameter> parameter = parameterService.getParameterById(id);
        return parameter.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteParameter(@PathVariable Long id) {
        parameterService.deleteParameter(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Parameter> updateParameter(@PathVariable Long id ,@PathVariable Long paramaterId, @RequestBody ParameterDTO parameterDTO) {
    Optional<Parameter> optionalParameter = parameterService.getParameterById(id);
    if (optionalParameter.isPresent()) {
        Parameter parameter = optionalParameter.get();
        parameter.setName(parameterDTO.getName());
        parameter.setRdl(parameterDTO.getRdl());
        parameter.setUnit(parameterDTO.getUnit());
        Parameter updatedParameter = parameterService.updateParameter(id, parameter);
        return ResponseEntity.ok(updatedParameter);
    } else {
        return ResponseEntity.notFound().build();
    }
}   
}
