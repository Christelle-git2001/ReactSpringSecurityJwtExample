package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record InscriptionEtudiantDTO {
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
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s]).+$",
                message = "validation.password.invalid"
        )
        String password,
        @NotBlank
        String passwordConfirmation;
}




