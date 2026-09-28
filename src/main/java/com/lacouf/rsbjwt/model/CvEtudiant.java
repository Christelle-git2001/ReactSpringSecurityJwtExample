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

    private String nomFichier;
    private String typeContenu;
    private Long tailleFichier;
    private String cheminStockage;
    private LocalDateTime dateTeleversement;

    @OneToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @Builder
    public CvEtudiant(String nomFichier, String typeContenu, String cheminStockage,
                      LocalDateTime dateTeleversement, Etudiant etudiant) {
        this.nomFichier = nomFichier;
        this.typeContenu = typeContenu;
        this.cheminStockage = cheminStockage;
        this.dateTeleversement = dateTeleversement;
        this.etudiant = etudiant;
    }
}
