package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Enum.Departement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreationOffreDeStageDTO(

        @NotBlank(message = "validation.title.required")
        String title,

        @NotBlank(message = "validation.description.required")
        String description,

        @NotNull(message = "validation.salary.required")
        Double salary,

        @NotNull(message = "validation.domain.required")
        Departement domain,

        @NotNull(message = "validation.startDate.required")
        LocalDate startDate,

        @NotNull(message = "validation.endDate.required")
        LocalDate endDate
) {}
