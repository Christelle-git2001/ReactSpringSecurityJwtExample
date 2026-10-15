package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Candidature;

import java.time.LocalDateTime;

public record CandidatureDTO(
        long id,
        long etudiantId,
        Long offreId,
        LocalDateTime dateCandidature) {

        public static CandidatureDTO of(Candidature candidature) {
            return new CandidatureDTO(
                    candidature.getId(),
                    candidature.getEtudiant().getId(),
                    candidature.getOffreDeStage().getId(),
                    candidature.getDateCandidature()
            );
        }
    }
