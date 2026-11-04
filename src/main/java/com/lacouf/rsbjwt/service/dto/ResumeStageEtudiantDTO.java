package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.OffreDeStage;

import java.time.LocalDate;

public record ResumeStageEtudiantDTO(
        long idOffre,
        String title,
        String descriptionOffre,
        Double salary,
        LocalDate startDate,
        LocalDate endDate,
        String fileName,
        EmployeurDTO employeurDTO
) {
    public static ResumeStageEtudiantDTO of(OffreDeStage offreDeStage){
        return new ResumeStageEtudiantDTO(
                offreDeStage.getId(),
                offreDeStage.getTitle(),
                offreDeStage.getDescription(),
                offreDeStage.getSalary(),
                offreDeStage.getStartDate(),
                offreDeStage.getEndDate(),
                offreDeStage.getFileName(),
                EmployeurDTO.of(offreDeStage.getEmployeur()));
    }
}
