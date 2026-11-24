package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Evaluation.MilieuStageScores;
import com.lacouf.rsbjwt.model.Evaluation.ObservationGeneral;
import com.lacouf.rsbjwt.model.Evaluation.SuperviseurInfo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationMillieuStage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "candidature_id", nullable = false, unique = true)
    private Candidature candidature;

    @Embedded
    private SuperviseurInfo superviseurInfo;

    @Embedded
    private ObservationGeneral observationGeneral;

    @Embedded
    private MilieuStageScores millieuStageScores;

}
