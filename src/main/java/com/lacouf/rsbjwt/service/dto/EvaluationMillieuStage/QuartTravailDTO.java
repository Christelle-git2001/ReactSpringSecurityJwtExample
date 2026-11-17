package com.lacouf.rsbjwt.service.dto.EvaluationMillieuStage;

import com.lacouf.rsbjwt.model.Evaluation.QuartTravail;

public record QuartTravailDTO(
        String heureDebut,
        String heureFin
) {
    public static QuartTravail toEntity(QuartTravailDTO dto) {
        if (dto == null) {
            return null;
        }

        return new QuartTravail(
                dto.heureDebut(),
                dto.heureFin()
        );
    }

    public static QuartTravailDTO toDTO(QuartTravail entity) {
        if (entity == null) {
            return null;
        }

        return new QuartTravailDTO(
                entity.getHeureDebut(),
                entity.getHeureFin()
        );
    }
}
