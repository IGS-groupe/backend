package com.example.backend.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.entity.Parameter;


public interface ParameterRepository extends JpaRepository<Parameter, Long> {
    Parameter findByParameterId(Long id);
}
