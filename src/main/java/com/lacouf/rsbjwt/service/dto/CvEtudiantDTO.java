package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.CvEtudiant;

import java.time.LocalDateTime;

public record CvEtudiantDTO (
    long id,
    String nomFichier,
    String typeContenu,
    Long tailleFichier,
    LocalDateTime dateTeleversement
){
    public static CvEtudiantDTO of(CvEtudiant cv) {
        return new CvEtudiantDTO(
                cv.getId(),
                cv.getNomFichier(),
                cv.getTypeContenu(),
                cv.getTailleFichier(),
                cv.getDateTeleversement()
        );
    }
}