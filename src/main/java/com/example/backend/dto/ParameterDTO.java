package com.example.backend.dto;

import java.math.BigDecimal;

import lombok.Data;
@Data
public class ParameterDTO {
    private String name;
    private BigDecimal rdl;
    private String unit;
    private boolean available;
}
