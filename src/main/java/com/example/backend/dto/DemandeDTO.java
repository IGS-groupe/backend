package com.example.backend.dto;

public class DemandeDTO {
    private String demandePour;
    private String envoyeAuLaboratoire;
    private String courrielsSupplementaires;
    private String bonDeCommande;
    private boolean unEchantillon;
    private String langueDuCertificat;
    private String commentairesInternes;
    private Long userId;

    public boolean getUnEchantillon() {
        return this.unEchantillon;
    }


    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getDemandePour() {
        return demandePour;
    }

    public void setDemandePour(String demandePour) {
        this.demandePour = demandePour;
    }

    public String getEnvoyeAuLaboratoire() {
        return envoyeAuLaboratoire;
    }

    public void setEnvoyeAuLaboratoire(String envoyeAuLaboratoire) {
        this.envoyeAuLaboratoire = envoyeAuLaboratoire;
    }

    public String getCourrielsSupplementaires() {
        return courrielsSupplementaires;
    }

    public void setCourrielsSupplementaires(String courrielsSupplementaires) {
        this.courrielsSupplementaires = courrielsSupplementaires;
    }

    public String getBonDeCommande() {
        return bonDeCommande;
    }

    public void setBonDeCommande(String bonDeCommande) {
        this.bonDeCommande = bonDeCommande;
    }

    public boolean isUnEchantillon() {
        return unEchantillon;
    }

    public void setUnEchantillon(boolean unEchantillon) {
        this.unEchantillon = unEchantillon;
    }

    public String getLangueDuCertificat() {
        return langueDuCertificat;
    }

    public void setLangueDuCertificat(String langueDuCertificat) {
        this.langueDuCertificat = langueDuCertificat;
    }

    public String getCommentairesInternes() {
        return commentairesInternes;
    }

    public void setCommentairesInternes(String commentairesInternes) {
        this.commentairesInternes = commentairesInternes;
    }
}
