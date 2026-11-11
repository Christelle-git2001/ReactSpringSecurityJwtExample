package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.EvaluationStagiaire;
import com.lacouf.rsbjwt.service.dto.EvaluationStagiaire.*;

public record EvaluationStagiaireDTO(
        long candidatureID,
        SuperviseurStageDTO supervisueurStageDTO,
        EvaluationProductiviteDTO evaluationProductiviteDTO,
        QualiteTravailDTO qualiteTravailDTO,
        QualiteSocialeDTO qualiteSocialDTO,
        HabiletePersonelleDTO habiletePersonelleDTO,
        AppreciationGlobaleDTO appreciationGlobalDTO) {

    public static EvaluationStagiaire toEntity(Candidature candidature,EvaluationStagiaireDTO evaluationStagiaireDTO){
        return EvaluationStagiaire.builder()
                .candidature(candidature)
                .superviseur(SuperviseurStageDTO.toEntity(evaluationStagiaireDTO.supervisueurStageDTO()))
                .evaluationProductivite(EvaluationProductiviteDTO.toEntity(evaluationStagiaireDTO.evaluationProductiviteDTO()))
                .qualiteTravail(QualiteTravailDTO.toEntity(evaluationStagiaireDTO.qualiteTravailDTO()))
                .qualiteSociale(QualiteSocialeDTO.toEntity(evaluationStagiaireDTO.qualiteSocialDTO()))
                .habiletePersonelle(HabiletePersonelleDTO.toEntity(evaluationStagiaireDTO.habiletePersonelleDTO()))
                .appreciationGlobal(AppreciationGlobaleDTO.toEntity(evaluationStagiaireDTO.appreciationGlobalDTO()))
                .build();
    }

    public static EvaluationStagiaireDTO toDto(EvaluationStagiaire entity) {
        if (entity == null) {
            return null;
        }
        return new EvaluationStagiaireDTO(
                entity.getCandidatureId(),
                SuperviseurStageDTO.toDto(entity.getSuperviseur()),
                EvaluationProductiviteDTO.toDto(entity.getEvaluationProductivite()),
                QualiteTravailDTO.toDto(entity.getQualiteTravail()),
                QualiteSocialeDTO.toDto(entity.getQualiteSociale()),
                HabiletePersonelleDTO.toDto(entity.getHabiletePersonelle()),
                AppreciationGlobaleDTO.toDto(entity.getAppreciationGlobal())
        );
    }

}
