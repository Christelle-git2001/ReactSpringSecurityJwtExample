package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Enum.StatutOffre;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.OffreDeStage;

import java.time.LocalDate;

public record OffreDeStageDTO(
        long id,
        String title,
        String description,
        Double salary,
        Departement domain,
        LocalDate startDate,
        LocalDate endDate,
        StatutOffre statut,
        String fileName,
        String rejectionComment
) {

    public static OffreDeStageDTO of(OffreDeStage offre) {
        return new OffreDeStageDTO(
                offre.getId(),
                offre.getTitle(),
                offre.getDescription(),
                offre.getSalary(),
                offre.getDomain(),
                offre.getStartDate(),
                offre.getEndDate(),
                offre.getStatut(),
                offre.getFileName(),
                offre.getRejectionComment()
        );
    }

    public static OffreDeStageDTO empty() {
        return new OffreDeStageDTO(
                0L,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
