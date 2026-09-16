package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmployeurInscriptionDTO extends EmployeurDTO {
    @NotBlank(message = "validation.password.required")
    private String password;
    @NotBlank(message = "validation.passwordConfirmation.required")
    private String passwordConfirmation;
}
