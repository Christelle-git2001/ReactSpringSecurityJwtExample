package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Enum.Departement;

import java.time.LocalDate;

public record CreationOffreCibleeDTO(
        String title,
        String description,
        Double salary,
        Departement domain,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate displayEndDate,
        Long employeurId,
        Long etudiantId
) {}

