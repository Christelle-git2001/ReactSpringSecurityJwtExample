package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.Contrat;
import com.lacouf.rsbjwt.model.Gestionnaire;

public record ContratDTO(
        long id,
        Long candidatureId,
        Long gestionnaireId
) {

    public static ContratDTO of(Contrat contrat){
        Candidature candidature = contrat.getCandidature();
        Gestionnaire gestionnaire = contrat.getGestionnaire();
        return new ContratDTO(
                contrat.getId(),
                candidature.getId(),
                gestionnaire.getId());
    }
}
