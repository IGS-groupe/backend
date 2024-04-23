package com.example.backend.services;



import org.springframework.stereotype.Service;

import com.example.backend.entity.Echantillon;
import com.example.backend.entity.Parameter;
import com.example.backend.entity.Priorite;
import com.example.backend.exception.EchantillonNotFoundException;
import com.example.backend.repository.EchantillonRepository;
import com.example.backend.repository.ParameterRepository;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EchantillonServiceImpl implements EchantillonService {

    private  EchantillonRepository echantillonRepository;
    private ParameterRepository parameterRepository;
    
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
        List<Parameter> parameters = parameterRepository.findAllByEchantillonId(id);
        parameterRepository.deleteAll(parameters);
        echantillonRepository.deleteById(id);
    }

    @Override
    public List<Echantillon> getEchantillonsByPriorite(Priorite priorite) {
        return echantillonRepository.findByPriorite(priorite);
    }
    @Override
    public Echantillon updatedEnchantillion(Long id ,Echantillon echantillon) {
        Optional<Echantillon> optionalEchantillon = echantillonRepository.findById(id);
        if (optionalEchantillon.isPresent()) {
            Echantillon existingEchantillon = optionalEchantillon.get();
            existingEchantillon.setGabarit(echantillon.getGabarit());
            existingEchantillon.setTypeEchantillon(echantillon.getTypeEchantillon());
            existingEchantillon.setNormeEchantillon(echantillon.getNormeEchantillon());
            existingEchantillon.setNomEchantillon(echantillon.getNomEchantillon());
            existingEchantillon.setLieuPrelevement(echantillon.getLieuPrelevement());
            existingEchantillon.setDateFinPrelevement(echantillon.getDateFinPrelevement());
            existingEchantillon.setHeureFinPrelevement(echantillon.getHeureFinPrelevement());
            existingEchantillon.setPriorite(echantillon.getPriorite());
            existingEchantillon.setCommentairesInternes(echantillon.getCommentairesInternes());
            existingEchantillon.setDemande(echantillon.getDemande());
            return echantillonRepository.save(existingEchantillon);
        } else {
            throw new EchantillonNotFoundException("Echantillon with ID " + id + " not found");
        }
    }
    @Override
    public List<Echantillon> findAllByDemandeId(Long demandeId) {
        return echantillonRepository.findAllByDemandeId(demandeId);
    }
    
}
