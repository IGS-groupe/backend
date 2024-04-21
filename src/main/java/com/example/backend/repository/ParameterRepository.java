package com.example.backend.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.backend.entity.Parameter;


public interface ParameterRepository extends JpaRepository<Parameter, Long> {
    Optional<Parameter> findByParameterId(Long id);
}
