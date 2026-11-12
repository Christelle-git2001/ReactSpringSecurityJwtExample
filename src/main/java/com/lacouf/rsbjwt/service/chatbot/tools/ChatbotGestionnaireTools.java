package com.lacouf.rsbjwt.service.chatbot.tools;

import com.lacouf.rsbjwt.service.donnees.DonneesEtudiantService;
import com.lacouf.rsbjwt.service.donnees.DonneesOffreService;
import com.lacouf.rsbjwt.service.donnees.DonneesCandidatureService;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.CandidatureDTO;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("chatbot")
public class ChatbotGestionnaireTools {

    private final DonneesEtudiantService donneesEtudiantService;
    private final DonneesOffreService donneesOffreService;
    private final DonneesCandidatureService donneesCandidatureService;

    public ChatbotGestionnaireTools(DonneesEtudiantService donneesEtudiantService,
                                   DonneesOffreService donneesOffreService,
                                   DonneesCandidatureService donneesCandidatureService) {
        this.donneesEtudiantService = donneesEtudiantService;
        this.donneesOffreService = donneesOffreService;
        this.donneesCandidatureService = donneesCandidatureService;
    }

    @Tool(description = "Compte les étudiants qui n'ont déposé aucun CV. Un CV refusé, en attente ou approuvé reste un CV déposé.")
    public long compterEtudiantsSansCv() {
        return donneesEtudiantService.compterEtudiantsSansCv();
    }

    @Tool(description = "Compte les étudiants qui n'ont pas encore trouvé de stage, y compris ceux en attente de réponse. Exclut ceux ayant un stage choisi ou le statut STAGE_TROUVE.")
    public long compterEtudiantsSansStage() {
        return donneesEtudiantService.compterEtudiantsSansStage();
    }

    @Tool(description = "Compte tous les étudiants inscrits sur la plateforme OSE.")
    public long compterEtudiantsInscrits() {
        return donneesEtudiantService.compterEtudiantsInscrits();
    }

    @Tool(description = "Liste les étudiants qui n'ont déposé aucun CV. Un CV refusé ou en attente reste un CV déposé.")
    public List<EtudiantDTO> obtenirEtudiantsSansCv() {
        return donneesEtudiantService.obtenirEtudiantsSansCv();
    }

    @Tool(description = "Liste les étudiants sans stage choisi et dont le statut n'est pas STAGE_TROUVE, y compris ceux en attente de réponse.")
    public List<EtudiantDTO> obtenirEtudiantsSansStage() {
        return donneesEtudiantService.obtenirEtudiantsSansStage();
    }

    @Tool(description = "Compte les étudiants ayant un stage choisi ou le statut STAGE_TROUVE. Un contrat signé n'est pas nécessaire.")
    public long compterEtudiantsAvecStage() {
        return donneesEtudiantService.compterEtudiantsAvecStage();
    }

    @Tool(description = "Liste les étudiants ayant un stage choisi ou le statut STAGE_TROUVE, même si leur contrat n'est pas encore signé.")
    public List<EtudiantDTO> obtenirEtudiantsAvecStage() {
        return donneesEtudiantService.obtenirEtudiantsAvecStage();
    }

    @Tool(description = "Liste les étudiants auxquels aucun professeur n'est assigné.")
    public List<EtudiantDTO> obtenirEtudiantsSansProfesseur() {
        return donneesEtudiantService.obtenirEtudiantsSansProfesseur();
    }

    @Tool(description = "Liste les CV déposés qui attendent la validation du gestionnaire, au statut EN_ATTENTE.")
    public List<CvEtudiantDTO> obtenirCvEnAttente() {
        return donneesEtudiantService.obtenirCvEnAttente();
    }

    @Tool(description = "Liste les offres de stage au statut EN_ATTENTE, à approuver par le gestionnaire.")
    public List<OffreDeStageDTO> obtenirOffresEnAttente() {
        return donneesOffreService.obtenirOffresEnAttente();
    }

    @Tool(description = "Liste les candidatures au statut STAGE_TROUVE sans contrat créé. Le gestionnaire doit démarrer leur processus de contrat.")
    public List<CandidatureDTO> obtenirStagesSansContrat() {
        return donneesCandidatureService.obtenirStagesSansContrat();
    }
}