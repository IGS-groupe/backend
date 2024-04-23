package com.example.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.ParameterDTO;
import com.example.backend.entity.Parameter;
import com.example.backend.services.EchantillonService;
import com.example.backend.services.ParameterService;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@AllArgsConstructor
@RequestMapping("/api/parameters")
public class ParameterController {

    private final ParameterService parameterService;
    private final EchantillonService echantillonService;

    @PostMapping
    public ResponseEntity<Parameter> saveParameter(@RequestBody ParameterDTO parameterDTO) {
    Parameter parameter = new Parameter();
    parameter.setName(parameterDTO.getName());
    parameter.setRdl(parameterDTO.getRdl());
    parameter.setUnit(parameterDTO.getUnit());
    parameter.setEchantillon(echantillonService.getEchantillonById(parameterDTO.getEchantillonId()));
    Parameter savedParameter = parameterService.saveParameter(parameter);
     return ResponseEntity.status(HttpStatus.CREATED).body(savedParameter);
    }

    @GetMapping
    public ResponseEntity<List<Parameter>> getAllParameter() {
        List<Parameter> parameters = parameterService.getAll();
        return ResponseEntity.ok(parameters);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Parameter> getParameterById(@PathVariable Long id) {
        Optional<Parameter> parameter = parameterService.getParameterById(id);
        return parameter.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParameter(@PathVariable Long id) {
        parameterService.deleteParameter(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Parameter> updateParameter(@PathVariable Long id, @RequestBody ParameterDTO parameterDTO) {
    Optional<Parameter> optionalParameter = parameterService.getParameterById(id);
    if (optionalParameter.isPresent()) {
        Parameter parameter = optionalParameter.get();
        parameter.setName(parameterDTO.getName());
        parameter.setRdl(parameterDTO.getRdl());
        parameter.setUnit(parameterDTO.getUnit());
        parameter.setEchantillon(echantillonService.getEchantillonById(parameterDTO.getEchantillonId()));
        Parameter updatedParameter = parameterService.updateParameter(id, parameter);
        return ResponseEntity.ok(updatedParameter);
    } else {
        return ResponseEntity.notFound().build();
    }
}   
}
