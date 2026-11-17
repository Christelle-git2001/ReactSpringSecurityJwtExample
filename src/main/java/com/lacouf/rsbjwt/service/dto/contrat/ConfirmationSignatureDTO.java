package com.lacouf.rsbjwt.service.dto.contrat;

import jakarta.validation.constraints.NotBlank;

public record ConfirmationSignatureDTO(
        @NotBlank(message = "error.password_required")
        String motDePasse
) {}