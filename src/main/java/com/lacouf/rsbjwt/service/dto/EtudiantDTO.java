package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.auth.Role;
import lombok.Builder;

public class EtudiantDTO extends UserDTO {

    private String matricule;

    @Builder
    public EtudiantDTO(
            //int ou LOng
            Long id,
            String firstName,
            String lastName,
            String email,
            Role role,
            String matricule) {

        super(id, firstName, lastName, email, role);
        this.matricule = matricule;
    }

    public EtudiantDTO() {
    }

    public static EtudiantDTO create(Etudiant etudiant) {
        return EtudiantDTO.builder()
                .id(etudiant.getId())
                .firstName(etudiant.getFirstName())
                .lastName(etudiant.getLastName())
                .email(etudiant.getEmail())
                .role(etudiant.getRole())
                .matricule(etudiant.getMatricule())
                .build();
    }

    public static EtudiantDTO empty() {
        return new EtudiantDTO();
    }
}
