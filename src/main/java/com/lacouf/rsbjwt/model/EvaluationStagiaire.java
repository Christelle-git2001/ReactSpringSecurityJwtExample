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
    private EvaluationProductivite evaluationProductivite;

    @Embedded
    private QualiteTravail qualiteTravail;

    @Embedded
    private QualiteSocial qualiteSociale;

    @Embedded
    private HabiletePersonelle habiletePersonelle;

    @Embedded
    private AppreciationGlobal appreciationGlobal;

    public Long getCandidatureId() {
        return candidature.getId();
    }
}