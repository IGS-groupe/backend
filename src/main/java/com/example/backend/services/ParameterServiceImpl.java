package com.example.backend.services;



import org.springframework.stereotype.Service;

import com.example.backend.entity.Parameter;
import com.example.backend.exception.ParameterNotFoundException;
import com.example.backend.repository.ParameterRepository;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ParameterServiceImpl implements ParameterService {

    private ParameterRepository parameterRepository;

    @Override
    public List<Parameter> getAll() {
        return parameterRepository.findAll();
    }
    
    @Override
    public Optional<Parameter> getParameterById(Long id) {
        return parameterRepository.findById(id);
    }

    @Override
    public Parameter saveParameter(Parameter parameter) {
        return parameterRepository.save(parameter);
    }
    @Override
    public List<Parameter> saveAllParameters(List<Parameter> parameters) {
        return parameterRepository.saveAll(parameters);
    }
    @Override
    public List<Parameter> findAllAvailableParameters() {
        return parameterRepository.findByAvailable(true);
    }
    @Override
    public void deleteParameter(Long id) {
        parameterRepository.deleteById(id);
    }
    @Override
    public Parameter updateParameterAvailability(Long parameterId, boolean availability) {
        Parameter parameter = parameterRepository.findById(parameterId)
            .orElseThrow(() -> new RuntimeException("Parameter not found with id: " + parameterId));
        parameter.setAvailable(availability);
        return parameterRepository.save(parameter);
    }
    @Override
    public Parameter updateParameter(Long id, Parameter parameter) {
        Optional<Parameter> optionalParameter = parameterRepository.findById(id);
    if (optionalParameter.isPresent()) {
        Parameter existingParameter = optionalParameter.get();
        existingParameter.setName(parameter.getName());
        existingParameter.setRdl(parameter.getRdl());
        existingParameter.setUnit(parameter.getUnit());
        return parameterRepository.save(existingParameter);
    } else {
        throw new ParameterNotFoundException("Parameter with ID " + id + " not found");
        }
    }

    // Add more methods if needed
    // Add more methods if needed
    }
