package com.lacouf.rsbjwt.service.dto.EvaluationMillieuStage;

import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.Evaluation.SuperviseurInfo;
import com.lacouf.rsbjwt.model.EvaluationMillieuStage;
import com.lacouf.rsbjwt.service.dto.evaluation_stagiaire.SuperviseurStageDTO;


public record EvaluationMillieuStageDTO(
        Long candidatureId,
        SuperviseurStageDTO superviseurStageDTO,
        ObservationGeneralDTO obersvationGeneralDTO,
        MilieuStageScoresDTO millieuStageScoresDTO
) {

    public static EvaluationMillieuStage toEntity(EvaluationMillieuStageDTO dto, Candidature candidature) {
        if (dto == null) {
            return null;
        }

        SuperviseurInfo superviseurInfo = SuperviseurStageDTO.toEntity(dto.superviseurStageDTO);

        return EvaluationMillieuStage.builder()
                .candidature(candidature)
                .superviseurInfo(superviseurInfo)
                .observationGeneral(ObservationGeneralDTO.toEntity(dto.obersvationGeneralDTO()))
                .millieuStageScores(MilieuStageScoresDTO.toEntity(dto.millieuStageScoresDTO()))
                .build();
    }

    public static EvaluationMillieuStageDTO toDTO(EvaluationMillieuStage entity) {
        if (entity == null) {
            return null;
        }

        SuperviseurStageDTO superviseurStageDTO = SuperviseurStageDTO.toDto(entity.getSuperviseurInfo());

        return new EvaluationMillieuStageDTO(
                entity.getCandidature().getId(),
                superviseurStageDTO,
                ObservationGeneralDTO.toDTO(entity.getObservationGeneral()),
                MilieuStageScoresDTO.toDTO(entity.getMillieuStageScores())
        );
    }
}