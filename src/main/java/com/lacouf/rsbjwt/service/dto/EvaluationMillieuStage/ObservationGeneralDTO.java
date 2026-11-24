package com.lacouf.rsbjwt.service.dto.EvaluationMillieuStage;

import com.lacouf.rsbjwt.model.Evaluation.ObservationGeneral;
import com.lacouf.rsbjwt.model.Evaluation.QuartTravail;

import java.util.List;

public record ObservationGeneralDTO(
        Long noStagePrivilegier,
        Long nbStagiaireAccueil,
        Boolean engagerMemeStagiaire,
        List<QuartTravailDTO> quartsTravail
) {
    public static ObservationGeneral toEntity(ObservationGeneralDTO dto) {
        if (dto == null) {
            return null;
        }

        List<QuartTravail> quartsTravail = dto.quartsTravail() != null
                ? dto.quartsTravail().stream()
                .map(QuartTravailDTO::toEntity)
                .toList()
                : List.of();

        return new ObservationGeneral(
                dto.noStagePrivilegier(),
                dto.nbStagiaireAccueil(),
                dto.engagerMemeStagiaire(),
                quartsTravail
        );
    }

    public static ObservationGeneralDTO toDTO(ObservationGeneral entity) {
        if (entity == null) {
            return null;
        }

        List<QuartTravailDTO> quartsTravailDTOs = entity.getQuartsTravail() != null
                ? entity.getQuartsTravail().stream()
                .map(QuartTravailDTO::toDTO)
                .toList()
                : List.of();

        return new ObservationGeneralDTO(
                entity.getNoStagePrivilegier(),
                entity.getNbStagiaireAccueil(),
                entity.getEngagerMemeStagiaire(),
                quartsTravailDTOs
        );
    }
}