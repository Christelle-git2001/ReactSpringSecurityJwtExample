package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotBlank;

public record QuestionChatbotDTO(
        @NotBlank(message = "La question ne doit pas être vide.")
        String question
) {}