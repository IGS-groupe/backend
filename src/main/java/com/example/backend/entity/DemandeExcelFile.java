package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "demande_excel_files")
public class DemandeExcelFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Original filename from client
    @Column(nullable = false)
    private String originalName;

    // Final saved filename (unique)
    @Column(nullable = false)
    private String storedName;

    // Relative path (e.g. "/uploads/demandes/12/excel/20250902_191210_file.xlsx")
    @Column(nullable = false, length = 500)
    private String storedPath;

    // MIME type (e.g. application/vnd.openxmlformats-officedocument.spreadsheetml.sheet)
    @Column(nullable = false, length = 150)
    private String contentType;

    // Size in bytes
    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    // Optional: who uploaded (if you have a User entity)
    @Column
    private Long uploadedByUserId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "demande_id", referencedColumnName = "DemandeID", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore   // <-- add this
    private Demande demande;
}
