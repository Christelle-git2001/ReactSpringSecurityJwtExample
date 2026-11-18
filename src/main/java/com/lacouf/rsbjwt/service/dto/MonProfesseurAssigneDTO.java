package com.lacouf.rsbjwt.service.dto;

public record MonProfesseurAssigneDTO(
        boolean professeurAssigne,
        ProfesseurDTO professeur
) {}