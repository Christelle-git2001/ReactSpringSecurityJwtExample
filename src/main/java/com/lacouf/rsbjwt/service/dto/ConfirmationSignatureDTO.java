package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmationSignatureDTO(
        @NotBlank(message = "error.password_required")
        String motDePasse
) {}