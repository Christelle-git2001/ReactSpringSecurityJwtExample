package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Candidature;

import java.time.LocalDateTime;

public record CandidatureDTO(
        long id,
        long etudiantId,
        String etudiantFirstName,
        String etudiantLastName,
        String etudiantDepartment,
        Long offreId,
        String offreTitle,
        String employeurBusinessName,
        LocalDateTime dateCandidature
) {
    public static CandidatureDTO of(Candidature candidature) {
        return new CandidatureDTO(
                candidature.getId(),
                candidature.getEtudiant().getId(),
                candidature.getEtudiant().getFirstName(),
                candidature.getEtudiant().getLastName(),
                candidature.getEtudiant().getDepartment().name(),
                candidature.getOffreDeStage().getId(),
                candidature.getOffreDeStage().getTitle(),
                candidature.getOffreDeStage().getEmployeur().getBusinessName(),
                candidature.getDateCandidature()
        );
    }
}
