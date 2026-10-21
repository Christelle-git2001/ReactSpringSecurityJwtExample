package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.OffreCiblee;
import com.lacouf.rsbjwt.model.OffreDeStage;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OffreDeStageDTO(
        long id,
        String title,
        String description,
        Double salary,
        Departement domain,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate displayEndDate,
        Statut statut,
        String fileName,
        EmployeurDTO employeur,
        EtudiantDTO etudiant,
        String rejectionComment,
        LocalDateTime createdAt,
        Boolean isTarget
) {

    public static OffreDeStageDTO of(OffreDeStage offre) {
        EtudiantDTO etudiantDTO = null;

        if (offre instanceof OffreCiblee offreCiblee && offreCiblee.getEtudiant() != null) {
            etudiantDTO = EtudiantDTO.of(offreCiblee.getEtudiant());
        }

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
                offre.getEmployeur() != null ? EmployeurDTO.of(offre.getEmployeur()) : null,
                etudiantDTO,
                offre.getRejectionComment(),
                offre.getCreatedAt(),
                offre instanceof OffreCiblee
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
                null,
                null,
                null,
                false
        );
    }
}