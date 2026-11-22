package com.lacouf.rsbjwt.service.dto.utilisateur;

public record MonProfesseurAssigneDTO(
        boolean professeurAssigne,
        ProfesseurDTO professeur
) {}