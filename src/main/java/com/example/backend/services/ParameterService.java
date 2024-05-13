package com.example.backend.services;



import java.util.List;
import java.util.Optional;

import com.example.backend.entity.Parameter;

public interface ParameterService {
    List<Parameter> getAll();
    Optional<Parameter> getParameterById(Long id);
    Parameter saveParameter(Parameter parameter);
    List<Parameter> findAllAvailableParameters();
    Parameter updateParameterAvailability(Long parameterId, boolean availability);
    List<Parameter> saveAllParameters(List<Parameter> parameters);
    void deleteParameter(Long id);
    Parameter updateParameter(Long id, Parameter parameter);
    // Add more methods if needed
}
