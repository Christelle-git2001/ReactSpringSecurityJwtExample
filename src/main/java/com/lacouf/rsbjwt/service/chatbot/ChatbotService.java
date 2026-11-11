package com.lacouf.rsbjwt.service.chatbot;

import com.lacouf.rsbjwt.service.chatbot.tools.ChatbotGestionnaireTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Profile("chatbot")
public class ChatbotService {
    private final ChatClient chatClient;
    private final ChatbotGestionnaireTools gestionnaireTools;
    private final Resource promptGestionnaire;

    public ChatbotService(ChatClient chatClient, ChatbotGestionnaireTools gestionnaireTools,
                          @Value("classpath:prompts/chatbot-gestionnaire.st") Resource promptGestionnaire) {
        this.chatClient = chatClient;
        this.gestionnaireTools = gestionnaireTools;
        this.promptGestionnaire = promptGestionnaire;
    }

    @PreAuthorize("hasAuthority('GESTIONNAIRE')")
    public String poserQuestion(String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("La question ne doit pas être vide.");
        }

        return chatClient.prompt()
                .system(promptGestionnaire)
                .user(question.trim())
                .tools(gestionnaireTools)
                .call()
                .content();
    }
}