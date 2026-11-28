package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.RapportService;
import com.lacouf.rsbjwt.service.dto.offre.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.cv.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.candidature.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.EntrevueDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RapportControllerTest {

    private RapportService rapportService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        rapportService = mock(RapportService.class);

        RapportController rapportController =
                new RapportController(rapportService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(rapportController)
                .build();
    }


    @Test
    void obtenirOffresEnAttente_retourne200() throws Exception {
        when(rapportService.obtenirOffresEnAttente())
                .thenReturn(List.of(mock(OffreDeStageDTO.class)));

        mockMvc.perform(get("/gestionnaire/rapports/offres/en-attente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(rapportService).obtenirOffresEnAttente();
    }

    @Test
    void obtenirOffresValidees_retourne200() throws Exception {
        when(rapportService.obtenirOffresApprouvees())
                .thenReturn(List.of(mock(OffreDeStageDTO.class)));

        mockMvc.perform(get("/gestionnaire/rapports/offres/validees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(rapportService).obtenirOffresApprouvees();
    }


    @Test
    void obtenirEtudiantsInscrits_retourne200() throws Exception {
        when(rapportService.obtenirEtudiantsInscrits())
                .thenReturn(List.of(mock(EtudiantDTO.class)));

        mockMvc.perform(get("/gestionnaire/rapports/etudiants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(rapportService).obtenirEtudiantsInscrits();
    }

    @Test
    void obtenirEtudiantsSansCv_retourne200() throws Exception {
        when(rapportService.obtenirEtudiantsSansCv())
                .thenReturn(List.of(mock(EtudiantDTO.class)));

        mockMvc.perform(get("/gestionnaire/rapports/etudiants/sans-cv")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(rapportService).obtenirEtudiantsSansCv();
    }

    @Test
    void obtenirCvEnAttente_retourne200() throws Exception {
        when(rapportService.obtenirCvEnAttente())
                .thenReturn(List.of(mock(CvEtudiantDTO.class)));

        mockMvc.perform(get("/gestionnaire/rapports/cvs/en-attente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(rapportService).obtenirCvEnAttente();
    }

    @Test
    void obtenirEtudiantsSansEntrevue_retourne200() throws Exception {
        when(rapportService.obtenirEtudiantsSansEntrevue())
                .thenReturn(List.of(mock(EtudiantDTO.class)));

        mockMvc.perform(get("/gestionnaire/rapports/etudiants/sans-entrevue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(rapportService).obtenirEtudiantsSansEntrevue();
    }

    @Test
    void obtenirEtudiantsAyantTrouveStage_retourne200() throws Exception {
        when(rapportService.obtenirEtudiantsAyantTrouveStage())
                .thenReturn(List.of(mock(EtudiantDTO.class)));

        mockMvc.perform(get("/gestionnaire/rapports/etudiants/stage-trouve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(rapportService).obtenirEtudiantsAyantTrouveStage();
    }


    @Test
    void obtenirCandidaturesEnAttenteEntrevue_retourne200() throws Exception {
        when(rapportService.obtenirCandidaturesEnAttenteEntrevue())
                .thenReturn(List.of(mock(CandidatureDTO.class)));

        mockMvc.perform(get("/gestionnaire/rapports/candidatures/en-attente-entrevue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(rapportService)
                .obtenirCandidaturesEnAttenteEntrevue();
    }

    @Test
    void obtenirCandidaturesSansEvaluation_retourne200() throws Exception {
        when(rapportService.obtenirCandidaturesSansEvaluation())
                .thenReturn(List.of(mock(CandidatureDTO.class)));

        mockMvc.perform(get("/gestionnaire/rapports/candidatures/sans-evaluation")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(rapportService)
                .obtenirCandidaturesSansEvaluation();
    }


    @Test
    void obtenirEntrevuesEnAttenteReponse_retourne200() throws Exception {
        when(rapportService.obtenirEntrevuesEnAttenteReponse())
                .thenReturn(List.of(mock(EntrevueDTO.class)));

        mockMvc.perform(get("/gestionnaire/rapports/entrevues/en-attente-reponse")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(rapportService)
                .obtenirEntrevuesEnAttenteReponse();
    }

    // --------------------------------------------------
    // DONNÉES VIDES
    // --------------------------------------------------

    @Test
    void obtenirOffresEnAttente_sansDonnees_retourneListeVide() throws Exception {
        when(rapportService.obtenirOffresEnAttente())
                .thenReturn(List.of());

        mockMvc.perform(get("/gestionnaire/rapports/offres/en-attente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void obtenirEtudiantsInscrits_sansDonnees_retourneListeVide() throws Exception {
        when(rapportService.obtenirEtudiantsInscrits())
                .thenReturn(List.of());

        mockMvc.perform(get("/gestionnaire/rapports/etudiants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void doitRetournerCandidaturesSansEvaluationMilieuStageAvecSucces() throws Exception {
        when(rapportService.obtenirCandidaturesSansEvaluationMilieuStage())
                .thenReturn(List.of());

        mockMvc.perform(get("/gestionnaire/rapports/candidatures/sans-evaluation-milieu-stage"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));

        verify(rapportService).obtenirCandidaturesSansEvaluationMilieuStage();
    }
}