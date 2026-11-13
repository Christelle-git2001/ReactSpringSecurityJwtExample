package com.lacouf.rsbjwt.config;

import com.lacouf.rsbjwt.Exception.ParametreDonneesInvalideException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("chatbot")
public class ChatbotConfiguration {
    @Bean
    public ToolExecutionExceptionProcessor chatbotToolExecutionExceptionProcessor() {
        return exception -> {
            if (exception.getCause() instanceof ParametreDonneesInvalideException cause) {
                return "Consultation non exécutée : " + cause.explicationPourOutil()
                        + " Corrige les paramètres à partir de la question si possible, sinon demande une précision."
                        + " Ne présente pas cette erreur comme un résultat et n'invente aucune donnée."
                        + " Réponds dans la langue de la question.";
            }
            // Une erreur technique ou d'autorisation ne doit pas être envoyée au modèle.
            throw exception;
        };
    }

    @Bean
    public ChatClient chatbotChatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}