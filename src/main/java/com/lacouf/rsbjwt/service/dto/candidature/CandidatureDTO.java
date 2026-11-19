package com.lacouf.rsbjwt.service.dto.candidature;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.model.Enum.StatutEtudiant;

import com.fasterxml.jackson.annotation.JsonInclude;

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
        StatutCandidature statut,
        String motifRefus,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        StatutEtudiant statutEtudiant,
        boolean contratSigne,
        boolean actionsDesactivees
) {
   public static StatutCandidature statutPourEmployeur(Candidature candidature) {
        if (candidature == null) return null;
        StatutCandidature statut = candidature.getStatut();
        if ("stage.refused_by_student".equals(candidature.getMotifRefus())
                || (candidature.actionsDesactivees() && statut != StatutCandidature.REFUSEE
                    && statut != StatutCandidature.ENTREVUE_REFUSEE)) {
            return StatutCandidature.OFFRE_DECLINEE;
        }
        return statut == StatutCandidature.STAGE_TROUVE ? StatutCandidature.ACCEPTEE : statut;
   }

   public static CandidatureDTO ofEmployeur(Candidature candidature) {
        CandidatureDTO dto = of(candidature);
        if (dto == null) return null;
        StatutCandidature statutEmployeur = statutPourEmployeur(candidature);
        return new CandidatureDTO(dto.id(), dto.etudiantId(), dto.etudiantFirstName(), dto.etudiantLastName(),
                dto.etudiantDepartment(), dto.offreId(), dto.offreTitle(), dto.employeurBusinessName(),
                dto.dateCandidature(), dto.cvId(), statutEmployeur, dto.motifRefus(), null,
                dto.contratSigne(), dto.actionsDesactivees() || statutEmployeur == StatutCandidature.OFFRE_DECLINEE);
   }

   public static CandidatureDTO of(Candidature candidature) {
        return of(candidature, null);
   }

   public static CandidatureDTO of(Candidature candidature, Contrat contrat) {
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
                candidature.getStatut(),
                candidature.getMotifRefus(),
                etudiant != null ? etudiant.getStatut() : null,
                contrat != null && contrat.getDateSignatureEtudiant() != null
                        && contrat.getDateSignatureEmployeur() != null
                        && contrat.getDateSignatureGestionnaire() != null,
                candidature.actionsDesactivees()
        );
    }
}