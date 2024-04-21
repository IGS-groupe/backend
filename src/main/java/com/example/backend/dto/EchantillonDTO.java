package com.example.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class EchantillonDTO {
    private String gabarit;
    private String typeEchantillon;
    private String normeEchantillon;
    private String nomEchantillon;
    private String lieuPrelevement;
    private LocalDate dateFinPrelevement;
    private LocalTime heureFinPrelevement;
    private String priorite;
    private String commentairesInternes;
    private Long demandeId;

    // Getters and setters
    public String getGabarit() {
        return gabarit;
    }

    public void setGabarit(String gabarit) {
        this.gabarit = gabarit;
    }

    public String getTypeEchantillon() {
        return typeEchantillon;
    }

    public void setTypeEchantillon(String typeEchantillon) {
        this.typeEchantillon = typeEchantillon;
    }

    public String getNormeEchantillon() {
        return normeEchantillon;
    }

    public void setNormeEchantillon(String normeEchantillon) {
        this.normeEchantillon = normeEchantillon;
    }

    public String getNomEchantillon() {
        return nomEchantillon;
    }

    public void setNomEchantillon(String nomEchantillon) {
        this.nomEchantillon = nomEchantillon;
    }

    public String getLieuPrelevement() {
        return lieuPrelevement;
    }

    public void setLieuPrelevement(String lieuPrelevement) {
        this.lieuPrelevement = lieuPrelevement;
    }

    public LocalDate getDateFinPrelevement() {
        return dateFinPrelevement;
    }

    public void setDateFinPrelevement(LocalDate dateFinPrelevement) {
        this.dateFinPrelevement = dateFinPrelevement;
    }

    public LocalTime getHeureFinPrelevement() {
        return heureFinPrelevement;
    }

    public void setHeureFinPrelevement(LocalTime heureFinPrelevement) {
        this.heureFinPrelevement = heureFinPrelevement;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public String getCommentairesInternes() {
        return commentairesInternes;
    }

    public void setCommentairesInternes(String commentairesInternes) {
        this.commentairesInternes = commentairesInternes;
    }

    public Long getDemandeId() {
        return demandeId;
    }

    public void setDemandeId(Long demandeId) {
        this.demandeId = demandeId;
    }
}
