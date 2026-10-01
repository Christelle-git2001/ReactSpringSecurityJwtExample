package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CvEtudiant {
    @Id
    @GeneratedValue
    private Long id;

    private String fileName;
    private String contentType;
    private Long fileSize;
    private String storagePath;
    private LocalDateTime uploadDate;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @Builder
    public CvEtudiant(String fileName, String contentType, Long fileSize,
                      String storagePath, LocalDateTime uploadDate, Etudiant etudiant) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.storagePath = storagePath;
        this.uploadDate = uploadDate;
        this.etudiant = etudiant;
    }
}
