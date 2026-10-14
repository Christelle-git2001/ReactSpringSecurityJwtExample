package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Candidature;

import java.time.LocalDateTime;

public record CandidatureDTO(
        Long id,
        Long offreId,
        LocalDateTime dateCandidature) {

        public static CandidatureDTO of(Candidature candidature) {
            return new CandidatureDTO(
                    candidature.getId(),
                    candidature.getOffreDeStage().getId(),
                    candidature.getDateCandidature()
            );
        }
    }
