package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeurInscriptionDTO extends EmployeurDTO {
    @NotBlank(message = "validation.password.required")
    private String password;
}
