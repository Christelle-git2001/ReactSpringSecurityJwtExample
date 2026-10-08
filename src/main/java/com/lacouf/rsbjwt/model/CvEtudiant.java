package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.Statut;
import jakarta.annotation.Nullable;
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
    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private String contentType;
    @Column(nullable = false)
    private Long fileSize;
    @Column(nullable = false)
    private String storagePath;
    @Column(nullable = false)
    private LocalDateTime uploadDate;
    @Enumerated
    @Column(nullable = false)
    private Statut statut;
    @Column
    private String rejectionComment;

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
        this.statut = Statut.EN_ATTENTE;
    }
}
