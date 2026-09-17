package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

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
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s]).+$",
                message = "validation.password.invalid"
        )
        String password,

        @NotBlank
        String passwordConfirmation
) {
}