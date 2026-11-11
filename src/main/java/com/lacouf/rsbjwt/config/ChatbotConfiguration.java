package com.lacouf.rsbjwt.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("chatbot")
public class ChatbotConfiguration {
    @Bean
    public ChatClient chatbotChatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}