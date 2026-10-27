package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;

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
        LocalDateTime dateCandidature,
        Long cvId,
        StatutCandidature statut
) {
    public static CandidatureDTO of(Candidature candidature) {
        if (candidature == null) {
            return null;
        }

        Etudiant etudiant = candidature.getEtudiant();
        OffreDeStage offre = candidature.getOffreDeStage();
        Long cvId = null;
        if (etudiant != null) {
            CvEtudiant cv = etudiant.getCv();
            if (cv != null) {
                cvId = cv.getId();
            }
        }

        return new CandidatureDTO(
                candidature.getId(),
                etudiant != null ? etudiant.getId() : 0L,
                etudiant != null ? etudiant.getFirstName() : null,
                etudiant != null ? etudiant.getLastName() : null,
                etudiant != null && etudiant.getDepartment() != null ? etudiant.getDepartment().name() : null,
                offre != null ? offre.getId() : null,
                offre != null ? offre.getTitle() : null,
                (offre != null && offre.getEmployeur() != null) ? offre.getEmployeur().getBusinessName() : null,
                candidature.getDateCandidature(),
                cvId,
                candidature.getStatut()
        );
    }
}