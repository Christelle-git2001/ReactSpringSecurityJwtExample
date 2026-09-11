package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.auth.Role;
import lombok.Builder;

public class EtudiantDto extends UserDTO {

    private String matricule;

    @Builder
    public EtudiantDto(
            Long id,
            String firstName,
            String lastName,
            String email,
            Role role,
            String matricule) {

        super(id, firstName, lastName, email, role);
        this.matricule = matricule;
    }

    public EtudiantDto() {
    }

    public static EtudiantDto create(Etudiant etudiant) {
        return EtudiantDto.builder()
                .id(etudiant.getId())
                .firstName(etudiant.getFirstName())
                .lastName(etudiant.getLastName())
                .email(etudiant.getEmail())
                .role(etudiant.getRole())
                .matricule(etudiant.getMatricule())
                .build();
    }

    public static EtudiantDto empty() {
        return new EtudiantDto();
    }
}
