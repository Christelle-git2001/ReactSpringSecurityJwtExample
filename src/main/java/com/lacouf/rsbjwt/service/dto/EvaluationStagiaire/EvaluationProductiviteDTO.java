package com.lacouf.rsbjwt.service.dto.EvaluationStagiaire;

import lombok.Builder;

@Builder
public record EvaluationProductiviteDTO(Long planificationScore,Long comprehensionScore, Long rythmeScore, Long prioriteScore, Long echeancierScore ) {
}
