package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PlageHoraireDTO(
        @NotNull
        @Future
        LocalDateTime dateHeureDebut,
        int dureeMinutes //Integer?????????
) {
}