package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Entrevue;

import java.time.LocalDateTime;

public record ReponseConvocationDTO(Long entrevueId, LocalDateTime dateHeureEntrevue,
                                    LocalDateTime dateConfirmation, boolean confirmee) {
    public static ReponseConvocationDTO of(Entrevue entrevue) {
        return new ReponseConvocationDTO(entrevue.getId(), entrevue.getDateHeureEntrevue(),
                entrevue.getDateConfirmation(), entrevue.getDateConfirmation() != null
        );
    }
}