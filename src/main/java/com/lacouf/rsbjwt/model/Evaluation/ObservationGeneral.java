package com.lacouf.rsbjwt.model.Evaluation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObservationGeneral {
    private Long noStagePrivilegier;
    private Long nbStagiaireAccueil;
    private Boolean engagerMemeStagiaire;
    @ElementCollection
    @CollectionTable(
            name = "evaluation_quarts_travail",
            joinColumns = @JoinColumn(name = "evaluation_milieu_stage_id")
    )
    private List<QuartTravail> quartsTravail;
}
