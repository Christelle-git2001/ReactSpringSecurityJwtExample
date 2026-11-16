package com.lacouf.rsbjwt.service.dto.EvaluationStagiaire;

import com.lacouf.rsbjwt.model.Enum.ChoixAccueil;
import com.lacouf.rsbjwt.model.Evaluation.AppreciationGlobal;
import lombok.Builder;

@Builder
public record AppreciationGlobaleDTO(
        Long attenteEmployeurScore,
        String descriptionAppreciation,
        Boolean discussionAvecStagiaire,
        double supervisionTime,
        ChoixAccueil renewExperience,
        String experienceSuffisante
) {

    public static AppreciationGlobal toEntity(AppreciationGlobaleDTO dto) {
        if (dto == null) return null;
        return AppreciationGlobal.builder()
                .attenteEmployeurScore(dto.attenteEmployeurScore())
                .descriptionAppreciation(dto.descriptionAppreciation())
                .discussionAvecStagiaire(dto.discussionAvecStagiaire())
                .supervisionTime(dto.supervisionTime())
                .renewExperience(dto.renewExperience())
                .experienceSuffisante(dto.experienceSuffisante())
                .build();
    }

    public static AppreciationGlobaleDTO toDto(AppreciationGlobal entity) {
        if (entity == null) return null;
        return new AppreciationGlobaleDTO(
                entity.getAttenteEmployeurScore(),
                entity.getDescriptionAppreciation(),
                entity.getDiscussionAvecStagiaire(),
                entity.getSupervisionTime(),
                entity.getRenewExperience(),
                entity.getExperienceSuffisante()
        );
    }
}