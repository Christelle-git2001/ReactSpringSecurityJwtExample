package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InscriptionEtudiantDTO(
        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        String phone,

        @NotBlank
        @Email
        String email,
        @NotBlank
        String matricule,

        @NotBlank
        String password,

        @NotBlank
        String passwordConfirmation
) {

}