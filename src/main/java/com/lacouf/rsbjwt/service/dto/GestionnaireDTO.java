package com.lacouf.rsbjwt.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lacouf.rsbjwt.model.Gestionnaire;
import lombok.Builder;

@Builder
public record GestionnaireDTO(
        long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String password
) implements UserDTO {

    public static GestionnaireDTO create(Gestionnaire gestionnaire) {
        if (gestionnaire == null) {
            return empty();
        }

        return GestionnaireDTO.builder()
                .id(gestionnaire.getId())
                .firstName(gestionnaire.getFirstName())
                .lastName(gestionnaire.getLastName())
                .email(gestionnaire.getEmail())
                .phoneNumber(gestionnaire.getPhoneNumber())
                .build();
    }

    public static GestionnaireDTO empty() {
        return new GestionnaireDTO(0L, "", "", "", "","");
    }

    @Override
    @JsonProperty("role")
    public RoleDTO role() {
        return RoleDTO.GESTIONNAIRE;
    }
}