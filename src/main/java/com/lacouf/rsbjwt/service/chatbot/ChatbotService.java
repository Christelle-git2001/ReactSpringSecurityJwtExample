package com.lacouf.rsbjwt.service.chatbot;

import com.lacouf.rsbjwt.exception.chatbot.AccesChatbotRefuseException;
import com.lacouf.rsbjwt.exception.chatbot.QuestionChatbotInvalideException;
import com.lacouf.rsbjwt.service.chatbot.tools.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private final Map<String, List<Message>> historiques = new ConcurrentHashMap<>();

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
    public String poserQuestion(String question)
            throws AccesChatbotRefuseException, QuestionChatbotInvalideException {

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
            return appelerChat(promptGestionnaire, gestionnaireTools, question, authentication.getName());
        }

        if(estEmployeur){
            return appelerChat(promptEmployeur, employeurTools, question, authentication.getName());
        }

        if (estEtudiant) {
            return appelerChat(promptEtudiant, etudiantTools, question, authentication.getName());
        }

        if (estProfesseur) {
            return appelerChat(promptProfesseur, professeurTools, question, authentication.getName());
        }

        throw new AccesChatbotRefuseException();
    }

    private String appelerChat(Resource prompt, Object outils, String question, String conversationId) {
        List<Message> historique = historiques.computeIfAbsent(conversationId, id -> new ArrayList<>()).stream().toList();

        String reponse = chatClient.prompt()
                .system(prompt)
                .messages(historique)
                .user(question.trim())
                .tools(outils)
                .call()
                .content();

        List<Message> historiqueMisAJour = historiques.computeIfAbsent(conversationId, id -> new ArrayList<>());
        synchronized (historiqueMisAJour) {
            historiqueMisAJour.add(new UserMessage(question.trim()));
            historiqueMisAJour.add(new AssistantMessage(reponse));
            while (historiqueMisAJour.size() > 20) {
                historiqueMisAJour.remove(0);
            }
        }

        return reponse;
    }
}