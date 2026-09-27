package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreationOffreDeStageDTO(
        @NotBlank(message = "validation.titre.required")
        String titre,

        @NotBlank(message = "validation.poste.required")
        String poste,

        @NotNull(message = "validation.salaire.required")
        Double salaire,

        @NotBlank(message = "validation.description.required")
        String description,

        @NotNull(message = "validation.dateDebut.required")
        LocalDate dateDebut,

        @NotNull(message = "validation.dateFin.required")
        LocalDate dateFin
) {
}
