
package com.lacouf.rsbjwt.service.chatbot;

import com.lacouf.rsbjwt.service.chatbot.tools.ChatbotEmployeurTools;
import com.lacouf.rsbjwt.service.chatbot.tools.ChatbotGestionnaireTools;
import com.lacouf.rsbjwt.service.chatbot.tools.ChatbotGestionnaireTemporelTools;
import com.lacouf.rsbjwt.service.chatbot.tools.ChatbotEtudiantTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Profile("chatbot")
public class ChatbotService {

    private final ChatClient chatClient;
    private final ChatbotGestionnaireTools gestionnaireTools;
    private final ChatbotGestionnaireTemporelTools temporels;
    private final ChatbotEtudiantTools etudiantTools;
    private final ChatbotEmployeurTools employeurTools;

    private final Resource promptGestionnaire;
    private final Resource promptEtudiant;
    private final Resource promptEmployeur;

    public ChatbotService(
            ChatClient chatClient,
            ChatbotGestionnaireTools gestionnaireTools,
            ChatbotGestionnaireTemporelTools temporels,
            ChatbotEtudiantTools etudiantTools, ChatbotEmployeurTools employeurTools,
            @Value("classpath:prompts/chatbot-gestionnaire.st") Resource promptGestionnaire,
            @Value("classpath:prompts/chatbot-etudiant.st") Resource promptEtudiant,
            @Value("classpath:prompts/chatbot-employeur.st") Resource promptEmployeur) {

        this.chatClient = chatClient;
        this.gestionnaireTools = gestionnaireTools;
        this.temporels = temporels;
        this.etudiantTools = etudiantTools;
        this.employeurTools = employeurTools;
        this.promptGestionnaire = promptGestionnaire;
        this.promptEtudiant = promptEtudiant;
        this.promptEmployeur = promptEmployeur;
    }

    @PreAuthorize("hasAnyAuthority('GESTIONNAIRE', 'ETUDIANT', 'EMPLOYEUR')")
    public String poserQuestion(String question) {

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("La question ne doit pas être vide.");
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean estGestionnaire = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("GESTIONNAIRE"));

        boolean estEmployeur = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("EMPLOYEUR"));

        if (estGestionnaire) {
            return chatClient.prompt()
                    .system(promptGestionnaire)
                    .user(question.trim())
                    .tools(gestionnaireTools, temporels)
                    .call()
                    .content();
        }

        if(estEmployeur){
            return chatClient.prompt()
                    .system(promptEmployeur)
                    .user(question.trim())
                    .tools(employeurTools)
                    .call()
                    .content();
        }

        return chatClient.prompt()
                .system(promptEtudiant)
                .user(question.trim())
                .tools(etudiantTools)
                .call()
                .content();
    }
}
