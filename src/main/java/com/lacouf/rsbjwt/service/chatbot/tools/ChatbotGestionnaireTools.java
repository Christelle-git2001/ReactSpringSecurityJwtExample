package com.lacouf.rsbjwt.service.chatbot.tools;

import com.lacouf.rsbjwt.service.donnees.DonneesEtudiantService;
import com.lacouf.rsbjwt.service.donnees.DonneesOffreService;
import com.lacouf.rsbjwt.service.donnees.DonneesCandidatureService;
import com.lacouf.rsbjwt.service.donnees.DonneesProfesseurService;
import com.lacouf.rsbjwt.service.donnees.DonneesEntrevueService;
import com.lacouf.rsbjwt.service.donnees.DonneesContratService;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.model.Enum.StatutEntrevue;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.ProfesseurDTO;
import com.lacouf.rsbjwt.service.dto.EntrevueDTO;
import com.lacouf.rsbjwt.service.dto.ResumeContratDTO;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("chatbot")
public class ChatbotGestionnaireTools {

    private final DonneesEtudiantService donneesEtudiantService;
    private final DonneesOffreService donneesOffreService;
    private final DonneesCandidatureService donneesCandidatureService;
    private final DonneesProfesseurService donneesProfesseurService;
    private final DonneesEntrevueService donneesEntrevueService;
    private final DonneesContratService donneesContratService;

    public ChatbotGestionnaireTools(DonneesEtudiantService donneesEtudiantService,
                                   DonneesOffreService donneesOffreService,
                                   DonneesCandidatureService donneesCandidatureService,
                                   DonneesProfesseurService donneesProfesseurService,
                                   DonneesEntrevueService donneesEntrevueService,
                                   DonneesContratService donneesContratService) {
        this.donneesEtudiantService = donneesEtudiantService;
        this.donneesOffreService = donneesOffreService;
        this.donneesCandidatureService = donneesCandidatureService;
        this.donneesProfesseurService = donneesProfesseurService;
        this.donneesEntrevueService = donneesEntrevueService;
        this.donneesContratService = donneesContratService;
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

    @Tool(description = "Compte les CV déposés selon leur statut : EN_ATTENTE, ACCEPTEE (approuvés) ou REFUSEE.")
    public long compterCvParStatut(@ToolParam(description = "Statut des CV à compter") Statut statut) {
        return donneesEtudiantService.compterCvParStatut(statut);
    }

    @Tool(description = "Liste les étudiants dont le CV a été refusé et doit être corrigé.")
    public List<EtudiantDTO> obtenirEtudiantsAvecCvRefuse() {
        return donneesEtudiantService.obtenirEtudiantsAvecCvRefuse();
    }

    @Tool(description = "Compte les étudiants inscrits dans le département demandé.")
    public long compterEtudiantsParDepartement(
            @ToolParam(description = "Département, par exemple INFORMATIQUE ou SOINS_INFIRMIERS") Departement departement) {
        return donneesEtudiantService.compterEtudiantsParDepartement(departement);
    }

    @Tool(description = "Liste les professeurs inscrits avec leurs identifiants. Utiliser cette liste pour identifier un professeur par son nom avant de consulter ses étudiants.")
    public List<ProfesseurDTO> obtenirProfesseurs() {
        return donneesProfesseurService.obtenirProfesseurs();
    }

    @Tool(description = "Liste les professeurs auxquels aucun étudiant n'est assigné.")
    public List<ProfesseurDTO> obtenirProfesseursSansEtudiants() {
        return donneesProfesseurService.obtenirProfesseursSansEtudiants();
    }

    @Tool(description = "Liste les étudiants assignés au professeur demandé. Chercher d'abord son identifiant avec obtenirProfesseurs si seul son nom est fourni. En cas d'homonymes, demander une précision.")
    public List<EtudiantDTO> obtenirEtudiantsParProfesseur(
            @ToolParam(description = "Identifiant du professeur fourni par l'utilisateur ou obtenu avec obtenirProfesseurs. Ne jamais l'inventer.") Long professeurId) {
        return donneesEtudiantService.obtenirEtudiantsParProfesseur(professeurId);
    }

    @Tool(description = "Compte les candidatures selon leur statut de candidature. ENTREVUE_PLANIFIEE ne permet pas de savoir si l'entrevue est confirmée ou terminée : utiliser alors les outils d'entrevue.")
    public long compterCandidaturesParStatut(
            @ToolParam(description = "Statut de candidature, par exemple SOUMISE, ACCEPTEE ou REFUSEE") StatutCandidature statut) {
        return donneesCandidatureService.compterCandidaturesParStatut(statut);
    }

    @Tool(description = "Liste les candidatures selon leur statut, avec les étudiants et les offres concernés. Pour CONTRAT_CREE, indique aussi si les trois signatures sont présentes.")
    public List<CandidatureDTO> obtenirCandidaturesParStatut(
            @ToolParam(description = "Statut des candidatures à consulter") StatutCandidature statut) {
        return donneesCandidatureService.obtenirCandidaturesParStatut(statut);
    }

    @Tool(description = "Compte les offres selon leur statut : EN_ATTENTE, ACCEPTEE ou REFUSEE, y compris les offres dont l'affichage est expiré.")
    public long compterOffresParStatut(@ToolParam(description = "Statut des offres à compter") Statut statut) {
        return donneesOffreService.compterOffresParStatut(statut);
    }

    @Tool(description = "Liste les offres approuvées au statut ACCEPTEE, y compris les offres dont l'affichage est expiré. Cette liste n'est pas limitée aux offres actuellement disponibles aux étudiants.")
    public List<OffreDeStageDTO> obtenirOffresApprouvees() {
        return donneesOffreService.obtenirOffresApprouvees();
    }

    @Tool(description = "Liste les entrevues à une date future, au statut A_CONFIRMER ou CONFIRMEE, triées par date. Exclut les entrevues refusées et terminées. Il s'agit des statuts enregistrés en base.")
    public List<EntrevueDTO> obtenirEntrevuesAVenir() {
        return donneesEntrevueService.obtenirEntrevuesAVenir();
    }

    @Tool(description = "Compte les entrevues selon leur statut enregistré : A_CONFIRMER, CONFIRMEE, REFUSEE ou TERMINEE. Le statut d'entrevue est distinct du statut de candidature.")
    public long compterEntrevuesParStatut(
            @ToolParam(description = "Statut des entrevues à compter") StatutEntrevue statut) {
        return donneesEntrevueService.compterEntrevuesParStatut(statut);
    }

    @Tool(description = "Liste les étudiants qui n'ont jamais eu d'entrevue enregistrée. Une convocation refusée compte comme une entrevue enregistrée.")
    public List<EtudiantDTO> obtenirEtudiantsSansEntrevue() {
        return donneesEtudiantService.obtenirEtudiantsSansEntrevue();
    }

    @Tool(description = "Liste les contrats dont au moins une des trois signatures manque, avec les étudiants, les offres et les dates de signature. Une date absente indique une signature manquante.")
    public List<ResumeContratDTO> obtenirContratsNonSignes() {
        return donneesContratService.obtenirContratsNonSignes();
    }

    @Tool(description = "Liste les contrats prêts à être signés par le gestionnaire : l'étudiant et l'employeur ont signé, mais la signature du gestionnaire manque. Inclut les noms des étudiants et les offres.")
    public List<ResumeContratDTO> obtenirContratsASignerGestionnaire() {
        return donneesContratService.obtenirContratsASignerGestionnaire();
    }
}