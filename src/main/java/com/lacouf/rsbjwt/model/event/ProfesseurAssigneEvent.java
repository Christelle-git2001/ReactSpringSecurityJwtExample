package com.lacouf.rsbjwt.model.event;

import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Professeur;

import java.util.List;

public record ProfesseurAssigneEvent(
        Professeur professeur,
        List<Etudiant> etudiants
) {
}