
package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Enum.Session;
import com.lacouf.rsbjwt.service.dto.SessionInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTest {

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService();
    }

    @Test
    void doitRetournerSessionAutomne() {
        SessionInfoDTO resultat = sessionService.getSessionFromDate(
                LocalDate.of(2026, 8, 20)
        );

        assertEquals(Session.AUTOMNE, resultat.session());
        assertEquals(2026, resultat.annee());
    }

    @Test
    void doitRetournerSessionHiver() {
        SessionInfoDTO resultat = sessionService.getSessionFromDate(
                LocalDate.of(2026, 1, 15)
        );

        assertEquals(Session.HIVER, resultat.session());
        assertEquals(2026, resultat.annee());
    }

    @Test
    void doitRetournerSessionEte() {
        SessionInfoDTO resultat = sessionService.getSessionFromDate(
                LocalDate.of(2026, 7, 15)
        );

        assertEquals(Session.ETE, resultat.session());
        assertEquals(2026, resultat.annee());
    }

    @Test
    void doitRetournerSessionHiver2027PourAutomne2026() {
        SessionInfoDTO resultat = sessionService.getSessionStageAVenir(
                LocalDate.of(2026, 11, 10)
        );

        assertEquals(Session.HIVER, resultat.session());
        assertEquals(2027, resultat.annee());
    }

    @Test
    void doitRetournerSessionEte2026PourHiver2026() {
        SessionInfoDTO resultat = sessionService.getSessionStageAVenir(
                LocalDate.of(2026, 3, 15)
        );

        assertEquals(Session.ETE, resultat.session());
        assertEquals(2026, resultat.annee());
    }

    @Test
    void doitRetournerSessionAutomne2026PourEte2026() {
        SessionInfoDTO resultat = sessionService.getSessionStageAVenir(
                LocalDate.of(2026, 7, 15)
        );

        assertEquals(Session.AUTOMNE, resultat.session());
        assertEquals(2026, resultat.annee());
    }

    @Test
    void le19AoutDoitEtreSessionEte() {
        SessionInfoDTO resultat = sessionService.getSessionFromDate(
                LocalDate.of(2026, 8, 19)
        );

        assertEquals(Session.ETE, resultat.session());
    }

    @Test
    void le20AoutDoitEtreSessionAutomne() {
        SessionInfoDTO resultat = sessionService.getSessionFromDate(
                LocalDate.of(2026, 8, 20)
        );

        assertEquals(Session.AUTOMNE, resultat.session());
    }

    @Test
    void le31DecembreDoitEtreSessionAutomne() {
        SessionInfoDTO resultat = sessionService.getSessionFromDate(
                LocalDate.of(2026, 12, 31)
        );

        assertEquals(Session.AUTOMNE, resultat.session());
    }

    @Test
    void le1erJanvierDoitEtreSessionHiver() {
        SessionInfoDTO resultat = sessionService.getSessionFromDate(
                LocalDate.of(2027, 1, 1)
        );

        assertEquals(Session.HIVER, resultat.session());
    }
}
