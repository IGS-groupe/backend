package com.example.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.ParameterDTO;
import com.example.backend.entity.Parameter;
import com.example.backend.services.EchantillonService;
import com.example.backend.services.ParameterService;

import lombok.AllArgsConstructor;

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
    public ResponseEntity<Parameter>  updparameter(@PathVariable Long id, @RequestBody ParameterDTO parameterDTO) {
        Parameter parameter = new Parameter();
        parameter.setName(parameterDTO.getName());
        parameter.setRdl(parameterDTO.getRdl());
        parameter.setUnit(parameterDTO.getUnit());
        parameter.setEchantillon(echantillonService.getEchantillonById(parameterDTO.getEchantillonId()));
        Parameter savedParameter = parameterService.updateParameter(id, parameter);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedParameter);
    }
}
