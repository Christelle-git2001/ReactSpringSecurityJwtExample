package com.lacouf.rsbjwt.model.Evaluation;

import com.lacouf.rsbjwt.model.Enum.ChoixAccueil;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@Builder
public class AppreciationGlobal {

    private Long attenteEmployeurScore;
    private String descriptionAppreciation;
    private Boolean discussionAvecStagiaire;
    private double supervisionTime;
    private ChoixAccueil renewExperience;
    private String experienceSuffisante;

    public AppreciationGlobal(){}

    public AppreciationGlobal(Long attenteEmployeurScore, String descriptionAppreciation, Boolean discussionAvecStagiaire, double supervisionTime, ChoixAccueil renewExperience, String experienceSuffisante){
        this.attenteEmployeurScore = attenteEmployeurScore;
        this.descriptionAppreciation = descriptionAppreciation;
        this.discussionAvecStagiaire= discussionAvecStagiaire;
        this.supervisionTime = supervisionTime;
        this.renewExperience = renewExperience;
        this.experienceSuffisante = experienceSuffisante;
    }
}