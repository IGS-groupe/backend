package com.example.backend.repository;

import com.example.backend.entity.DemandeExcelFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandeExcelFileRepository extends JpaRepository<DemandeExcelFile, Long> {
    List<DemandeExcelFile> findByDemande_DemandeId(Long demandeId);
}
