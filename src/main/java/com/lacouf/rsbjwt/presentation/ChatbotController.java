package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.chatbot.ChatbotService;
import com.lacouf.rsbjwt.service.dto.QuestionChatbotDTO;
import com.lacouf.rsbjwt.service.dto.ReponseChatbotDTO;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("chatbot")
@RequestMapping("/chatbot")
@PreAuthorize("hasAuthority('GESTIONNAIRE')")
public class ChatbotController {
    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/questions")
    public ResponseEntity<ReponseChatbotDTO> poserQuestion(@Valid @RequestBody QuestionChatbotDTO question) {
        String reponse = chatbotService.poserQuestion(question.question());
        return ResponseEntity.ok(new ReponseChatbotDTO(reponse));
    }
}