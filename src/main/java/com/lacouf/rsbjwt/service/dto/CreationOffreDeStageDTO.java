package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Enum.StatutOffre;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class CreationOffreDeStageDTO {
    @NotBlank(message = "validation.titre.required") String titre;
    @NotBlank(message = "validation.poste.required") String poste;
    @NotBlank(message = "validation.salaire.required") String salaire;
    @NotBlank(message = "validation.description.required") String description;
    @NotBlank(message = "validation.dateDebut.required") LocalDate dateDebut;
    @NotBlank(message = "validation.dateFin.required") LocalDate dateFin;
}
