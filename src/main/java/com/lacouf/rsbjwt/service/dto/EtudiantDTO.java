package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.auth.Role;
import lombok.Builder;

public record EtudiantDTO(long id, String firstName, String lastName, String email, String matricule, String phoneNumber) {

    public static EtudiantDTO of(Etudiant etudiant) {
        return new EtudiantDTO(etudiant.getId(), etudiant.getFirstName(), etudiant.getLastName(),
                etudiant.getEmail(), etudiant.getMatricule(), etudiant.getPhoneNumber());
    }
}
