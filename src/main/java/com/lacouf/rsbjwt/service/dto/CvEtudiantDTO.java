package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Enum.Statut;

import java.time.LocalDateTime;

public record CvEtudiantDTO (
    long id,
    String fileName,
    String contentType,
    Long fileSize,
    LocalDateTime uploadDate,
    Statut statut,
    String rejectionComment
){
    public static CvEtudiantDTO of(CvEtudiant cv) {
        return new CvEtudiantDTO(
                cv.getId(),
                cv.getFileName(),
                cv.getContentType(),
                cv.getFileSize(),
                cv.getUploadDate(),
                cv.getStatut(),
                cv.getRejectionComment()
        );
    }
}