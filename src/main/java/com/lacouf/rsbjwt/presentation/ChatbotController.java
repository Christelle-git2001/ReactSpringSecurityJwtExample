package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.dto.chatbot.QuestionChatbotDTO;
import com.lacouf.rsbjwt.service.dto.chatbot.ReponseChatbotDTO;
import com.lacouf.rsbjwt.exception.chatbot.AccesChatbotRefuseException;
import com.lacouf.rsbjwt.exception.chatbot.QuestionChatbotInvalideException;
import com.lacouf.rsbjwt.service.chatbot.ChatbotService;
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
@PreAuthorize("hasAnyAuthority('GESTIONNAIRE', 'ETUDIANT', 'EMPLOYEUR', 'PROFESSEUR')")
public class ChatbotController {
    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/questions")
    public ResponseEntity<ReponseChatbotDTO> poserQuestion(@Valid @RequestBody QuestionChatbotDTO question)
            throws QuestionChatbotInvalideException, AccesChatbotRefuseException {
        String reponse = chatbotService.poserQuestion(question.question());
        return ResponseEntity.ok(new ReponseChatbotDTO(reponse));
    }
}