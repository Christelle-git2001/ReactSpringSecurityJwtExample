package com.lacouf.rsbjwt.service.chatbot;

import com.lacouf.rsbjwt.service.dto.candidature.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.commun.ReferenceTemporelleDTO;
import com.lacouf.rsbjwt.service.dto.commun.ResultatDonneesDTO;
import com.lacouf.rsbjwt.service.dto.contrat.ResumeContratDTO;
import com.lacouf.rsbjwt.service.dto.cv.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.EntrevueDTO;
import com.lacouf.rsbjwt.service.dto.offre.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.ProfesseurDTO;
import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException;
import com.lacouf.rsbjwt.model.Enum.ModeResultat;
import com.lacouf.rsbjwt.model.Enum.SignataireContrat;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.model.Enum.StatutEntrevue;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.service.chatbot.tools.ChatbotGestionnaireTools;
import com.lacouf.rsbjwt.service.donnees.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatbotGestionnaireToolsTest {

    @Mock
    private DonneesEtudiantService donneesEtudiantService;

    @Mock
    private DonneesTemporellesService donneesTemporellesService;

    @Mock
    private DonneesOffreService donneesOffreService;

    @Mock
    private DonneesCandidatureService donneesCandidatureService;

    @Mock
    private DonneesProfesseurService donneesProfesseurService;

    @Mock
    private DonneesEntrevueService donneesEntrevueService;

    @Mock
    private DonneesContratService donneesContratService;

    private ChatbotGestionnaireTools chatbotGestionnaireTools;

    @BeforeEach
    void preparer() {
        chatbotGestionnaireTools = new ChatbotGestionnaireTools(
                donneesEtudiantService,
                donneesOffreService,
                donneesCandidatureService,
                donneesProfesseurService,
                donneesEntrevueService,
                donneesContratService,
                donneesTemporellesService);
    }

    @Test
    void doitExposerLesOutilsPrincipaux() {
        ToolCallback[] result = ToolCallbacks.from(chatbotGestionnaireTools);

        assertThat(Arrays.stream(result).map(callback -> callback.getToolDefinition().name()).toList())
                .contains("compterEtudiantsSansCv", "obtenirEtudiantsInscrits", "compterCvParStatut", "consulterOffresAfficheesEntre",
                        "consulterContratsSignesEntre", "obtenirDateReference")
                .doesNotHaveDuplicates();
        for (ToolCallback callback : result) {
            assertThat(callback.getToolDefinition().description()).isNotBlank();
        }
    }

    @Test
    void doitCompterCvParStatut() throws ParametreDonneesInvalideException {
        when(donneesEtudiantService.compterCvParStatut(Statut.REFUSEE)).thenReturn(5L);

        long result = chatbotGestionnaireTools.compterCvParStatut(Statut.REFUSEE);

        assertThat(result).isEqualTo(5L);
        verify(donneesEtudiantService).compterCvParStatut(Statut.REFUSEE);
    }

    @Test
    void doitConsulterOffresAfficheesSelonLeMode() throws ParametreDonneesInvalideException {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        List<OffreDeStageDTO> mockList = List.of(OffreDeStageDTO.empty());
        when(donneesTemporellesService.compterOffresAfficheesEntre(jour, jour)).thenReturn(4L);
        when(donneesTemporellesService.obtenirOffresAfficheesEntre(jour, jour)).thenReturn(mockList);

        ResultatDonneesDTO nombre = chatbotGestionnaireTools.consulterOffresAfficheesEntre(jour, jour, ModeResultat.NOMBRE);

        assertThat(nombre.nombre()).isEqualTo(4L);
        assertThat(nombre.resultats()).isEmpty();
        verify(donneesTemporellesService).compterOffresAfficheesEntre(jour, jour);
        verify(donneesTemporellesService, never()).obtenirOffresAfficheesEntre(any(), any());

        ResultatDonneesDTO liste = chatbotGestionnaireTools.consulterOffresAfficheesEntre(jour, jour, ModeResultat.LISTE);

        assertThat(liste.resultats()).isEqualTo(mockList);
        assertThat(liste.nombre()).isNull();
        verify(donneesTemporellesService).obtenirOffresAfficheesEntre(jour, jour);
        verifyNoMoreInteractions(donneesTemporellesService);
    }

    @Test
    void doitConsulterOffresAvecFinAffichageApresEnNombreEtEnListe() throws ParametreDonneesInvalideException {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        List<OffreDeStageDTO> mockList = List.of(OffreDeStageDTO.empty());
        when(donneesTemporellesService.compterOffresAvecFinAffichageApres(jour)).thenReturn(3L);
        when(donneesTemporellesService.obtenirOffresAvecFinAffichageApres(jour)).thenReturn(mockList);

        ResultatDonneesDTO nombre = chatbotGestionnaireTools.consulterOffresAvecFinAffichageApres(jour, ModeResultat.NOMBRE);
        ResultatDonneesDTO liste = chatbotGestionnaireTools.consulterOffresAvecFinAffichageApres(jour, ModeResultat.LISTE);

        assertThat(nombre.nombre()).isEqualTo(3L);
        assertThat(liste.resultats()).isEqualTo(mockList);
        verify(donneesTemporellesService).compterOffresAvecFinAffichageApres(jour);
        verify(donneesTemporellesService).obtenirOffresAvecFinAffichageApres(jour);
        verifyNoMoreInteractions(donneesTemporellesService);
    }

    @Test
    void doitConsulterCandidaturesDeposeesEntreEnNombreEtEnListe() throws ParametreDonneesInvalideException {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        List<CandidatureDTO> mockList = List.of(mock(CandidatureDTO.class));
        when(donneesTemporellesService.compterCandidaturesDeposeesEntre(jour, jour)).thenReturn(2L);
        when(donneesTemporellesService.obtenirCandidaturesDeposeesEntre(jour, jour)).thenReturn(mockList);

        ResultatDonneesDTO nombre = chatbotGestionnaireTools.consulterCandidaturesDeposeesEntre(jour, jour, ModeResultat.NOMBRE);
        ResultatDonneesDTO liste = chatbotGestionnaireTools.consulterCandidaturesDeposeesEntre(jour, jour, ModeResultat.LISTE);

        assertThat(nombre.nombre()).isEqualTo(2L);
        assertThat(liste.resultats()).isEqualTo(mockList);
        verify(donneesTemporellesService).compterCandidaturesDeposeesEntre(jour, jour);
        verify(donneesTemporellesService).obtenirCandidaturesDeposeesEntre(jour, jour);
        verifyNoMoreInteractions(donneesTemporellesService);
    }

    @Test
    void doitConsulterCvDeposesEntreEnNombreEtEnListe() throws ParametreDonneesInvalideException {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        List<CvEtudiantDTO> mockList = List.of(mock(CvEtudiantDTO.class));
        when(donneesTemporellesService.compterCvDeposesEntre(jour, jour)).thenReturn(5L);
        when(donneesTemporellesService.obtenirCvDeposesEntre(jour, jour)).thenReturn(mockList);

        ResultatDonneesDTO nombre = chatbotGestionnaireTools.consulterCvDeposesEntre(jour, jour, ModeResultat.NOMBRE);
        ResultatDonneesDTO liste = chatbotGestionnaireTools.consulterCvDeposesEntre(jour, jour, ModeResultat.LISTE);

        assertThat(nombre.nombre()).isEqualTo(5L);
        assertThat(liste.resultats()).isEqualTo(mockList);
        verify(donneesTemporellesService).compterCvDeposesEntre(jour, jour);
        verify(donneesTemporellesService).obtenirCvDeposesEntre(jour, jour);
        verifyNoMoreInteractions(donneesTemporellesService);
    }

    @Test
    void doitConsulterEntrevuesEntreEnNombreEtEnListe() throws ParametreDonneesInvalideException {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        List<EntrevueDTO> mockList = List.of(mock(EntrevueDTO.class));
        when(donneesTemporellesService.compterEntrevuesEntre(jour, jour)).thenReturn(7L);
        when(donneesTemporellesService.obtenirEntrevuesEntre(jour, jour)).thenReturn(mockList);

        ResultatDonneesDTO nombre = chatbotGestionnaireTools.consulterEntrevuesEntre(jour, jour, ModeResultat.NOMBRE);
        ResultatDonneesDTO liste = chatbotGestionnaireTools.consulterEntrevuesEntre(jour, jour, ModeResultat.LISTE);

        assertThat(nombre.nombre()).isEqualTo(7L);
        assertThat(liste.resultats()).isEqualTo(mockList);
        verify(donneesTemporellesService).compterEntrevuesEntre(jour, jour);
        verify(donneesTemporellesService).obtenirEntrevuesEntre(jour, jour);
        verifyNoMoreInteractions(donneesTemporellesService);
    }

    @Test
    void doitConsulterContratsSignesEntreEnNombreEtEnListe() throws ParametreDonneesInvalideException {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        List<ResumeContratDTO> mockList = List.of(mock(ResumeContratDTO.class));
        when(donneesTemporellesService.compterContratsSignesEntre(SignataireContrat.EMPLOYEUR, jour, jour)).thenReturn(4L);
        when(donneesTemporellesService.obtenirContratsSignesEntre(SignataireContrat.EMPLOYEUR, jour, jour)).thenReturn(mockList);

        ResultatDonneesDTO nombre = chatbotGestionnaireTools.consulterContratsSignesEntre(SignataireContrat.EMPLOYEUR, jour, jour, ModeResultat.NOMBRE);
        ResultatDonneesDTO liste = chatbotGestionnaireTools.consulterContratsSignesEntre(SignataireContrat.EMPLOYEUR, jour, jour, ModeResultat.LISTE);

        assertThat(nombre.nombre()).isEqualTo(4L);
        assertThat(liste.resultats()).isEqualTo(mockList);
        verify(donneesTemporellesService).compterContratsSignesEntre(SignataireContrat.EMPLOYEUR, jour, jour);
        verify(donneesTemporellesService).obtenirContratsSignesEntre(SignataireContrat.EMPLOYEUR, jour, jour);
        verifyNoMoreInteractions(donneesTemporellesService);
    }

    @Test
    void doitRefuserUnModeAbsent() {
        LocalDate jour = LocalDate.of(2026, 11, 12);

        assertThatExceptionOfType(ParametreDonneesInvalideException.class)
                .isThrownBy(() -> chatbotGestionnaireTools.consulterOffresAfficheesEntre(jour, jour, null))
                .withMessage("chatbot.errors.mode_required");
        assertThatExceptionOfType(ParametreDonneesInvalideException.class)
                .isThrownBy(() -> chatbotGestionnaireTools.consulterOffresAvecFinAffichageApres(jour, null))
                .withMessage("chatbot.errors.mode_required");
        assertThatExceptionOfType(ParametreDonneesInvalideException.class)
                .isThrownBy(() -> chatbotGestionnaireTools.consulterCandidaturesDeposeesEntre(jour, jour, null))
                .withMessage("chatbot.errors.mode_required");
        assertThatExceptionOfType(ParametreDonneesInvalideException.class)
                .isThrownBy(() -> chatbotGestionnaireTools.consulterCvDeposesEntre(jour, jour, null))
                .withMessage("chatbot.errors.mode_required");
        assertThatExceptionOfType(ParametreDonneesInvalideException.class)
                .isThrownBy(() -> chatbotGestionnaireTools.consulterEntrevuesEntre(jour, jour, null))
                .withMessage("chatbot.errors.mode_required");
        assertThatExceptionOfType(ParametreDonneesInvalideException.class)
                .isThrownBy(() -> chatbotGestionnaireTools.consulterContratsSignesEntre(SignataireContrat.ETUDIANT, jour, jour, null))
                .withMessage("chatbot.errors.mode_required");
        verifyNoInteractions(donneesTemporellesService);
    }

    @Test
    void doitObtenirLesOffresParDatesEtLaReferenceTemporelle() throws ParametreDonneesInvalideException {
        LocalDate debut = LocalDate.of(2026, 11, 9);
        LocalDate fin = LocalDate.of(2026, 11, 15);
        List<OffreDeStageDTO> mockList = List.of(OffreDeStageDTO.empty());
        ReferenceTemporelleDTO mockRef = mock(ReferenceTemporelleDTO.class);
        when(donneesTemporellesService.obtenirOffresExpirantEntre(debut, fin)).thenReturn(mockList);
        when(donneesTemporellesService.obtenirOffresAvecDebutStageEntre(debut, fin)).thenReturn(mockList);
        when(donneesTemporellesService.obtenirDateReference()).thenReturn(mockRef);

        List<OffreDeStageDTO> offresExpirant = chatbotGestionnaireTools.obtenirOffresExpirantEntre(debut, fin);
        List<OffreDeStageDTO> offresDebutant = chatbotGestionnaireTools.obtenirOffresAvecDebutStageEntre(debut, fin);
        ReferenceTemporelleDTO reference = chatbotGestionnaireTools.obtenirDateReference();

        assertThat(offresExpirant).isEqualTo(mockList);
        assertThat(offresDebutant).isEqualTo(mockList);
        assertThat(reference).isEqualTo(mockRef);
        verify(donneesTemporellesService).obtenirOffresExpirantEntre(debut, fin);
        verify(donneesTemporellesService).obtenirOffresAvecDebutStageEntre(debut, fin);
        verify(donneesTemporellesService).obtenirDateReference();
    }

    @Test
    void doitConvertirLesDatesEtEnumsDuJsonSpringAi() throws ParametreDonneesInvalideException {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        when(donneesTemporellesService.compterContratsSignesEntre(SignataireContrat.GESTIONNAIRE, jour, jour)).thenReturn(6L);
        ToolCallback callback = Arrays.stream(ToolCallbacks.from(chatbotGestionnaireTools))
                .filter(tool -> tool.getToolDefinition().name().equals("consulterContratsSignesEntre"))
                .findFirst().orElseThrow();

        String result = callback.call("""
                {"signataire":"GESTIONNAIRE","debut":"2026-11-12","fin":"2026-11-12","mode":"NOMBRE"}
                """);

        assertThat(result).contains("\"nombre\":6");
        verify(donneesTemporellesService).compterContratsSignesEntre(SignataireContrat.GESTIONNAIRE, jour, jour);
    }

    @Test
    void doitTransmettreLesComptagesEtudiantsEtLeDepartement() throws Exception {
        when(donneesEtudiantService.compterEtudiantsSansCv()).thenReturn(3L);
        when(donneesEtudiantService.compterEtudiantsSansStage()).thenReturn(4L);
        when(donneesEtudiantService.compterEtudiantsInscrits()).thenReturn(15L);
        when(donneesEtudiantService.compterEtudiantsAvecStage()).thenReturn(6L);
        when(donneesEtudiantService.compterEtudiantsParDepartement(Departement.INFORMATIQUE)).thenReturn(8L);

        assertThat(chatbotGestionnaireTools.compterEtudiantsSansCv()).isEqualTo(3L);
        assertThat(chatbotGestionnaireTools.compterEtudiantsSansStage()).isEqualTo(4L);
        assertThat(chatbotGestionnaireTools.compterEtudiantsInscrits()).isEqualTo(15L);
        assertThat(chatbotGestionnaireTools.compterEtudiantsAvecStage()).isEqualTo(6L);
        assertThat(chatbotGestionnaireTools.compterEtudiantsParDepartement(Departement.INFORMATIQUE)).isEqualTo(8L);
    }

    @Test
    void doitRetournerLesListesEtudiants() throws Exception {
        List<EtudiantDTO> resultatInscrits = List.of(mock(EtudiantDTO.class));
        when(donneesEtudiantService.obtenirEtudiantsInscrits()).thenReturn(resultatInscrits);

        List<EtudiantDTO> resultat1 = List.of(mock(EtudiantDTO.class));
        when(donneesEtudiantService.obtenirEtudiantsSansCv()).thenReturn(resultat1);
        List<EtudiantDTO> resultat2 = List.of(mock(EtudiantDTO.class));
        when(donneesEtudiantService.obtenirEtudiantsSansStage()).thenReturn(resultat2);
        List<EtudiantDTO> resultat3 = List.of(mock(EtudiantDTO.class));
        when(donneesEtudiantService.obtenirEtudiantsAvecStage()).thenReturn(resultat3);
        List<EtudiantDTO> resultat4 = List.of(mock(EtudiantDTO.class));
        when(donneesEtudiantService.obtenirEtudiantsSansProfesseur()).thenReturn(resultat4);
        List<CvEtudiantDTO> resultat5 = List.of(mock(CvEtudiantDTO.class));
        when(donneesEtudiantService.obtenirCvEnAttente()).thenReturn(resultat5);
        List<EtudiantDTO> resultat6 = List.of(mock(EtudiantDTO.class));
        when(donneesEtudiantService.obtenirEtudiantsAvecCvRefuse()).thenReturn(resultat6);
        List<EtudiantDTO> resultat7 = List.of(mock(EtudiantDTO.class));
        when(donneesEtudiantService.obtenirEtudiantsSansEntrevue()).thenReturn(resultat7);
        List<EtudiantDTO> resultat8 = List.of(mock(EtudiantDTO.class));
        when(donneesEtudiantService.obtenirEtudiantsParProfesseur(42L)).thenReturn(resultat8);

        assertThat(chatbotGestionnaireTools.obtenirEtudiantsInscrits()).isEqualTo(resultatInscrits);
        assertThat(chatbotGestionnaireTools.obtenirEtudiantsSansCv()).isEqualTo(resultat1);
        assertThat(chatbotGestionnaireTools.obtenirEtudiantsSansStage()).isEqualTo(resultat2);
        assertThat(chatbotGestionnaireTools.obtenirEtudiantsAvecStage()).isEqualTo(resultat3);
        assertThat(chatbotGestionnaireTools.obtenirEtudiantsSansProfesseur()).isEqualTo(resultat4);
        assertThat(chatbotGestionnaireTools.obtenirCvEnAttente()).isEqualTo(resultat5);
        assertThat(chatbotGestionnaireTools.obtenirEtudiantsAvecCvRefuse()).isEqualTo(resultat6);
        assertThat(chatbotGestionnaireTools.obtenirEtudiantsSansEntrevue()).isEqualTo(resultat7);
        assertThat(chatbotGestionnaireTools.obtenirEtudiantsParProfesseur(42L)).isEqualTo(resultat8);
    }

    @Test
    void doitRetournerLesListesProfesseurs() throws Exception {
        List<ProfesseurDTO> resultat1 = List.of(mock(ProfesseurDTO.class));
        when(donneesProfesseurService.obtenirProfesseurs()).thenReturn(resultat1);
        List<ProfesseurDTO> resultat2 = List.of(mock(ProfesseurDTO.class));
        when(donneesProfesseurService.obtenirProfesseursSansEtudiants()).thenReturn(resultat2);

        assertThat(chatbotGestionnaireTools.obtenirProfesseurs()).isEqualTo(resultat1);
        assertThat(chatbotGestionnaireTools.obtenirProfesseursSansEtudiants()).isEqualTo(resultat2);
    }

    @Test
    void doitRetournerLesListesOffres() throws Exception {
        List<OffreDeStageDTO> resultat1 = List.of(mock(OffreDeStageDTO.class));
        when(donneesOffreService.obtenirOffresEnAttente()).thenReturn(resultat1);
        List<OffreDeStageDTO> resultat2 = List.of(mock(OffreDeStageDTO.class));
        when(donneesOffreService.obtenirOffresApprouvees()).thenReturn(resultat2);

        assertThat(chatbotGestionnaireTools.obtenirOffresEnAttente()).isEqualTo(resultat1);
        assertThat(chatbotGestionnaireTools.obtenirOffresApprouvees()).isEqualTo(resultat2);
    }

    @Test
    void doitRetournerLesListesCandidatures() throws Exception {
        List<CandidatureDTO> resultat1 = List.of(mock(CandidatureDTO.class));
        List<CandidatureDTO> resultat2 = List.of(mock(CandidatureDTO.class));
        when(donneesCandidatureService.obtenirCandidaturesParStatut(StatutCandidature.CONTRAT_CREE)).thenReturn(resultat2);

        assertThat(chatbotGestionnaireTools.obtenirCandidaturesParStatut(StatutCandidature.CONTRAT_CREE)).isEqualTo(resultat2);
    }

    @Test
    void doitRetournerLesContratsEnAttenteDeSignature() throws Exception {
        List<ResumeContratDTO> resultat1 = List.of(mock(ResumeContratDTO.class));
        when(donneesContratService.obtenirContratsNonSignes()).thenReturn(resultat1);
        List<ResumeContratDTO> resultat2 = List.of(mock(ResumeContratDTO.class));
        when(donneesContratService.obtenirContratsASignerGestionnaire()).thenReturn(resultat2);

        assertThat(chatbotGestionnaireTools.obtenirContratsNonSignes()).isEqualTo(resultat1);
        assertThat(chatbotGestionnaireTools.obtenirContratsASignerGestionnaire()).isEqualTo(resultat2);
    }

    @Test
    void doitTransmettreLesStatutsOffreCandidatureEtEntrevue() throws Exception {
        when(donneesOffreService.compterOffresParStatut(Statut.EN_ATTENTE)).thenReturn(2L);
        when(donneesCandidatureService.compterCandidaturesParStatut(StatutCandidature.SOUMISE)).thenReturn(7L);
        when(donneesEntrevueService.compterEntrevuesParStatut(StatutEntrevue.CONFIRMEE)).thenReturn(3L);
        List<EntrevueDTO> mockList = List.of(mock(EntrevueDTO.class));
        when(donneesEntrevueService.obtenirEntrevuesAVenir()).thenReturn(mockList);

        assertThat(chatbotGestionnaireTools.compterOffresParStatut(Statut.EN_ATTENTE)).isEqualTo(2L);
        assertThat(chatbotGestionnaireTools.compterCandidaturesParStatut(StatutCandidature.SOUMISE)).isEqualTo(7L);
        assertThat(chatbotGestionnaireTools.compterEntrevuesParStatut(StatutEntrevue.CONFIRMEE)).isEqualTo(3L);
        assertThat(chatbotGestionnaireTools.obtenirEntrevuesAVenir()).isEqualTo(mockList);
    }
}