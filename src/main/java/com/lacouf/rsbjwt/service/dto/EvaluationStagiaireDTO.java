package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.service.dto.EvaluationStagiaire.*;

public record EvaluationStagiaireDTO(
        EtudiantDTO etudiantDTO,
        CandidatureDTO candidatureDTO,
        SuperviseurStageDTO supervisueurStageDTO,
        EvaluationProductiviteDTO evaluationProductiviteDTO,
        QualiteTravailDTO qualiteTravailDTO,
        QualiteSocialDTO qualiteSocialDTO,
        HabiletePersonelleDTO habiletePersonelleDTO,
        AppreciationGlobalDTO appreciationGlobalDTO) {
}
