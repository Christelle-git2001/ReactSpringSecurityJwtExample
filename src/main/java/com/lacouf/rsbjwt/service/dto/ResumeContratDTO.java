package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Contrat;

public record ResumeContratDTO(ContratDTO contrat, CandidatureDTO candidature) {
    public static ResumeContratDTO of(Contrat contrat) {
        return new ResumeContratDTO(ContratDTO.of(contrat),
                CandidatureDTO.of(contrat.getCandidature(), contrat));
    }
}