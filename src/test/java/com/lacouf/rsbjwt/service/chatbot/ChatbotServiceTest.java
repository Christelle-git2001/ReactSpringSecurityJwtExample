package com.lacouf.rsbjwt.service.chatbot;

import com.lacouf.rsbjwt.Exception.AccesChatbotRefuseException;
import com.lacouf.rsbjwt.Exception.QuestionChatbotInvalideException;
import com.lacouf.rsbjwt.service.chatbot.tools.ChatbotEmployeurTools;
import com.lacouf.rsbjwt.service.chatbot.tools.ChatbotEtudiantTools;
import com.lacouf.rsbjwt.service.chatbot.tools.ChatbotGestionnaireTools;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {
    @Mock private ChatClient chatClient;
    @Mock private ChatClient.ChatClientRequestSpec requete;
    @Mock private ChatClient.CallResponseSpec reponse;
    @Mock private ChatbotGestionnaireTools gestionnaireTools;
    @Mock private ChatbotEtudiantTools etudiantTools;
    @Mock private ChatbotEmployeurTools employeurTools;
    @Mock private Resource promptGestionnaire;
    @Mock private Resource promptEtudiant;
    @Mock private Resource promptEmployeur;
    private ChatbotService service;

    @BeforeEach
    void preparer() {
        SecurityContextHolder.clearContext();
        service = new ChatbotService(chatClient, gestionnaireTools, etudiantTools, employeurTools,
                promptGestionnaire, promptEtudiant, promptEmployeur);
    }

    @AfterEach
    void nettoyer() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doitUtiliserLePromptEtLesToolsGestionnaire() throws Exception {
        verifierReponsePourRole("GESTIONNAIRE", promptGestionnaire, gestionnaireTools);
    }

    @Test
    void doitUtiliserLePromptEtLesToolsEtudiant() throws Exception {
        verifierReponsePourRole("ETUDIANT", promptEtudiant, etudiantTools);
    }

    @Test
    void doitUtiliserLePromptEtLesToolsEmployeur() throws Exception {
        verifierReponsePourRole("EMPLOYEUR", promptEmployeur, employeurTools);
    }

    @Test
    void doitRefuserLesQuestionsVides() {
        String[] questionsInvalides = {null, "", "   ", "\t\n"};

        for (String question : questionsInvalides) {
            assertThatExceptionOfType(QuestionChatbotInvalideException.class)
                    .isThrownBy(() -> service.poserQuestion(question))
                    .withMessage("chatbot.errors.question_required");
        }
        verifyNoInteractions(chatClient);
    }

    @Test
    void doitRefuserLesRolesNonAutorisesSansUtiliserLesToolsEtudiant() {
        String[] rolesInterdits = {"PROFESSEUR", "INCONNU", "ROLE_ETUDIANT", "ROLE_ANONYMOUS"};

        for (String role : rolesInterdits) {
            connecter(role);

            assertThatExceptionOfType(AccesChatbotRefuseException.class)
                    .isThrownBy(() -> service.poserQuestion("Mes candidatures ?"));
        }
        verifyNoInteractions(chatClient, etudiantTools);
    }

    @Test
    void doitRefuserUneAuthentificationAbsente() {
        assertThatExceptionOfType(AccesChatbotRefuseException.class)
                .isThrownBy(() -> service.poserQuestion("Mes candidatures ?"));
        verifyNoInteractions(chatClient);
    }

    @Test
    void doitRefuserUneAuthentificationNonValidee() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("cindy@example.com", "test"));

        assertThatExceptionOfType(AccesChatbotRefuseException.class)
                .isThrownBy(() -> service.poserQuestion("Mes candidatures ?"));
        verifyNoInteractions(chatClient);
    }

    private void verifierReponsePourRole(String role, Resource prompt, Object tools) throws Exception {
        connecter(role);
        when(chatClient.prompt()).thenReturn(requete);
        when(requete.system(prompt)).thenReturn(requete);
        when(requete.user("Mes candidatures ?")).thenReturn(requete);
        when(requete.tools(tools)).thenReturn(requete);
        when(requete.call()).thenReturn(reponse);
        when(reponse.content()).thenReturn("Voici vos candidatures.");

        String result = service.poserQuestion("  Mes candidatures ?  ");

        assertThat(result).isEqualTo("Voici vos candidatures.");
        verify(requete).system(prompt);
        verify(requete).user("Mes candidatures ?");
        verify(requete).tools(tools);
    }

    private void connecter(String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("cindy@example.com", null,
                        List.of(new SimpleGrantedAuthority(role))));
    }
}