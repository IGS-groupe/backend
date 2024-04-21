package com.example.backend.services;



import org.springframework.stereotype.Service;

import com.example.backend.entity.Echantillon;
import com.example.backend.entity.Gabarit;
import com.example.backend.entity.Priorite;
import com.example.backend.entity.TypeEchantillon;
import com.example.backend.repository.EchantillonRepository;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EchantillonServiceImpl implements EchantillonService {

    private  EchantillonRepository echantillonRepository;

    @Override
    public Echantillon saveEchantillon(Echantillon echantillon) {
        return echantillonRepository.save(echantillon);
    }

    @Override
    public List<Echantillon> getAllEchantillons() {
        return echantillonRepository.findAll();
    }

    @Override
    public Echantillon getEchantillonById(Long id) {
        return echantillonRepository.findByEchantillonId(id);
    }

    
    @Override
    public void deleteEchantillon(Long id) {
        echantillonRepository.deleteById(id);
    }

    @Override
    public List<Echantillon> getEchantillonsByPriorite(Priorite priorite) {
        return echantillonRepository.findByPriorite(priorite);
    }
    @Override
    public Echantillon updatedEnchantillion(Long id ,Echantillon echantillon) {
    Echantillon existingenchantillion = echantillonRepository.findById(echantillon.getEchantillonId()).orElse(null);
    if (existingenchantillion != null) {
        echantillon.setGabarit(echantillon.getGabarit());
        echantillon.setTypeEchantillon(echantillon.getTypeEchantillon());
        echantillon.setNormeEchantillon(echantillon.getNormeEchantillon());
        echantillon.setNomEchantillon(echantillon.getNomEchantillon());
        echantillon.setLieuPrelevement(echantillon.getLieuPrelevement());
        echantillon.setDateFinPrelevement(echantillon.getDateFinPrelevement());
        echantillon.setHeureFinPrelevement(echantillon.getHeureFinPrelevement());
        echantillon.setPriorite(echantillon.getPriorite());
        echantillon.setCommentairesInternes(echantillon.getCommentairesInternes());
        echantillon.setDemande(echantillon.getDemande());

        return echantillonRepository.save(existingenchantillion);
    }
    return null; // Or throw an exception indicating the demande is not found
}

    
}
