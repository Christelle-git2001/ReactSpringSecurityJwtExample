package com.lacouf.rsbjwt.model.Evaluation;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Embeddable
public class EvaluationProductivite {
    private Long planificationScore;
    private Long comprehensionScore;
    private Long rythmeScore;
    private Long prioriteScore;
    private Long echeancierScore;
    private String commentaire;

    public EvaluationProductivite() {

    }

    public EvaluationProductivite(Long planificationScore, Long comprehensionScore, Long rythmeScore, Long prioriteScore, Long echeancierScore, String commentaire) {
        this.planificationScore = planificationScore;
        this.comprehensionScore = comprehensionScore;
        this.rythmeScore = rythmeScore;
        this.prioriteScore = prioriteScore;
        this.echeancierScore = echeancierScore;
        this.commentaire = commentaire;
    }
}
