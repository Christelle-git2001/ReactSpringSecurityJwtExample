package com.lacouf.rsbjwt.model.Evaluation;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@Builder
public class HabiletePersonelle {

    private Long motivationScore;
    private Long tellIdeaScore;
    private Long initiativeScore;
    private Long securityScore;
    private Long responsabiliteScore;
    private Long ponctuelScore;

    public HabiletePersonelle(){}

    public HabiletePersonelle(Long motivationScore, Long tellIdeaScore, Long initiativeScore, Long securityScore, Long responsabiliteScore, Long ponctuelScore){
        this.motivationScore = motivationScore;
        this.tellIdeaScore = tellIdeaScore;
        this.initiativeScore = initiativeScore;
        this.securityScore = securityScore;
        this.responsabiliteScore = responsabiliteScore;
        this.ponctuelScore = ponctuelScore;
    }
}