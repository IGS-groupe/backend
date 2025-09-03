package com.example.backend.controllers;


import com.example.backend.entity.DemandeExcelFile;
import com.example.backend.services.DemandeExcelService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/demandesExcel")
@RequiredArgsConstructor
public class DemandeExcelController {

    private final DemandeExcelService excelService;

    @PostMapping("/{demandeId}/excel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<DemandeExcelFile> uploadSingle(
            @PathVariable Long demandeId,
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) {
        Long uploaderId = resolveUserId(principal); // adapt to your auth
        DemandeExcelFile saved = excelService.uploadExcel(demandeId, file, uploaderId);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{demandeId}/excel/batch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<DemandeExcelFile>> uploadMany(
            @PathVariable Long demandeId,
            @RequestParam("files") List<MultipartFile> files,
            Principal principal
    ) {
        Long uploaderId = resolveUserId(principal);
        List<DemandeExcelFile> saved = files.stream()
                .map(f -> excelService.uploadExcel(demandeId, f, uploaderId))
                .toList();
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{demandeId}/excel")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<DemandeExcelFile>> list(@PathVariable Long demandeId) {
        return ResponseEntity.ok(excelService.listFiles(demandeId));
    }

    @DeleteMapping("/excel/{fileId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long fileId) {
        excelService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(Principal principal) {
        // Replace with your security logic; return null if you don't track uploader
        return null;
    }
}
