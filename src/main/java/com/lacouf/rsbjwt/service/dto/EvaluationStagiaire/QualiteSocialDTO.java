package com.lacouf.rsbjwt.service.dto.EvaluationStagiaire;

import lombok.Builder;

@Builder
public record QualiteSocialDTO(Long socialScore, Long teamWorkScore, Long adaptationScore, Long critiqueAcceptationScore, Long respectScore, Long listenScore) {
}
