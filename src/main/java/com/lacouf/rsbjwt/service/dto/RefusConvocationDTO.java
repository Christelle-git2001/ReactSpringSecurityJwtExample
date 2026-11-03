package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefusConvocationDTO(
        @NotBlank(message = "convocation.refusal_reason_required")
        @Size(max = 500, message = "convocation.refusal_reason_too_long")
        String motifRefus
) {
}