package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.service.donnees.DonneesCandidatureService;
import com.lacouf.rsbjwt.service.donnees.DonneesEntrevueService;
import com.lacouf.rsbjwt.service.donnees.DonneesEtudiantService;
import com.lacouf.rsbjwt.service.donnees.DonneesOffreService;
import com.lacouf.rsbjwt.service.dto.offre.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.cv.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.candidature.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.EntrevueDTO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RapportServiceTest {

    @Mock
    private DonneesOffreService donneesOffreService;

    @Mock
    private DonneesEtudiantService donneesEtudiantService;

    @Mock
    private DonneesCandidatureService donneesCandidatureService;

    @Mock
    private DonneesEntrevueService donneesEntrevueService;

    @InjectMocks
    private RapportService rapportService;


    @Test
    void obtenirOffresEnAttente_retourneOffresDuService() {
        List<OffreDeStageDTO> offres = List.of(mock(OffreDeStageDTO.class));

        when(donneesOffreService.obtenirOffresEnAttente())
                .thenReturn(offres);

        List<OffreDeStageDTO> resultat =
                rapportService.obtenirOffresEnAttente();

        assertEquals(offres, resultat);

        verify(donneesOffreService).obtenirOffresEnAttente();
    }

    @Test
    void obtenirOffresApprouvees_retourneOffresDuService() {
        List<OffreDeStageDTO> offres = List.of(mock(OffreDeStageDTO.class));

        when(donneesOffreService.obtenirOffresApprouvees())
                .thenReturn(offres);

        List<OffreDeStageDTO> resultat =
                rapportService.obtenirOffresApprouvees();

        assertEquals(offres, resultat);

        verify(donneesOffreService).obtenirOffresApprouvees();
    }

    @Test
    void obtenirEtudiantsInscrits_retourneEtudiantsDuService() {
        List<EtudiantDTO> etudiants = List.of(mock(EtudiantDTO.class));

        when(donneesEtudiantService.obtenirEtudiantsInscrits())
                .thenReturn(etudiants);

        List<EtudiantDTO> resultat =
                rapportService.obtenirEtudiantsInscrits();

        assertEquals(etudiants, resultat);

        verify(donneesEtudiantService).obtenirEtudiantsInscrits();
    }

    @Test
    void obtenirEtudiantsSansCv_retourneEtudiantsDuService() {
        List<EtudiantDTO> etudiants = List.of(mock(EtudiantDTO.class));

        when(donneesEtudiantService.obtenirEtudiantsSansCv())
                .thenReturn(etudiants);

        List<EtudiantDTO> resultat =
                rapportService.obtenirEtudiantsSansCv();

        assertEquals(etudiants, resultat);

        verify(donneesEtudiantService).obtenirEtudiantsSansCv();
    }

    @Test
    void obtenirCvEnAttente_retourneCvDuService() {
        List<CvEtudiantDTO> cvs = List.of(mock(CvEtudiantDTO.class));

        when(donneesEtudiantService.obtenirCvEnAttente())
                .thenReturn(cvs);

        List<CvEtudiantDTO> resultat =
                rapportService.obtenirCvEnAttente();

        assertEquals(cvs, resultat);

        verify(donneesEtudiantService).obtenirCvEnAttente();
    }

    @Test
    void obtenirEtudiantsSansEntrevue_retourneEtudiantsDuService() {
        List<EtudiantDTO> etudiants = List.of(mock(EtudiantDTO.class));

        when(donneesEtudiantService.obtenirEtudiantsSansEntrevue())
                .thenReturn(etudiants);

        List<EtudiantDTO> resultat =
                rapportService.obtenirEtudiantsSansEntrevue();

        assertEquals(etudiants, resultat);

        verify(donneesEtudiantService).obtenirEtudiantsSansEntrevue();
    }

    @Test
    void obtenirEtudiantsAyantTrouveStage_retourneEtudiantsDuService() {
        List<EtudiantDTO> etudiants = List.of(mock(EtudiantDTO.class));

        when(donneesEtudiantService.obtenirEtudiantsAyantTrouveStage())
                .thenReturn(etudiants);

        List<EtudiantDTO> resultat =
                rapportService.obtenirEtudiantsAyantTrouveStage();

        assertEquals(etudiants, resultat);

        verify(donneesEtudiantService).obtenirEtudiantsAyantTrouveStage();
    }


    @Test
    void obtenirCandidaturesEnAttenteEntrevue_retourneCandidaturesDuService() {
        List<CandidatureDTO> candidatures =
                List.of(mock(CandidatureDTO.class));

        when(donneesCandidatureService.obtenirCandidaturesEnAttenteEntrevue())
                .thenReturn(candidatures);

        List<CandidatureDTO> resultat =
                rapportService.obtenirCandidaturesEnAttenteEntrevue();

        assertEquals(candidatures, resultat);

        verify(donneesCandidatureService)
                .obtenirCandidaturesEnAttenteEntrevue();
    }

    @Test
    void obtenirCandidaturesSansEvaluation_retourneCandidaturesDuService() {
        List<CandidatureDTO> candidatures =
                List.of(mock(CandidatureDTO.class));

        when(donneesCandidatureService.obtenirCandidaturesSansEvaluation())
                .thenReturn(candidatures);

        List<CandidatureDTO> resultat =
                rapportService.obtenirCandidaturesSansEvaluation();

        assertEquals(candidatures, resultat);

        verify(donneesCandidatureService)
                .obtenirCandidaturesSansEvaluation();
    }


    @Test
    void obtenirEntrevuesEnAttenteReponse_retourneEntrevuesDuService() {
        List<EntrevueDTO> entrevues =
                List.of(mock(EntrevueDTO.class));

        when(donneesEntrevueService.obtenirEntrevuesEnAttenteReponse())
                .thenReturn(entrevues);

        List<EntrevueDTO> resultat =
                rapportService.obtenirEntrevuesEnAttenteReponse();

        assertEquals(entrevues, resultat);

        verify(donneesEntrevueService)
                .obtenirEntrevuesEnAttenteReponse();
    }
}