package com.lacouf.rsbjwt.service.chatbot;


import com.lacouf.rsbjwt.service.EtudiantService;
import com.lacouf.rsbjwt.service.NotificationService;
import com.lacouf.rsbjwt.service.chatbot.tools.ChatbotEtudiantTools;
import com.lacouf.rsbjwt.exception.utilisateur.EtudiantIntrouvableException;
import com.lacouf.rsbjwt.exception.cv.CurriculumVitaeNonApprouveException;
import  com.lacouf.rsbjwt.exception.cv.CurriculumVitaeIntrouvable ;

import com.lacouf.rsbjwt.service.dto.candidature.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.cv.MonCvTeleverseDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.EntrevueDTO;
import com.lacouf.rsbjwt.service.dto.notification.NotificationDTO;
import com.lacouf.rsbjwt.service.dto.offre.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.MonProfesseurAssigneDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatbotEtudiantToolsTest {

    private EtudiantService etudiantService;
    private NotificationService notificationService;
    private ChatbotEtudiantTools tools;

    @BeforeEach
    void setUp() {
        etudiantService = mock(EtudiantService.class);
        notificationService = mock(NotificationService.class);

        tools = new ChatbotEtudiantTools(
                etudiantService,
                notificationService
        );
    }

    @Test
    void doitRetournerMonCv() throws Exception {
        MonCvTeleverseDTO cv = mock(MonCvTeleverseDTO.class);

        when(etudiantService.getMonCv()).thenReturn(cv);

        MonCvTeleverseDTO resultat = tools.consulterMonCv();

        assertNotNull(resultat);
        assertEquals(cv, resultat);

        verify(etudiantService).getMonCv();
    }

    @Test
    void doitPropagerExceptionSiEtudiantIntrouvablePourMonCv()
            throws Exception {
        when(etudiantService.getMonCv())
                .thenThrow(mock(EtudiantIntrouvableException.class));

        assertThrows(
                EtudiantIntrouvableException.class,
                () -> tools.consulterMonCv()
        );

        verify(etudiantService).getMonCv();
    }

    @Test
    void doitRetournerMesOffresNonPostulees() throws Exception {
        List<OffreDeStageDTO> offres = List.of(
                mock(OffreDeStageDTO.class),
                mock(OffreDeStageDTO.class)
        );

        when(etudiantService.getOffresNonPostulees())
                .thenReturn(offres);

        List<OffreDeStageDTO> resultat =
                tools.obtenirMesOffresNonPostulees();

        assertEquals(2, resultat.size());
        assertEquals(offres, resultat);

        verify(etudiantService).getOffresNonPostulees();
    }

    @Test
    void doitRetournerUneListeVideSiAucuneOffreNonPostulee()
            throws Exception {
        when(etudiantService.getOffresNonPostulees())
                .thenReturn(List.of());

        List<OffreDeStageDTO> resultat =
                tools.obtenirMesOffresNonPostulees();

        assertNotNull(resultat);
        assertTrue(resultat.isEmpty());

        verify(etudiantService).getOffresNonPostulees();
    }

    @Test
    void doitPropagerExceptionSiEtudiantIntrouvablePourLesOffres()
            throws Exception {
        when(etudiantService.getOffresNonPostulees())
                .thenThrow(mock(EtudiantIntrouvableException.class));

        assertThrows(
                EtudiantIntrouvableException.class,
                () -> tools.obtenirMesOffresNonPostulees()
        );
    }

    @Test
    void doitPropagerExceptionSiCvNonApprouvePourLesOffres()
            throws Exception {
        when(etudiantService.getOffresNonPostulees())
                .thenThrow(mock(CurriculumVitaeNonApprouveException.class));

        assertThrows(
                CurriculumVitaeNonApprouveException.class,
                () -> tools.obtenirMesOffresNonPostulees()
        );
    }

    @Test
    void doitPropagerExceptionSiCvIntrouvablePourLesOffres()
            throws Exception {
        when(etudiantService.getOffresNonPostulees())
                .thenThrow(mock(CurriculumVitaeIntrouvable.class));

        assertThrows(
                com.lacouf.rsbjwt.exception.cv.CurriculumVitaeIntrouvable.class,
                () -> tools.obtenirMesOffresNonPostulees()
        );
    }

    @Test
    void doitRetournerLesNouvellesOffresDepuisDerniereVisite()
            throws Exception {
        List<OffreDeStageDTO> offres = List.of(
                mock(OffreDeStageDTO.class),
                mock(OffreDeStageDTO.class)
        );

        when(etudiantService.getNouvellesOffresDepuisDerniereVisite())
                .thenReturn(offres);

        List<OffreDeStageDTO> resultat =
                tools.obtenirNouvellesOffresDepuisDerniereVisite();

        assertEquals(2, resultat.size());
        assertEquals(offres, resultat);

        verify(etudiantService)
                .getNouvellesOffresDepuisDerniereVisite();
    }

    @Test
    void doitRetournerUneListeVideSiAucuneNouvelleOffre()
            throws Exception {
        when(etudiantService.getNouvellesOffresDepuisDerniereVisite())
                .thenReturn(List.of());

        List<OffreDeStageDTO> resultat =
                tools.obtenirNouvellesOffresDepuisDerniereVisite();

        assertNotNull(resultat);
        assertTrue(resultat.isEmpty());

        verify(etudiantService)
                .getNouvellesOffresDepuisDerniereVisite();
    }

    @Test
    void doitPropagerExceptionSiEtudiantIntrouvablePourNouvellesOffres()
            throws Exception {
        when(etudiantService.getNouvellesOffresDepuisDerniereVisite())
                .thenThrow(mock(EtudiantIntrouvableException.class));

        assertThrows(
                EtudiantIntrouvableException.class,
                () -> tools.obtenirNouvellesOffresDepuisDerniereVisite()
        );
    }

    @Test
    void doitPropagerExceptionSiCvNonApprouvePourNouvellesOffres()
            throws Exception {
        when(etudiantService.getNouvellesOffresDepuisDerniereVisite())
                .thenThrow(mock(CurriculumVitaeNonApprouveException.class));

        assertThrows(
                CurriculumVitaeNonApprouveException.class,
                () -> tools.obtenirNouvellesOffresDepuisDerniereVisite()
        );
    }

    @Test
    void doitPropagerExceptionSiCvIntrouvablePourNouvellesOffres()
            throws Exception {
        when(etudiantService.getNouvellesOffresDepuisDerniereVisite())
                .thenThrow(mock(CurriculumVitaeIntrouvable.class));

        assertThrows(
                CurriculumVitaeIntrouvable.class,
                () -> tools.obtenirNouvellesOffresDepuisDerniereVisite()
        );
    }

    @Test
    void doitRetournerMesCandidatures() throws Exception {
        List<CandidatureDTO> candidatures = List.of(
                mock(CandidatureDTO.class),
                mock(CandidatureDTO.class)
        );

        when(etudiantService.getCandidaturesEtudiant())
                .thenReturn(candidatures);

        List<CandidatureDTO> resultat =
                tools.consulterMesCandidatures();

        assertEquals(2, resultat.size());
        assertEquals(candidatures, resultat);

        verify(etudiantService).getCandidaturesEtudiant();
    }

    @Test
    void doitRetournerUneListeVideSiAucuneCandidature()
            throws Exception {
        when(etudiantService.getCandidaturesEtudiant())
                .thenReturn(List.of());

        List<CandidatureDTO> resultat =
                tools.consulterMesCandidatures();

        assertNotNull(resultat);
        assertTrue(resultat.isEmpty());

        verify(etudiantService).getCandidaturesEtudiant();
    }

    @Test
    void doitPropagerExceptionSiEtudiantIntrouvablePourCandidatures()
            throws Exception {
        when(etudiantService.getCandidaturesEtudiant())
                .thenThrow(mock(EtudiantIntrouvableException.class));

        assertThrows(
                EtudiantIntrouvableException.class,
                () -> tools.consulterMesCandidatures()
        );
    }

    @Test
    void doitRetournerMesEntrevues() throws Exception {
        List<EntrevueDTO> entrevues = List.of(
                mock(EntrevueDTO.class),
                mock(EntrevueDTO.class)
        );

        when(etudiantService.getConvocationsEtudiant())
                .thenReturn(entrevues);

        List<EntrevueDTO> resultat =
                tools.consulterMesEntrevues();

        assertEquals(2, resultat.size());
        assertEquals(entrevues, resultat);

        verify(etudiantService).getConvocationsEtudiant();
    }

    @Test
    void doitRetournerUneListeVideSiAucuneEntrevue()
            throws Exception {
        when(etudiantService.getConvocationsEtudiant())
                .thenReturn(List.of());

        List<EntrevueDTO> resultat =
                tools.consulterMesEntrevues();

        assertNotNull(resultat);
        assertTrue(resultat.isEmpty());

        verify(etudiantService).getConvocationsEtudiant();
    }

    @Test
    void doitPropagerExceptionSiEtudiantIntrouvablePourEntrevues()
            throws Exception {
        when(etudiantService.getConvocationsEtudiant())
                .thenThrow(mock(EtudiantIntrouvableException.class));

        assertThrows(
                EtudiantIntrouvableException.class,
                () -> tools.consulterMesEntrevues()
        );
    }

    @Test
    void doitRetournerMonProfesseurAssigne() throws Exception {
        MonProfesseurAssigneDTO professeur =
                mock(MonProfesseurAssigneDTO.class);

        when(etudiantService.getMonProfesseur())
                .thenReturn(professeur);

        MonProfesseurAssigneDTO resultat =
                tools.consulterMonProfesseur();

        assertNotNull(resultat);
        assertEquals(professeur, resultat);

        verify(etudiantService).getMonProfesseur();
    }

    @Test
    void doitPropagerExceptionSiEtudiantIntrouvablePourProfesseur()
            throws Exception {
        when(etudiantService.getMonProfesseur())
                .thenThrow(mock(EtudiantIntrouvableException.class));

        assertThrows(
                EtudiantIntrouvableException.class,
                () -> tools.consulterMonProfesseur()
        );
    }

    @Test
    void doitRetournerMesNotificationsNonLues()
            throws Exception {
        List<NotificationDTO> notifications = List.of(
                mock(NotificationDTO.class),
                mock(NotificationDTO.class)
        );

        when(notificationService.getNotificationsNonLuesEtudiant())
                .thenReturn(notifications);

        List<NotificationDTO> resultat =
                tools.consulterMesNotificationsNonLues();

        assertEquals(2, resultat.size());
        assertEquals(notifications, resultat);

        verify(notificationService)
                .getNotificationsNonLuesEtudiant();
    }

    @Test
    void doitRetournerUneListeVideSiAucuneNotificationNonLue()
            throws Exception {
        when(notificationService.getNotificationsNonLuesEtudiant())
                .thenReturn(List.of());

        List<NotificationDTO> resultat =
                tools.consulterMesNotificationsNonLues();

        assertNotNull(resultat);
        assertTrue(resultat.isEmpty());

        verify(notificationService)
                .getNotificationsNonLuesEtudiant();
    }

    @Test
    void doitPropagerExceptionSiEtudiantIntrouvablePourNotifications()
            throws Exception {
        when(notificationService.getNotificationsNonLuesEtudiant())
                .thenThrow(mock(EtudiantIntrouvableException.class));

        assertThrows(
                EtudiantIntrouvableException.class,
                () -> tools.consulterMesNotificationsNonLues()
        );
    }
}
