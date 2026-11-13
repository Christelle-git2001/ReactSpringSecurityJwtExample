package com.lacouf.rsbjwt.service.chatbot;

import com.lacouf.rsbjwt.service.chatbot.tools.ChatbotGestionnaireTools;
import com.lacouf.rsbjwt.service.chatbot.tools.ChatbotGestionnaireTemporelTools;
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
    private final ChatbotGestionnaireTemporelTools temporels;

    public ChatbotService(ChatClient chatClient, ChatbotGestionnaireTools gestionnaireTools,
                          @Value("classpath:prompts/chatbot-gestionnaire.st") Resource promptGestionnaire,
                          ChatbotGestionnaireTemporelTools temporels) {
        this.chatClient = chatClient;
        this.gestionnaireTools = gestionnaireTools;
        this.promptGestionnaire = promptGestionnaire;
        this.temporels = temporels;
    }

    @PreAuthorize("hasAuthority('GESTIONNAIRE')")
    public String poserQuestion(String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("La question ne doit pas être vide.");
        }

        return chatClient.prompt()
                .system(promptGestionnaire)
                .user(question.trim())
                .tools(gestionnaireTools, temporels)
                .call()
                .content();
    }
}