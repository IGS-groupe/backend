package com.example.backend.dto;

import java.math.BigDecimal;

public class ParameterDTO {
    private String name;
    private BigDecimal rdl;
    private String unit;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getRdl() {
        return rdl;
    }

    public void setRdl(BigDecimal rdl) {
        this.rdl = rdl;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}
