package com.lacouf.rsbjwt.service.dto.EvaluationStagiaire;

import lombok.Builder;

@Builder
public record QualiteTravailDTO(Long completeTaskScore, Long minutieScore, Long checkBoxScore, Long perfectionnementResearchScore, Long analystScore) {
}
