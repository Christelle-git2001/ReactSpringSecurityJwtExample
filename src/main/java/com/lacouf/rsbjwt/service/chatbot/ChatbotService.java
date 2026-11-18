package com.lacouf.rsbjwt.service.chatbot;

import com.lacouf.rsbjwt.exception.chatbot.AccesChatbotRefuseException;
import com.lacouf.rsbjwt.exception.chatbot.QuestionChatbotInvalideException;
import com.lacouf.rsbjwt.service.chatbot.tools.*;
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
    private final ChatbotEtudiantTools etudiantTools;
    private final ChatbotEmployeurTools employeurTools;
    private final ChatbotProfesseurTools professeurTools;

    private final Resource promptGestionnaire;
    private final Resource promptEtudiant;
    private final Resource promptEmployeur;
    private final Resource promptProfesseur;

    public ChatbotService(
            ChatClient chatClient,
            ChatbotGestionnaireTools gestionnaireTools,
            ChatbotEtudiantTools etudiantTools,
            ChatbotEmployeurTools employeurTools,
            ChatbotProfesseurTools professeurTools,
            @Value("classpath:prompts/chatbot-gestionnaire.st") Resource promptGestionnaire,
            @Value("classpath:prompts/chatbot-etudiant.st") Resource promptEtudiant,
            @Value("classpath:prompts/chatbot-employeur.st") Resource promptEmployeur,
            @Value("classpath:prompts/chatbot-professeur.st") Resource promptProfesseur) {

        this.chatClient = chatClient;
        this.gestionnaireTools = gestionnaireTools;
        this.etudiantTools = etudiantTools;
        this.employeurTools = employeurTools;
        this.professeurTools = professeurTools;
        this.promptGestionnaire = promptGestionnaire;
        this.promptEtudiant = promptEtudiant;
        this.promptEmployeur = promptEmployeur;
        this.promptProfesseur = promptProfesseur;
    }

    @PreAuthorize("hasAnyAuthority('GESTIONNAIRE', 'ETUDIANT', 'EMPLOYEUR', 'PROFESSEUR')")
    public String poserQuestion(String question) throws AccesChatbotRefuseException, QuestionChatbotInvalideException {

        if (question == null || question.isBlank()) {
            throw new QuestionChatbotInvalideException();
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccesChatbotRefuseException();
        }

        boolean estGestionnaire = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("GESTIONNAIRE"));

        boolean estEmployeur = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("EMPLOYEUR"));

        boolean estProfesseur = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("PROFESSEUR"));

        boolean estEtudiant = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ETUDIANT"));

        if (estGestionnaire) {
            return chatClient.prompt()
                    .system(promptGestionnaire)
                    .user(question.trim())
                    .tools(gestionnaireTools)
                    .call()
                    .content();
        }

        if (estEmployeur) {
            return chatClient.prompt()
                    .system(promptEmployeur)
                    .user(question.trim())
                    .tools(employeurTools)
                    .call()
                    .content();
        }

        if (estProfesseur) {
            return chatClient.prompt()
                    .system(promptProfesseur)
                    .user(question.trim())
                    .tools(professeurTools)
                    .call()
                    .content();
        }

        if (estEtudiant) {
            return chatClient.prompt()
                    .system(promptEtudiant)
                    .user(question.trim())
                    .tools(etudiantTools)
                    .call()
                    .content();
        }

        throw new AccesChatbotRefuseException();
    }
}