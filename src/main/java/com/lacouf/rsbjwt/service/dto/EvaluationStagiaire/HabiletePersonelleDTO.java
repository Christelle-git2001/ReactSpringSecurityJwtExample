package com.lacouf.rsbjwt.service.dto.EvaluationStagiaire;

import lombok.Builder;

@Builder
public record HabiletePersonelleDTO(Long motivationScore, Long tellIdeaScore, Long initiativeScore, Long securityScore, Long responsabiliteScore, Long ponctuelScore) {
}
