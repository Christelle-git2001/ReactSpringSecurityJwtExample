package com.lacouf.rsbjwt.service.dto.EvaluationStagiaire;

import lombok.Builder;

@Builder
public record AppreciationGlobalDTO(Long attenteEmployeurScore, String descriptionAppreciation, Boolean discussionAvecStagiaire, double supervisionTime, Boolean renewExperience, String experienceSuffisante ) {
    //Let renewExperience undefined/null pour peut-être
}
