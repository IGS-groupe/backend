package com.example.backend.services;

import org.springframework.stereotype.Service;
import com.example.backend.entity.Echantillon;
import com.example.backend.entity.Parameter;
import com.example.backend.entity.Priorite;
import com.example.backend.exception.EchantillonNotFoundException;
import com.example.backend.repository.EchantillonRepository;
import com.example.backend.repository.ParameterRepository;
import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class EchantillonServiceImpl implements EchantillonService {

    private final EchantillonRepository echantillonRepository;
    private final ParameterRepository parameterRepository;
    
    @Override
    public Echantillon saveEchantillon(Echantillon echantillon) {
        if (echantillon.getParameter() != null && !echantillon.getParameter().isEmpty()) {
            Set<Parameter> managedParameters = new HashSet<>();
            for (Parameter param : echantillon.getParameter()) {
                Optional<Parameter> existingParam = parameterRepository.findById(param.getParameterId());
                managedParameters.add(existingParam.orElse(param));
            }
            echantillon.setParameter(managedParameters);
        }
        return echantillonRepository.save(echantillon);
    }
    
    @Override
    public Set<Parameter> getParametersByEchantillonId(Long echantillonId) {
        return echantillonRepository.findById(echantillonId)
                .map(Echantillon::getParameter)
                .orElse(Collections.emptySet());
    }
    @Override
    public List<Echantillon> getAllEchantillons() {
        return echantillonRepository.findAll();
    }

    @Override
    public Echantillon getEchantillonById(Long id) {
        return echantillonRepository.findById(id).orElseThrow(() -> new EchantillonNotFoundException("Echantillon with ID " + id + " not found"));
    }

    @Override
    public void deleteEchantillon(Long id) {
        if (!echantillonRepository.existsById(id)) {
            throw new EchantillonNotFoundException("Echantillon with ID " + id + " not found");
        }
        echantillonRepository.deleteById(id);
    }

    @Override
    public List<Echantillon> getEchantillonsByPriorite(Priorite priorite) {
        return echantillonRepository.findByPriorite(priorite);
    }

    @Override
    public Echantillon updateEchantillon(Long id, Echantillon echantillon) {
        Optional<Echantillon> optionalEchantillon = echantillonRepository.findById(id);
        if (optionalEchantillon.isPresent()) {
            Echantillon existingEchantillon = optionalEchantillon.get();
            existingEchantillon.setTypeEchantillon(echantillon.getTypeEchantillon());
            existingEchantillon.setNomEchantillon(echantillon.getNomEchantillon());
            existingEchantillon.setLieuPrelevement(echantillon.getLieuPrelevement());
            existingEchantillon.setAddressRetourner((echantillon.getAddressRetourner()));
            existingEchantillon.setDateFinPrelevement(echantillon.getDateFinPrelevement());
            existingEchantillon.setHeureFinPrelevement(echantillon.getHeureFinPrelevement());
            existingEchantillon.setPriorite(echantillon.getPriorite());
            existingEchantillon.setCommentairesInternes(echantillon.getCommentairesInternes());
            existingEchantillon.setDemande(echantillon.getDemande());
            existingEchantillon.setReturns(echantillon.getReturns());
            existingEchantillon.setDisposes(echantillon.getDisposes());     
            if (echantillon.getParameter() != null) {
                existingEchantillon.setParameter(new HashSet<>(echantillon.getParameter()));
            }
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
