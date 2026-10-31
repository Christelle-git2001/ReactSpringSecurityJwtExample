package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.Contrat;
import com.lacouf.rsbjwt.model.Gestionnaire;

import java.time.LocalDate;

public record ContratDTO(
        long id,
        Long candidatureId,
        Long gestionnaireId,
        LocalDate dateSignatureGestionnaire,
        LocalDate dateSignatureEmployeur,
        LocalDate dateSignatureEtudiant
) {

    public static ContratDTO of(Contrat contrat){
        Candidature candidature = contrat.getCandidature();
        Gestionnaire gestionnaire = contrat.getGestionnaire();
        return new ContratDTO(
                contrat.getId(),
                candidature.getId(),
                gestionnaire.getId(),
        contrat.getDateSignatureGestionnaire(),
        contrat.getDateSignatureEmployeur(),
        contrat.getDateSignatureEtudiant());
    }
}
