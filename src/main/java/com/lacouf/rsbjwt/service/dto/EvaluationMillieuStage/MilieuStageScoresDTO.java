package com.lacouf.rsbjwt.service.dto.EvaluationMillieuStage;

import com.lacouf.rsbjwt.model.Evaluation.MilieuStageScores;

import java.util.List;

public record MilieuStageScoresDTO(
        Long tacheConformeScore,
        Long integrationScore,
        Long tempsSupervisionSuffisantScore,
        List<Long> tempsSupervision,
        Long environmentScore,
        Long climatScore,
        Long salaireSuffisentScore,
        Long communicationScore,
        Long equipementScore,
        Long volumeTravailScore,
        String commentaire
) {
    public static MilieuStageScores toEntity(MilieuStageScoresDTO dto) {
        if (dto == null) {
            return null;
        }

        return MilieuStageScores.builder()
                .tacheConformeScore(dto.tacheConformeScore())
                .integrationScore(dto.integrationScore())
                .tempsSupervisionSuffisantScore(dto.tempsSupervisionSuffisantScore())
                .tempsSupervision(dto.tempsSupervision() != null ? List.copyOf(dto.tempsSupervision()) : List.of())
                .environmentScore(dto.environmentScore())
                .climatScore(dto.climatScore())
                .salaireSuffisentScore(dto.salaireSuffisentScore())
                .communicationScore(dto.communicationScore())
                .equipementScore(dto.equipementScore())
                .volumeTravailScore(dto.volumeTravailScore())
                .commentaire(dto.commentaire())
                .build();
    }

    public static MilieuStageScoresDTO toDTO(MilieuStageScores entity) {
        if (entity == null) {
            return null;
        }

        return new MilieuStageScoresDTO(
                entity.getTacheConformeScore(),
                entity.getIntegrationScore(),
                entity.getTempsSupervisionSuffisantScore(),
                entity.getTempsSupervision() != null ? List.copyOf(entity.getTempsSupervision()) : List.of(),
                entity.getEnvironmentScore(),
                entity.getClimatScore(),
                entity.getSalaireSuffisentScore(),
                entity.getCommunicationScore(),
                entity.getEquipementScore(),
                entity.getVolumeTravailScore(),
                entity.getCommentaire()
        );
    }
}