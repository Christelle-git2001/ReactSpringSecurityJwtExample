package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionEtudiantDto {
        @NotBlank
        private String firstName;
        @NotBlank
        private String lastName;

        @Email
        @NotBlank
        private String email;
        @NotBlank
        private String password;
        @NotBlank
        private String confirmPassword;
        @NotBlank
        private String matricule;
}

