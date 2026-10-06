package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Etudiant;

import java.time.LocalDateTime;

public record CvEtudiantDTO (
    long id,
    EtudiantDTO etudiant,
    String fileName,
    String contentType,
    Long fileSize,
    LocalDateTime uploadDate,
    Statut statut,
    String comment
){
    public static CvEtudiantDTO of(CvEtudiant cv) {
        return new CvEtudiantDTO(
                cv.getId(),
                EtudiantDTO.of(cv.getEtudiant()),
                cv.getFileName(),
                cv.getContentType(),
                cv.getFileSize(),
                cv.getUploadDate(),
                cv.getStatut(),
                cv.getComment()
        );
    }
}