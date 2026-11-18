
package com.lacouf.rsbjwt.service.chatbot.tools;
import com.lacouf.rsbjwt.exception.utilisateur.UtilisateurIntrouvableException;
import com.lacouf.rsbjwt.service.NotificationService;
import com.lacouf.rsbjwt.service.ProfesseurService;
import com.lacouf.rsbjwt.service.dto.evaluation_stagiaire.StageEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.candidature.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.EntrevueDTO;
import com.lacouf.rsbjwt.service.dto.notification.NotificationDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EtudiantDTO;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("chatbot")
public class ChatbotProfesseurTools {

    private final ProfesseurService professeurService;
    private final NotificationService notificationService;

    public ChatbotProfesseurTools(
            ProfesseurService professeurService,
            NotificationService notificationService) {
        this.professeurService = professeurService;
        this.notificationService = notificationService;
    }

    @Tool(description = "Liste les étudiants assignés au professeur connecté.")
    public List<EtudiantDTO> obtenirMesEtudiantsAssignes()
            throws UtilisateurIntrouvableException {
        return professeurService.getEtudiantsAssignes();
    }

    @Tool(description = "Liste les candidatures de tous les étudiants assignés.")
    public List<CandidatureDTO> obtenirCandidaturesDeMesEtudiants()
            throws UtilisateurIntrouvableException {
        return professeurService.getCandidaturesDeMesEtudiants();
    }

    @Tool(description = "Liste les entrevues de tous les étudiants assignés.")
    public List<EntrevueDTO> obtenirEntrevuesDeMesEtudiants()
            throws UtilisateurIntrouvableException {
        return professeurService.getEntrevuesDeMesEtudiants();
    }

    @Tool(description = "Liste les stages trouvés par les étudiants assignés.")
    public List<StageEtudiantDTO> obtenirStagesDeMesEtudiants()
            throws UtilisateurIntrouvableException {
        return professeurService.getStagesDeMesEtudiants();
    }

    @Tool(description = "Consulte les notifications non lues du professeur connecté.")
    public List<NotificationDTO> consulterMesNotificationsNonLues()
            throws UtilisateurIntrouvableException {
        return notificationService.getNotificationsNonLuesProfesseur();
    }
}
