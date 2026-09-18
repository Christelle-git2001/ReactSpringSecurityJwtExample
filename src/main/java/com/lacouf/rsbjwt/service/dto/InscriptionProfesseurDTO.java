package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InscriptionProfesseurDTO(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password,

        @NotBlank
        String passwordConfirmation,

        @NotBlank
        String matricule,

        @NotBlank
        String phoneNumber,

        @NotBlank
        String department
) {}
