package com.lacouf.rsbjwt.service.chatbot.tools;

import com.lacouf.rsbjwt.service.dto.candidature.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.cv.MonCvTeleverseDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.EntrevueDTO;
import com.lacouf.rsbjwt.service.dto.notification.NotificationDTO;
import com.lacouf.rsbjwt.service.dto.offre.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.MonProfesseurAssigneDTO;
import com.lacouf.rsbjwt.exception.utilisateur.EtudiantIntrouvableException;
import com.lacouf.rsbjwt.service.EtudiantService;
import com.lacouf.rsbjwt.service.NotificationService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.lacouf.rsbjwt.exception.cv.CurriculumVitaeIntrouvable;
import com.lacouf.rsbjwt.exception.cv.CurriculumVitaeNonApprouveException;

import java.util.List;

@Component
@Profile("chatbot")
public class ChatbotEtudiantTools {

    private final EtudiantService etudiantService;
    private final NotificationService notificationService;

    public ChatbotEtudiantTools(EtudiantService etudiantService, NotificationService notificationService) {
        this.etudiantService = etudiantService;
        this.notificationService = notificationService;
    }

    @Tool(description = "Consulte le CV de l'étudiant actuellement connecté. Indique si un CV a été téléversé. Si oui, retourne son nom, sa date de téléversement, son statut et son motif de refus s'il existe.")
    public MonCvTeleverseDTO consulterMonCv()
            throws EtudiantIntrouvableException {

        return etudiantService.getMonCv();
    }


    @Tool(description = "Liste les offres de stage actuellement disponibles pour l'étudiant connecté auxquelles il n'a pas encore postulé. Une candidature existante compte comme une candidature déjà déposée, même si elle a été refusée.")
    public List<OffreDeStageDTO> obtenirMesOffresNonPostulees()
            throws EtudiantIntrouvableException,
            CurriculumVitaeNonApprouveException,
            CurriculumVitaeIntrouvable {

        return etudiantService.getOffresNonPostulees();
    }


    @Tool(description = "Liste les nouvelles offres de stage actuellement disponibles pour l'étudiant connecté et approuvées depuis sa dernière connexion à la plateforme. Utilise la date approvedAt et previousLoginAt. Lors de sa première connexion, retourne toutes les offres actuellement disponibles.")
    public List<OffreDeStageDTO> obtenirNouvellesOffresDepuisDerniereVisite()
            throws EtudiantIntrouvableException,
            CurriculumVitaeNonApprouveException,
            CurriculumVitaeIntrouvable {

        return etudiantService.getNouvellesOffresDepuisDerniereVisite();
    }

    @Tool(description = "Consulte toutes les candidatures de l'étudiant actuellement connecté. Retourne les offres auxquelles il a postulé, leur statut, la date de candidature et le motif du refus s'il existe.")
    public List<CandidatureDTO> consulterMesCandidatures()
            throws EtudiantIntrouvableException {
        return etudiantService.getCandidaturesEtudiant();
    }

    @Tool(description = """
        Consulte les entrevues de l'étudiant actuellement connecté.
        Retourne les dates, heures, durées, formats, entreprises,
        offres concernées et statuts des entrevues.
        """)
    public List<EntrevueDTO> consulterMesEntrevues()
            throws EtudiantIntrouvableException {
        return etudiantService.getConvocationsEtudiant();
    }

    @Tool(description = """
        Consulte les informations du professeur responsable
        de l'étudiant actuellement connecté.
        Retourne son nom, son prénom, son adresse courriel,
        son numéro de téléphone et son département.
        """)
    public MonProfesseurAssigneDTO consulterMonProfesseur()
            throws EtudiantIntrouvableException {
        return etudiantService.getMonProfesseur();
    }

    @Tool(description = """
        Consulte les notifications non lues de l'étudiant actuellement connecté.
        Retourne leur titre, leur description et leur date.
        """)
    public List<NotificationDTO> consulterMesNotificationsNonLues()
            throws EtudiantIntrouvableException {
        return notificationService.getNotificationsNonLuesEtudiant();
    }
}
