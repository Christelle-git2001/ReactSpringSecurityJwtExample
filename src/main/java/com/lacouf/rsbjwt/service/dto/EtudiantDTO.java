package com.lacouf.rsbjwt.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lacouf.rsbjwt.model.Etudiant;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EtudiantDTO(long id, String firstName, String lastName, String email, String matricule,
                          String phoneNumber, String department, LocalDateTime previousLoginAt, Long professeurId,
                          String professeurNom)
        implements UserDTO{
    public static EtudiantDTO of(Etudiant etudiant) {
        return new EtudiantDTO(
                etudiant.getId(),
                etudiant.getFirstName(),
                etudiant.getLastName(),
                etudiant.getEmail(),
                etudiant.getMatricule(),
                etudiant.getPhoneNumber(),
                etudiant.getDepartment().getLabel(),
                etudiant.getPreviousLoginAt(),
        etudiant.getProfesseur() != null
                ? etudiant.getProfesseur().getId()
                : null,
                etudiant.getProfesseur() != null
                        ? etudiant.getProfesseur().getFirstName()
                        + " " + etudiant.getProfesseur().getLastName()
                        : null

        );
    }

    public static EtudiantDTO empty() {
        return new EtudiantDTO(0L, null, null, null,
                null, null, null, null, null, null);
    }

    @Override
    @JsonProperty("role")
    public RoleDTO role() {
        return RoleDTO.ETUDIANT;
    }
}
