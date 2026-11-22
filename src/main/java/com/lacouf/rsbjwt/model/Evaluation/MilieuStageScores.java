package com.lacouf.rsbjwt.model.Evaluation;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilieuStageScores {
    private Long tacheConformeScore;
    private Long integrationScore;
    private Long tempsSupervisionSuffisantScore;

    @ElementCollection
    @CollectionTable(
            name = "evaluation_temps_supervision",
            joinColumns = @JoinColumn(name = "evaluation_milieu_stage_id")
    )
    @Column(name = "temps_supervision_valeur")
    private List<Long> tempsSupervision;

    private Long environmentScore;
    private Long climatScore;
    private Long transportScore;
    private Long salaireSuffisentScore;
    private Long communicationScore;
    private Long equipementScore;
    private Long volumeTravailScore;
    private String commentaire;
}