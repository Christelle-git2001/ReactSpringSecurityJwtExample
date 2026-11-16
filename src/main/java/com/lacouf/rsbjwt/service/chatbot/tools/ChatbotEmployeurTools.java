package com.lacouf.rsbjwt.service.chatbot.tools;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.Enum.StatutEntrevue;
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.NotificationService;
import com.lacouf.rsbjwt.service.donnees.DonneesTemporellesService;
import com.lacouf.rsbjwt.service.dto.*;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("chatbot")
public class ChatbotEmployeurTools {

    private final EmployeurService employeurService;
    private final NotificationService notificationService;
    private final DonneesTemporellesService donneesTemporellesService;

    public ChatbotEmployeurTools(EmployeurService employeurService, NotificationService notificationService, DonneesTemporellesService donneesTemporellesService) {
        this.employeurService = employeurService;
        this.notificationService = notificationService;
        this.donneesTemporellesService = donneesTemporellesService;
    }

    @Tool(description = "Consulte la liste de toutes les offres de stage publiées par l'employeur actuellement connecté pour la session active. Retourne les titres, descriptions, salaires, domaines, dates et statuts.")
    public List<OffreDeStageDTO> obtenirOffres() {
        return employeurService.obtenirOffres();
    }

    @Tool(description = "Consulte l'ensemble des candidatures reçues par l'employeur connecté pour ses offres de stage de la session active.")
    public List<CandidatureDTO> obtenirCandidatures()
            throws UtilisateurIntrouvableException {
        return employeurService.obtenirCandidatures();
    }

    @Tool(description = "Consulte les candidatures qui ont été retenues pour une entrevue parmi les offres de l'employeur connecté.")
    public List<CandidatureDTO> obtenirCandidaturesConvoquees()
            throws UtilisateurIntrouvableException {
        return employeurService.obtenirCandidaturesConvoquees();
    }

    @Tool(description = "Consulte la liste de toutes les entrevues planifiées ou terminées (hors entrevues refusées) associées aux offres de stage de l'employeur connecté pour la session active. Retourne les dates, heures, durées, formats et statuts.")
    public List<EntrevueDTO> obtenirEntrevues()
            throws UtilisateurIntrouvableException {
        return employeurService.obtenirEntrevues();
    }

    @Tool(description = "Consulte les notifications non lues de l'employeur actuellement connecté. Retourne leur titre, leur description et leur date.")
    public List<NotificationDTO> consulterMesNotificationsNonLues()
            throws EmployeurIntrouvable {
        return notificationService.getNotificationsNonLuesEmployeur();
    }

    @Tool(description = "Liste les candidats ayant postulé à une offre de stage précise de l'employeur. Utiliser obtenirOffres au préalable pour récupérer l'identifiant (offreId) de l'offre.")
    public List<CandidatureDTO> obtenirCandidaturesParOffre(
            @ToolParam(description = "Identifiant de l'offre de stage. Ne jamais l'inventer.") Long offreId)
            throws  UtilisateurIntrouvableException, OffreIntrouvableException, OffreNonAutoriseeException {
        return employeurService.getCandidaturesParOffre(offreId);
    }

    @Tool(description = "Liste tous les contrats de stage associés aux offres de l'employeur connecté pour la session active, avec l'état des signatures (employeur, étudiant, gestionnaire) et les dates de signature.")
    public List<ResumeContratDTO> obtenirMesContrats()
            throws UtilisateurIntrouvableException {
        return employeurService.obtenirContratsEmployeur();
    }

    @Tool(description = "Liste toutes les entrevues de l'employeur connecté selon un statut précis : A_CONFIRMER, CONFIRMEE, REFUSEE (avec motif de refus) ou TERMINEE.")
    public List<EntrevueDTO> obtenirEntrevuesParStatut(
            @ToolParam(description = "Statut de l'entrevue : A_CONFIRMER, CONFIRMEE, REFUSEE ou TERMINEE") StatutEntrevue statut)
            throws UtilisateurIntrouvableException {
        return employeurService.obtenirEntrevuesParStatut(statut);
    }

    @Tool(description = "Retourne la date et l'heure actuelles ainsi que les dates de début et de fin de la semaine en cours. À utiliser avant de répondre à toute question temporelle (cette semaine, aujourd'hui, bientôt).")
    public ReferenceTemporelleDTO obtenirDateReference() {
        return donneesTemporellesService.obtenirDateReference();
    }

}