package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Enum.StatutOffre;
import com.lacouf.rsbjwt.model.OffreDeStage;

import java.time.LocalDate;

public record OffreDeStageDTO(
        Long id,
        String titre,
        String poste,
        StatutOffre statut,
        Double salaire,
        String description,
        LocalDate dateDebut,
        LocalDate dateFin,
        String cheminFichier,
        String messageRefus
) {
    public static OffreDeStageDTO of(OffreDeStage offreDeStage) {
        return new OffreDeStageDTO(offreDeStage.getId()
        ,offreDeStage.getTitle(),
        offreDeStage.getPoste(),
        offreDeStage.getStatut(),
        offreDeStage.getSalary(),
        offreDeStage.getDescription(),
        offreDeStage.getFirstDate(),
        offreDeStage.getLastDate(),
        offreDeStage.getFilePath(),
        offreDeStage.getMessageRefus());
    }

    public static OffreDeStageDTO empty(){
        return new OffreDeStageDTO(0L,null,null,null,null,null,
                null,null,null,null);
    }

}
