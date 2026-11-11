package com.lacouf.rsbjwt.service.chatbot.tools;

import com.lacouf.rsbjwt.service.donnees.DonneesEtudiantService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("chatbot")
public class ChatbotGestionnaireTools {

    private final DonneesEtudiantService donneesEtudiantService;

    public ChatbotGestionnaireTools(DonneesEtudiantService donneesEtudiantService) {
        this.donneesEtudiantService = donneesEtudiantService;
    }

    @Tool(description = "Compte les étudiants qui n'ont déposé aucun CV. Un CV refusé, en attente ou approuvé reste un CV déposé.")
    public long compterEtudiantsSansCv() {
        return donneesEtudiantService.compterEtudiantsSansCv();
    }

    @Tool(description = "Compte les étudiants qui n'ont pas encore trouvé de stage, y compris ceux en attente de réponse. Exclut ceux ayant un stage choisi ou le statut STAGE_TROUVE.")
    public long compterEtudiantsSansStage() {
        return donneesEtudiantService.compterEtudiantsSansStage();
    }
}