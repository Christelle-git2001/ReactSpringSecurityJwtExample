package com.lacouf.rsbjwt.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "offre_de_stage_id", nullable = false)
    private OffreDeStage offreDeStage;

    @Column(nullable = false)
    private LocalDateTime dateCandidature;

    public Candidature(Etudiant etudiant, OffreDeStage offreDeStage) {
        this.etudiant = etudiant;
        this.offreDeStage = offreDeStage;
        this.dateCandidature = LocalDateTime.now();
    }
}
