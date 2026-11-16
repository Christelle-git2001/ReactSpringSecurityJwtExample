package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.AccesChatbotRefuseException;
import com.lacouf.rsbjwt.Exception.QuestionChatbotInvalideException;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.security.JwtAuthenticationEntryPoint;
import com.lacouf.rsbjwt.security.JwtTokenProvider;
import com.lacouf.rsbjwt.security.SecurityConfiguration;
import com.lacouf.rsbjwt.service.chatbot.ChatbotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitWebConfig(ChatbotControllerTest.ConfigurationTest.class)
@ActiveProfiles("chatbot")
class ChatbotControllerTest {
    @Configuration
    @EnableWebMvc
    @Import({ChatbotController.class, GlobalExceptionHandler.class,
            SecurityConfiguration.class, JwtAuthenticationEntryPoint.class})
    static class ConfigurationTest {}

    @Autowired private WebApplicationContext contexte;
    @MockitoBean private ChatbotService chatbotService;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private UserAppRepository userAppRepository;
    private MockMvc mockMvc;

    @BeforeEach
    void preparer() {
        mockMvc = MockMvcBuilders.webAppContextSetup(contexte).apply(springSecurity()).build();
    }

    @Test
    void doitRepondreAuxRolesAutorises() throws Exception {
        String[] rolesAutorises = {"GESTIONNAIRE", "ETUDIANT", "EMPLOYEUR"};
        when(chatbotService.poserQuestion("Mes candidatures ?")).thenReturn("Voici vos candidatures.");

        for (String role : rolesAutorises) {
            mockMvc.perform(post("/chatbot/questions")
                            .with(user("cindy@example.com").authorities(() -> role))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"question":"Mes candidatures ?"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.reponse").value("Voici vos candidatures."));
        }
        verify(chatbotService, times(rolesAutorises.length)).poserQuestion("Mes candidatures ?");
    }

    @Test
    void doitRetourner401SansAuthentification() throws Exception {
        mockMvc.perform(post("/chatbot/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"question":"Mes candidatures ?"}
                                """))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(chatbotService);
    }

    @Test
    void doitRetourner403PourLesRolesNonAutorises() throws Exception {
        String[] rolesInterdits = {"PROFESSEUR", "INCONNU", "ROLE_ETUDIANT"};

        for (String role : rolesInterdits) {
            mockMvc.perform(post("/chatbot/questions")
                            .with(user("cindy@example.com").authorities(() -> role))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"question":"Mes candidatures ?"}
                                    """))
                    .andExpect(status().isForbidden());
        }
        verifyNoInteractions(chatbotService);
    }

    @Test
    void doitRetourner400PourLesQuestionsVidesOuAbsentes() throws Exception {
        String[] corpsInvalides = {
                "{\"question\":\"\"}", "{\"question\":\"   \"}", "{\"question\":null}", "{}"
        };

        for (String corps : corpsInvalides) {
            mockMvc.perform(post("/chatbot/questions")
                            .with(user("cindy@example.com").authorities(() -> "GESTIONNAIRE"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(corps))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(chatbotService);
    }

    @Test
    void doitRetourner400PourUnJsonInvalide() throws Exception {
        mockMvc.perform(post("/chatbot/questions")
                        .with(user("cindy@example.com").authorities(() -> "GESTIONNAIRE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(chatbotService);
    }

    @Test
    void doitRetourner400AvecLaCleDeQuestionInvalideDuService() throws Exception {
        when(chatbotService.poserQuestion("Mes candidatures ?"))
                .thenThrow(new QuestionChatbotInvalideException());

        mockMvc.perform(post("/chatbot/questions")
                        .with(user("cindy@example.com").authorities(() -> "ETUDIANT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"question":"Mes candidatures ?"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("chatbot.errors.question_required"));
    }

    @Test
    void doitRetourner403AvecLaCleDeRefusDuService() throws Exception {
        when(chatbotService.poserQuestion("Mes candidatures ?"))
                .thenThrow(new AccesChatbotRefuseException());

        mockMvc.perform(post("/chatbot/questions")
                        .with(user("cindy@example.com").authorities(() -> "ETUDIANT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"question":"Mes candidatures ?"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("chatbot.errors.access_denied"));
    }
}