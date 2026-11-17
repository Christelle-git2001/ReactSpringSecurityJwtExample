package com.lacouf.rsbjwt.service.dto.chatbot;

import jakarta.validation.constraints.NotBlank;

public record QuestionChatbotDTO(
        @NotBlank(message = "La question ne doit pas être vide.")
        String question
) {}