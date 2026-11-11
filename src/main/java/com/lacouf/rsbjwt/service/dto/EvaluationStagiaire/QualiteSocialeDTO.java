package com.lacouf.rsbjwt.service.dto.EvaluationStagiaire;

import com.lacouf.rsbjwt.model.Evaluation.QualiteSocial;
import lombok.Builder;

@Builder
public record QualiteSocialeDTO(
        Long socialScore,
        Long teamWorkScore,
        Long adaptationScore,
        Long critiqueAcceptationScore,
        Long respectScore,
        Long listenScore
) {

    public static QualiteSocial toEntity(QualiteSocialeDTO dto) {
        if (dto == null) return null;
        return QualiteSocial.builder()
                .socialScore(dto.socialScore())
                .teamWorkScore(dto.teamWorkScore())
                .adaptationScore(dto.adaptationScore())
                .critiqueAcceptationScore(dto.critiqueAcceptationScore())
                .respectScore(dto.respectScore())
                .listenScore(dto.listenScore())
                .build();
    }

    public static QualiteSocialeDTO toDto(QualiteSocial entity) {
        if (entity == null) return null;
        return new QualiteSocialeDTO(
                entity.getSocialScore(),
                entity.getTeamWorkScore(),
                entity.getAdaptationScore(),
                entity.getCritiqueAcceptationScore(),
                entity.getRespectScore(),
                entity.getListenScore()
        );
    }
}