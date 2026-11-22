
package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Enum.Saison;
import com.lacouf.rsbjwt.context.SessionContext;
import com.lacouf.rsbjwt.model.Inscription;
import com.lacouf.rsbjwt.model.UserApp;
import com.lacouf.rsbjwt.repository.InscriptionRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.session.SessionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

        private SessionService sessionService;

        @Mock
        private SessionContext sessionContext;

        @Mock
        private InscriptionRepository inscriptionRepository;

        @Mock
        private UserAppRepository userAppRepository;

        @BeforeEach
        void setUp() {
            sessionService = new SessionService(sessionContext, inscriptionRepository, userAppRepository);
        }

    @Test
    void doitRetournerSessionHiver() {
        SessionDTO resultat = sessionService.getSession(
                LocalDate.of(2026, 1, 15)
        );

        assertEquals(Saison.HIVER, resultat.saison());
        assertEquals(2026, resultat.annee());
    }

    @Test
    void doitRetournerSessionHiverLe10Juin() {
        SessionDTO resultat = sessionService.getSession(
                LocalDate.of(2026, 6, 10)
        );

        assertEquals(Saison.HIVER, resultat.saison());
    }

    @Test
    void doitRetournerSessionAutomneLe11Juin() {
        SessionDTO resultat = sessionService.getSession(
                LocalDate.of(2026, 6, 11)
        );

        assertEquals(Saison.AUTOMNE, resultat.saison());
    }

    @Test
    void doitRetournerSessionAutomneLe19Aout() {
        SessionDTO resultat = sessionService.getSession(
                LocalDate.of(2026, 8, 19)
        );

        assertEquals(Saison.AUTOMNE, resultat.saison());
    }

    @Test
    void doitRetournerSessionAutomneLe20Aout() {
        SessionDTO resultat = sessionService.getSession(
                LocalDate.of(2026, 8, 20)
        );

        assertEquals(Saison.AUTOMNE, resultat.saison());
    }

    @Test
    void doitRetournerSessionAutomneLe31Decembre() {
        SessionDTO resultat = sessionService.getSession(
                LocalDate.of(2026, 12, 31)
        );

        assertEquals(Saison.AUTOMNE, resultat.saison());
    }

    @Test
    void doitRetournerSessionHiverLe1erJanvier() {
        SessionDTO resultat = sessionService.getSession(
                LocalDate.of(2027, 1, 1)
        );

        assertEquals(Saison.HIVER, resultat.saison());
        assertEquals(2027, resultat.annee());
    }

    @Test
    void doitRetournerLaSessionActuelle() {

        when(sessionContext.hasSessionCible()).thenReturn(false);

        SessionDTO resultat = sessionService.getSessionActuelle();

        SessionDTO attendu = sessionService.getSession(LocalDate.now());

        assertEquals(attendu, resultat);
    }

    @Test
    void doitAssignerLaBonneSession() {
        LocalDate date = LocalDate.of(2026, 11, 10);

        SessionDTO resultat = sessionService.assignerSession(date);

        assertEquals(Saison.AUTOMNE, resultat.saison());
        assertEquals(2026, resultat.annee());
    }

    @Test
    void doitRetournerLaPeriodeAutomne() {
        SessionService.SessionPeriode periode =
                sessionService.getPeriodeSession(Saison.AUTOMNE, 2026);

        assertEquals(
                LocalDateTime.of(2026, 8, 20, 0, 0),
                periode.debut()
        );

        assertEquals(
                LocalDateTime.of(2026, 12, 31, 23, 59, 59),
                periode.fin()
        );
    }

    @Test
    void doitRetournerLaPeriodeHiver() {
        SessionService.SessionPeriode periode =
                sessionService.getPeriodeSession(Saison.HIVER, 2026);

        assertEquals(
                LocalDateTime.of(2026, 1, 1, 0, 0),
                periode.debut()
        );

        assertEquals(
                LocalDateTime.of(2026, 8, 19, 23, 59, 59),
                periode.fin()
        );
    }

    @Test
    void doitFiltrerLesElementsSelonLaSession() {
        List<LocalDateTime> dates = List.of(
                LocalDateTime.of(2026, 8, 19, 12, 0),
                LocalDateTime.of(2026, 8, 20, 12, 0),
                LocalDateTime.of(2026, 10, 15, 12, 0),
                LocalDateTime.of(2027, 1, 10, 12, 0)
        );

        List<LocalDateTime> resultat =
                sessionService.filtrerParSession(
                        dates,
                        Saison.AUTOMNE,
                        2026,
                        Function.identity()
                );

        assertEquals(2, resultat.size());

        assertEquals(
                LocalDateTime.of(2026, 8, 20, 12, 0),
                resultat.get(0)
        );

        assertEquals(
                LocalDateTime.of(2026, 10, 15, 12, 0),
                resultat.get(1)
        );
    }

    @Test
    void doitIgnorerLesDatesNullesLorsDuFiltrage() {
        List<LocalDateTime> dates = java.util.Arrays.asList(
                LocalDateTime.of(2026, 9, 10, 12, 0),
                null,
                LocalDateTime.of(2027, 1, 10, 12, 0)
        );

        List<LocalDateTime> resultat =
                sessionService.filtrerParSession(
                        dates,
                        Saison.AUTOMNE,
                        2026,
                        Function.identity()
                );

        assertEquals(1, resultat.size());
    }

    @Test
    void doitFiltrerSelonLaSessionActuelle() {

        when(sessionContext.hasSessionCible()).thenReturn(false);

        List<LocalDateTime> dates = List.of(
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().plusMonths(1),
                LocalDateTime.now().plusYears(1)
        );

        List<LocalDateTime> resultat =
                sessionService.filtrerParSessionActuelle(
                        dates,
                        Function.identity()
                );

        SessionDTO actuelle = sessionService.getSessionActuelle();

        SessionService.SessionPeriode periode =
                sessionService.getPeriodeSession(
                        actuelle.saison(),
                        actuelle.annee()
                );

        assertTrue(resultat.stream().allMatch(date ->
                !date.isBefore(periode.debut())
                        && !date.isAfter(periode.fin())
        ));
    }

    @Test
    void doitRetournerSessionCibleLorsquElleExiste() {
        SessionDTO sessionCible = new SessionDTO(Saison.HIVER, 2025);

        when(sessionContext.hasSessionCible()).thenReturn(true);
        when(sessionContext.getSessionCible()).thenReturn(sessionCible);

        SessionDTO resultat = sessionService.getSessionActuelle();

        assertEquals(sessionCible, resultat);

        verify(sessionContext).getSessionCible();
    }

    @Test
    void doitFiltrerUtilisateursSelonLeursInscriptions() {
        UserApp utilisateur1 = mock(UserApp.class);
        UserApp utilisateur2 = mock(UserApp.class);
        UserApp utilisateur3 = mock(UserApp.class);

        Inscription inscriptionAutomne2026 = mock(Inscription.class);
        Inscription inscriptionHiver2026 = mock(Inscription.class);
        Inscription inscriptionAutomne2025 = mock(Inscription.class);

        when(inscriptionAutomne2026.getSaison()).thenReturn(Saison.AUTOMNE);
        when(inscriptionAutomne2026.getAnnee()).thenReturn(2026);

        when(inscriptionHiver2026.getSaison()).thenReturn(Saison.HIVER);

        when(inscriptionAutomne2025.getSaison()).thenReturn(Saison.AUTOMNE);
        when(inscriptionAutomne2025.getAnnee()).thenReturn(2025);

        when(utilisateur1.getInscriptions()).thenReturn(List.of(inscriptionAutomne2026));
        when(utilisateur2.getInscriptions()).thenReturn(List.of(inscriptionHiver2026));
        when(utilisateur3.getInscriptions()).thenReturn(List.of(inscriptionAutomne2025));

        List<UserApp> utilisateurs = List.of(utilisateur1, utilisateur2, utilisateur3);

        List<UserApp> resultat = sessionService.filtrerUtilisateursParSession(
                utilisateurs,
                Saison.AUTOMNE,
                2026
        );

        assertEquals(1, resultat.size());
        assertEquals(utilisateur1, resultat.get(0));
    }

    @Test
    void doitFiltrerUtilisateursSelonLaSessionCible() {
        SessionDTO sessionCible = new SessionDTO(Saison.AUTOMNE, 2026);

        when(sessionContext.hasSessionCible()).thenReturn(true);
        when(sessionContext.getSessionCible()).thenReturn(sessionCible);

        UserApp utilisateur1 = mock(UserApp.class);
        UserApp utilisateur2 = mock(UserApp.class);

        Inscription inscriptionAutomne2026 = mock(Inscription.class);
        Inscription inscriptionHiver2026 = mock(Inscription.class);

        when(inscriptionAutomne2026.getSaison()).thenReturn(Saison.AUTOMNE);
        when(inscriptionAutomne2026.getAnnee()).thenReturn(2026);

        when(inscriptionHiver2026.getSaison()).thenReturn(Saison.HIVER);

        when(utilisateur1.getInscriptions()).thenReturn(List.of(inscriptionAutomne2026));
        when(utilisateur2.getInscriptions()).thenReturn(List.of(inscriptionHiver2026));

        List<UserApp> resultat = sessionService.filtrerUtilisateursParSessionActuelle(
                List.of(utilisateur1, utilisateur2)
        );

        assertEquals(1, resultat.size());
        assertEquals(utilisateur1, resultat.get(0));
    }

    @Test
    void doitLeverExceptionLorsqueUtilisateurIntrouvable() {
        when(userAppRepository.findUserAppByEmail("test@test.com"))
                .thenReturn(Optional.empty());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@test.com", null)
        );

        assertThrows(
                com.lacouf.rsbjwt.exception.utilisateur.UtilisateurIntrouvableException.class,
                () -> sessionService.getSessionsDisponibles()
        );

        SecurityContextHolder.clearContext();
    }
}
