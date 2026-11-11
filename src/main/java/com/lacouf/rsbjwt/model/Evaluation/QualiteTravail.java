package com.lacouf.rsbjwt.model.Evaluation;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@Builder
public class QualiteTravail {
    private Long completeTaskScore;
    private Long minutieScore;
    private Long checkBoxScore;
    private Long perfectionnementResearchScore;
    private Long analystScore;

    public QualiteTravail(){}

    public QualiteTravail(Long completeTaskScore, Long minutieScore, Long checkBoxScore, Long perfectionnementResearchScore, Long analystScore){
        this.completeTaskScore = completeTaskScore;
        this.minutieScore = minutieScore;
        this.checkBoxScore = checkBoxScore;
        this.perfectionnementResearchScore = perfectionnementResearchScore;
        this.analystScore = analystScore;
    }
}
