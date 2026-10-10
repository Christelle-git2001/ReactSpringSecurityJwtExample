package com.lacouf.rsbjwt.service.dto;
import com.lacouf.rsbjwt.model.Enum.Statut;
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
        LocalDate  displayEndDate,
        Statut statut,
        String fileName,
        EmployeurDTO employeur,
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
                offre.getDisplayEndDate(),
                offre.getStatut(),
                offre.getFileName(),
                EmployeurDTO.of(offre.getEmployeur()),
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
                null,
                null,
                null
        );
    }
}
