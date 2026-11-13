package com.lacouf.rsbjwt.service.dto.EvaluationStagiaire;

import com.lacouf.rsbjwt.model.Evaluation.EvaluationProductivite;
import lombok.Builder;

@Builder
public record EvaluationProductiviteDTO(
        Long planificationScore,
        Long comprehensionScore,
        Long rythmeScore,
        Long prioriteScore,
        Long echeancierScore,
        String commentaire
) {

    public static EvaluationProductivite toEntity(EvaluationProductiviteDTO dto) {
        if (dto == null) {
            return null;
        }
        return EvaluationProductivite.builder()
                .planificationScore(dto.planificationScore())
                .comprehensionScore(dto.comprehensionScore())
                .rythmeScore(dto.rythmeScore())
                .prioriteScore(dto.prioriteScore())
                .echeancierScore(dto.echeancierScore())
                .commentaire(dto.commentaire)
                .build();
    }

    public static EvaluationProductiviteDTO toDto(EvaluationProductivite entity) {
        if (entity == null) {
            return null;
        }
        return new EvaluationProductiviteDTO(
                entity.getPlanificationScore(),
                entity.getComprehensionScore(),
                entity.getRythmeScore(),
                entity.getPrioriteScore(),
                entity.getEcheancierScore(),
                entity.getCommentaire()
        );
    }
}