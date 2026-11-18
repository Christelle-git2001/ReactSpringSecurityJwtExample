package com.lacouf.rsbjwt.service.chatbot;

import com.lacouf.rsbjwt.service.NotificationService;
import com.lacouf.rsbjwt.service.ProfesseurService;
import com.lacouf.rsbjwt.service.chatbot.tools.ChatbotProfesseurTools;
import com.lacouf.rsbjwt.service.dto.*;
import com.lacouf.rsbjwt.service.dto.candidature.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.EntrevueDTO;
import com.lacouf.rsbjwt.service.dto.notification.NotificationDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EtudiantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.lacouf.rsbjwt.exception.utilisateur.UtilisateurIntrouvableException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatbotProfesseurToolsTest {

    private ProfesseurService professeurService;
    private NotificationService notificationService;
    private ChatbotProfesseurTools tools;

    @BeforeEach
    void setUp() {
        professeurService = mock(ProfesseurService.class);
        notificationService = mock(NotificationService.class);

        tools = new ChatbotProfesseurTools(
                professeurService,
                notificationService
        );
    }

    @Test
    void doitRetournerLesEtudiantsAssignes() throws Exception {
        List<EtudiantDTO> etudiants = List.of(
                mock(EtudiantDTO.class),
                mock(EtudiantDTO.class)
        );

        when(professeurService.getEtudiantsAssignes())
                .thenReturn(etudiants);

        List<EtudiantDTO> resultat =
                tools.obtenirMesEtudiantsAssignes();

        assertEquals(2, resultat.size());
        assertEquals(etudiants, resultat);

        verify(professeurService).getEtudiantsAssignes();
    }

    @Test
    void doitRetournerUneListeVideSiAucunEtudiantAssigne()
            throws Exception {
        when(professeurService.getEtudiantsAssignes())
                .thenReturn(List.of());

        List<EtudiantDTO> resultat =
                tools.obtenirMesEtudiantsAssignes();

        assertNotNull(resultat);
        assertTrue(resultat.isEmpty());

        verify(professeurService).getEtudiantsAssignes();
    }

    @Test
    void doitPropagerExceptionSiImpossibleDeTrouverLeProfesseur()
            throws Exception {
        when(professeurService.getEtudiantsAssignes())
                .thenThrow(new UtilisateurIntrouvableException());

        assertThrows(
                UtilisateurIntrouvableException.class,
                () -> tools.obtenirMesEtudiantsAssignes()
        );

        verify(professeurService).getEtudiantsAssignes();
    }

    @Test
    void doitRetournerLesCandidaturesDeMesEtudiants()
            throws Exception {
        List<CandidatureDTO> candidatures = List.of(
                mock(CandidatureDTO.class),
                mock(CandidatureDTO.class)
        );

        when(professeurService.getCandidaturesDeMesEtudiants())
                .thenReturn(candidatures);

        List<CandidatureDTO> resultat =
                tools.obtenirCandidaturesDeMesEtudiants();

        assertEquals(2, resultat.size());
        assertEquals(candidatures, resultat);

        verify(professeurService).getCandidaturesDeMesEtudiants();
    }

    @Test
    void doitRetournerUneListeVideSiAucuneCandidature()
            throws Exception {
        when(professeurService.getCandidaturesDeMesEtudiants())
                .thenReturn(List.of());

        List<CandidatureDTO> resultat =
                tools.obtenirCandidaturesDeMesEtudiants();

        assertNotNull(resultat);
        assertTrue(resultat.isEmpty());

        verify(professeurService).getCandidaturesDeMesEtudiants();
    }

    @Test
    void doitPropagerExceptionLorsDeLaRechercheDesCandidatures()
            throws Exception {
        when(professeurService.getCandidaturesDeMesEtudiants())
                .thenThrow(new UtilisateurIntrouvableException());

        assertThrows(
                UtilisateurIntrouvableException.class,
                () -> tools.obtenirCandidaturesDeMesEtudiants()
        );

        verify(professeurService).getCandidaturesDeMesEtudiants();
    }

    @Test
    void doitRetournerLesEntrevuesDeMesEtudiants()
            throws Exception {
        List<EntrevueDTO> entrevues = List.of(
                mock(EntrevueDTO.class),
                mock(EntrevueDTO.class)
        );

        when(professeurService.getEntrevuesDeMesEtudiants())
                .thenReturn(entrevues);

        List<EntrevueDTO> resultat =
                tools.obtenirEntrevuesDeMesEtudiants();

        assertEquals(2, resultat.size());
        assertEquals(entrevues, resultat);

        verify(professeurService).getEntrevuesDeMesEtudiants();
    }

    @Test
    void doitRetournerUneListeVideSiAucuneEntrevue()
            throws Exception {
        when(professeurService.getEntrevuesDeMesEtudiants())
                .thenReturn(List.of());

        List<EntrevueDTO> resultat =
                tools.obtenirEntrevuesDeMesEtudiants();

        assertNotNull(resultat);
        assertTrue(resultat.isEmpty());

        verify(professeurService).getEntrevuesDeMesEtudiants();
    }

    @Test
    void doitPropagerExceptionLorsDeLaRechercheDesEntrevues()
            throws Exception {
        when(professeurService.getEntrevuesDeMesEtudiants())
                .thenThrow(new UtilisateurIntrouvableException());

        assertThrows(
                UtilisateurIntrouvableException.class,
                () -> tools.obtenirEntrevuesDeMesEtudiants()
        );

        verify(professeurService).getEntrevuesDeMesEtudiants();
    }

    @Test
    void doitRetournerLesStagesDeMesEtudiants()
            throws Exception {
        List<StageEtudiantDTO> stages = List.of(
                mock(StageEtudiantDTO.class),
                mock(StageEtudiantDTO.class)
        );

        when(professeurService.getStagesDeMesEtudiants())
                .thenReturn(stages);

        List<StageEtudiantDTO> resultat =
                tools.obtenirStagesDeMesEtudiants();

        assertEquals(2, resultat.size());
        assertEquals(stages, resultat);

        verify(professeurService).getStagesDeMesEtudiants();
    }

    @Test
    void doitRetournerUneListeVideSiAucunStage()
            throws Exception {
        when(professeurService.getStagesDeMesEtudiants())
                .thenReturn(List.of());

        List<StageEtudiantDTO> resultat =
                tools.obtenirStagesDeMesEtudiants();

        assertNotNull(resultat);
        assertTrue(resultat.isEmpty());

        verify(professeurService).getStagesDeMesEtudiants();
    }

    @Test
    void doitPropagerExceptionLorsDeLaRechercheDesStages()
            throws Exception {
        when(professeurService.getStagesDeMesEtudiants())
                .thenThrow(new UtilisateurIntrouvableException());

        assertThrows(
                UtilisateurIntrouvableException.class,
                () -> tools.obtenirStagesDeMesEtudiants()
        );

        verify(professeurService).getStagesDeMesEtudiants();
    }

    @Test
    void doitRetournerLesNotificationsNonLues()
            throws Exception {
        List<NotificationDTO> notifications = List.of(
                mock(NotificationDTO.class),
                mock(NotificationDTO.class)
        );

        when(notificationService.getNotificationsNonLuesProfesseur())
                .thenReturn(notifications);

        List<NotificationDTO> resultat =
                tools.consulterMesNotificationsNonLues();

        assertEquals(2, resultat.size());
        assertEquals(notifications, resultat);

        verify(notificationService)
                .getNotificationsNonLuesProfesseur();
    }

    @Test
    void doitRetournerUneListeVideSiAucuneNotificationNonLue()
            throws Exception {
        when(notificationService.getNotificationsNonLuesProfesseur())
                .thenReturn(List.of());

        List<NotificationDTO> resultat =
                tools.consulterMesNotificationsNonLues();

        assertNotNull(resultat);
        assertTrue(resultat.isEmpty());

        verify(notificationService)
                .getNotificationsNonLuesProfesseur();
    }

    @Test
    void doitPropagerExceptionLorsDeLaRechercheDesNotifications()
            throws Exception {
        when(notificationService.getNotificationsNonLuesProfesseur())
                .thenThrow(new UtilisateurIntrouvableException());

        assertThrows(
                UtilisateurIntrouvableException.class,
                () -> tools.consulterMesNotificationsNonLues()
        );

        verify(notificationService)
                .getNotificationsNonLuesProfesseur();
    }
}

