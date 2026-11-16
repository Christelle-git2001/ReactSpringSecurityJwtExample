package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefusCandidatureDTO(
        @NotBlank(message = "error.refusal_reason_invalid")
        @Size(max = 1000, message = "error.refusal_reason_invalid")
        String motifRefus
) { }