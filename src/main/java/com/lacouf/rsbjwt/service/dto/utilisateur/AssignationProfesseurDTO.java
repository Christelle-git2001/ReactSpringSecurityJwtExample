package com.lacouf.rsbjwt.service.dto.utilisateur;

import java.util.List;

public record AssignationProfesseurDTO(
        List<Long> etudiantIds
) {}