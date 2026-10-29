package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.FormatEntrevue;

import java.time.LocalDateTime;

public record EntrevueDTO(
        long id,
        LocalDateTime dateCreation,
        FormatEntrevue format,
        LocalDateTime dateHeureEntrevue,
        int duree,
        long candidatureId,
        long employeurId,
        String etudiantFirstName,
        String etudiantLastName,
        String offreTitle
) {

        public static EntrevueDTO of(Entrevue entrevue) {
                if (entrevue == null) {
                        return null;
                }

                Candidature candidature = entrevue.getCandidature();
                Etudiant etudiant = candidature != null ? candidature.getEtudiant() : null;
                OffreDeStage offre = candidature != null ? candidature.getOffreDeStage() : null;
                Employeur employeur = offre.getEmployeur();

                return new EntrevueDTO(
                        entrevue.getId(),
                        entrevue.getDateCreation(),
                        entrevue.getFormat(),
                        entrevue.getDateHeureEntrevue(),
                        entrevue.getDuree(),
                        candidature != null ? candidature.getId() : 0L,
                        employeur != null ? employeur.getId() :0L,
                        etudiant != null ? etudiant.getFirstName() : null,
                        etudiant != null ? etudiant.getLastName() : null,
                        offre != null ? offre.getTitle() : null
                );
        }
}