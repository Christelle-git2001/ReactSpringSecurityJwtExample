package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "candidature_id", nullable = false, unique = true)
    private Candidature candidature;

    @ManyToOne(optional = false)
    @JoinColumn(name = "gestionnaire_id", nullable = false)
    private Gestionnaire gestionnaire;

    private LocalDateTime dateSignatureEmployeur;
    private LocalDateTime dateSignatureEtudiant;
    private LocalDateTime dateSignatureGestionnaire;

    @Builder
    public Contrat(Candidature candidature, Gestionnaire gestionnaire) {
        this.candidature = candidature;
        this.gestionnaire = gestionnaire;
    }
}
