package com.example.backend.services;



import java.util.Optional;

import com.example.backend.entity.Parameter;

public interface ParameterService {
    Optional<Parameter> getParameterById(Long id);
    Parameter saveParameter(Parameter parameter);
    void deleteParameter(Long id);
    Parameter updateParameter(Long id, Parameter parameter);
    // Add more methods if needed
}