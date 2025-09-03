package com.example.backend.services;


import com.example.backend.config.FileUploadUtil;
import com.example.backend.entity.Demande;
import com.example.backend.entity.DemandeExcelFile;
import com.example.backend.repository.DemandeExcelFileRepository;
import com.example.backend.repository.DemandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandeExcelService {

    private final DemandeRepository demandeRepository;
    private final DemandeExcelFileRepository fileRepository;

    public DemandeExcelFile uploadExcel(Long demandeId, MultipartFile file, Long uploaderUserId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Empty file.");
        }
        String ct = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        boolean isExcel =
                ct.contains("spreadsheetml") || ct.contains("ms-excel") ||
                file.getOriginalFilename() != null &&
                    (file.getOriginalFilename().toLowerCase().endsWith(".xlsx") ||
                     file.getOriginalFilename().toLowerCase().endsWith(".xls") ||
                     file.getOriginalFilename().toLowerCase().endsWith(".csv"));
        if (!isExcel) {
            throw new IllegalArgumentException("Only Excel/CSV files are allowed.");
        }

        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande not found: " + demandeId));

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String original = file.getOriginalFilename() == null ? "file.xlsx" : file.getOriginalFilename();
        String storedName = ts + "_" + original.replaceAll("\\s+", "_");
        String dir = "uploads/demandes/" + demandeId + "/excel";
        String savedPathAbs = FileUploadUtil.saveFile(dir, storedName, file);

        String relPath = "/" + dir + "/" + storedName;

        DemandeExcelFile entity = DemandeExcelFile.builder()
                .demande(demande)
                .originalName(original)
                .storedName(storedName)
                .storedPath(relPath)
                .contentType(ct.isEmpty() ? "application/octet-stream" : ct)
                .size(file.getSize())
                .uploadedAt(LocalDateTime.now())
                .uploadedByUserId(uploaderUserId)
                .build();

        return fileRepository.save(entity);
    }

    public List<DemandeExcelFile> listFiles(Long demandeId) {
        return fileRepository.findByDemande_DemandeId(demandeId);
    }

    public void deleteFile(Long fileId) {
        fileRepository.deleteById(fileId);
    }
}
