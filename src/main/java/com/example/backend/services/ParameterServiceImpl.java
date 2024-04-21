package com.example.backend.services;



import org.springframework.stereotype.Service;

import com.example.backend.entity.Parameter;
import com.example.backend.repository.ParameterRepository;

import lombok.AllArgsConstructor;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ParameterServiceImpl implements ParameterService {

    private ParameterRepository parameterRepository;


    @Override
    public Optional<Parameter> getParameterById(Long id) {
        return parameterRepository.findById(id);
    }

    @Override
    public Parameter saveParameter(Parameter parameter) {
        return parameterRepository.save(parameter);
    }

    @Override
    public void deleteParameter(Long id) {
        parameterRepository.deleteById(id);
    }
    @Override
    public Parameter updateParameter(Long id, Parameter parameter) {
        Parameter existingParameter = parameterRepository.findById(id).orElse(null);
        if (existingParameter != null) {
            existingParameter.setName(parameter.getName());
            existingParameter.setRdl(parameter.getRdl());
            existingParameter.setUnit(parameter.getUnit());
            existingParameter.setEchantillon(parameter.getEchantillon()); 
                return parameterRepository.save(existingParameter);
        }
        return null;
    
    // Add more methods if needed
}
    // Add more methods if needed
}
