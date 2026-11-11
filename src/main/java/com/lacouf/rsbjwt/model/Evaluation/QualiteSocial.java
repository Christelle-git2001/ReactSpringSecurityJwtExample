package com.lacouf.rsbjwt.model.Evaluation;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Embeddable
public class QualiteSocial {
    private Long socialScore;
    private Long teamWorkScore;
    private Long adaptationScore;
    private Long critiqueAcceptationScore;
    private Long respectScore;
    private Long listenScore;

    public QualiteSocial(){}

    public QualiteSocial(Long socialScore, Long teamWorkScore, Long adaptationScore, Long critiqueAcceptationScore, Long respectScore, Long listenScore){
        this.socialScore = socialScore;
        this.teamWorkScore = teamWorkScore;
        this.adaptationScore = adaptationScore;
        this.critiqueAcceptationScore = critiqueAcceptationScore;
        this.respectScore = respectScore;
        this.listenScore = listenScore;
    }
}