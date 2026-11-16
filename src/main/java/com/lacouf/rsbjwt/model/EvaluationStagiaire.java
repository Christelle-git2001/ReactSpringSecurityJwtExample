package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Evaluation.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationStagiaire {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "candidature_id", nullable = false, unique = true)
    private Candidature candidature;

    @Embedded
    private SuperviseurInfo superviseur;

    @Embedded
    @AttributeOverride(name = "commentaire", column = @Column(name = "commentaire_productivite"))
    private EvaluationProductivite evaluationProductivite;

    @Embedded
    @AttributeOverride(name = "commentaire", column = @Column(name = "commentaire_qualite_travail"))
    private QualiteTravail qualiteTravail;

    @Embedded
    @AttributeOverride(name = "commentaire", column = @Column(name = "commentaire_qualite_sociale"))
    private QualiteSocial qualiteSociale;

    @Embedded
    @AttributeOverride(name = "commentaire", column = @Column(name = "commentaire_habilete_personnelle"))
    private HabiletePersonelle habiletePersonelle;

    @Embedded
    @AttributeOverride(name = "commentaire", column = @Column(name = "commentaire_appreciation_globale"))
    private AppreciationGlobal appreciationGlobal;

    public Long getCandidatureId() {
        return candidature.getId();
    }
}