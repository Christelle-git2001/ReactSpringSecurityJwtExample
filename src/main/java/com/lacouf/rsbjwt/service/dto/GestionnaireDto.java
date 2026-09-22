package com.lacouf.rsbjwt.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lacouf.rsbjwt.model.Gestionnaire;
import lombok.Builder;

@Builder
public record GestionnaireDto(
        long id,
        String firstName,
        String lastName,
        String email,
        String matricule,
        String phoneNumber
) implements UserDTO {

    public static GestionnaireDto create(Gestionnaire gestionnaire) {
        if (gestionnaire == null) {
            return empty();
        }

        return GestionnaireDto.builder()
                .id(gestionnaire.getId())
                .firstName(gestionnaire.getFirstName())
                .lastName(gestionnaire.getLastName())
                .email(gestionnaire.getEmail())
                .matricule(gestionnaire.getMatricule())
                .phoneNumber(gestionnaire.getPhoneNumber())
                .build();
    }

    public static GestionnaireDto empty() {
        return new GestionnaireDto(0L, "", "", "", "", "");
    }

    @Override
    @JsonProperty("role")
    public RoleDTO role() {
        return RoleDTO.GESTIONNAIRE;
    }
}