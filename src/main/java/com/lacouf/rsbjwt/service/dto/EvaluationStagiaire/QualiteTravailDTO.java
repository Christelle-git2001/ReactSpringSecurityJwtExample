package com.lacouf.rsbjwt.service.dto.EvaluationStagiaire;

import com.lacouf.rsbjwt.model.Evaluation.QualiteTravail;
import lombok.Builder;

@Builder
public record QualiteTravailDTO(
        Long completeTaskScore,
        Long minutieScore,
        Long checkBoxScore,
        Long perfectionnementResearchScore,
        Long analystScore
) {

    public static QualiteTravail toEntity(QualiteTravailDTO dto) {
        if (dto == null) return null;
        return QualiteTravail.builder()
                .completeTaskScore(dto.completeTaskScore())
                .minutieScore(dto.minutieScore())
                .checkBoxScore(dto.checkBoxScore())
                .perfectionnementResearchScore(dto.perfectionnementResearchScore())
                .analystScore(dto.analystScore())
                .build();
    }

    public static QualiteTravailDTO toDto(QualiteTravail entity) {
        if (entity == null) return null;
        return new QualiteTravailDTO(
                entity.getCompleteTaskScore(),
                entity.getMinutieScore(),
                entity.getCheckBoxScore(),
                entity.getPerfectionnementResearchScore(),
                entity.getAnalystScore()
        );
    }
}