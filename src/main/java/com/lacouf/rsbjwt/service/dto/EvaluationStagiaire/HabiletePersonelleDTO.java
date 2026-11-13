package com.lacouf.rsbjwt.service.dto.EvaluationStagiaire;

import com.lacouf.rsbjwt.model.Evaluation.HabiletePersonelle;
import lombok.Builder;

@Builder
public record HabiletePersonelleDTO(
        Long motivationScore,
        Long tellIdeaScore,
        Long initiativeScore,
        Long securityScore,
        Long responsabiliteScore,
        Long ponctuelScore,
        String commentaire
) {

    public static HabiletePersonelle toEntity(HabiletePersonelleDTO dto) {
        if (dto == null) return null;
        return HabiletePersonelle.builder()
                .motivationScore(dto.motivationScore())
                .tellIdeaScore(dto.tellIdeaScore())
                .initiativeScore(dto.initiativeScore())
                .securityScore(dto.securityScore())
                .responsabiliteScore(dto.responsabiliteScore())
                .ponctuelScore(dto.ponctuelScore())
                .commentaire(dto.commentaire)
                .build();
    }

    public static HabiletePersonelleDTO toDto(HabiletePersonelle entity) {
        if (entity == null) return null;
        return new HabiletePersonelleDTO(
                entity.getMotivationScore(),
                entity.getTellIdeaScore(),
                entity.getInitiativeScore(),
                entity.getSecurityScore(),
                entity.getResponsabiliteScore(),
                entity.getPonctuelScore(),
                entity.getCommentaire()
        );
    }
}