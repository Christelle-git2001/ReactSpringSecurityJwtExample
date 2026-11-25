package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException;
import com.lacouf.rsbjwt.model.Enum.Saison;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import com.lacouf.rsbjwt.service.SessionService;
import com.lacouf.rsbjwt.service.dto.session.SessionDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonneesOffreServiceTest {
    @Mock OffreDeStageRepository offreDeStageRepository;
    @Mock
    SessionService sessionService;
    @InjectMocks DonneesOffreService donneesOffreService;

    @ParameterizedTest
    @EnumSource(value = Statut.class, names = {"EN_ATTENTE", "ACCEPTEE"})
    void rechercheLesOffresAvecLeBonStatut(Statut statut) {
        var offre = new OffreDeStage();
        offre.setId(5L);
        offre.setTitle("Dev junior");
        offre.setStatut(statut);
        when(offreDeStageRepository.findByStatut(statut)).thenReturn(List.of(offre));

        var resultat = statut == Statut.EN_ATTENTE
                ? donneesOffreService.obtenirOffresEnAttente() : donneesOffreService.obtenirOffresApprouvees();

        assertThat(resultat).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(5L);
            assertThat(dto.title()).isEqualTo("Dev junior");
            assertThat(dto.statut()).isEqualTo(statut);
        });
    }

    @ParameterizedTest
    @EnumSource(Statut.class)
    void compteLesOffresSansChargerLaListe(Statut statut) throws ParametreDonneesInvalideException {
        when(offreDeStageRepository.countByStatut(statut)).thenReturn(4L);

        assertThat(donneesOffreService.compterOffresParStatut(statut)).isEqualTo(4L);
        verify(offreDeStageRepository).countByStatut(statut);
        verifyNoMoreInteractions(offreDeStageRepository);
    }

    @Test
    void refuseUnStatutAbsent() {
        assertThatExceptionOfType(ParametreDonneesInvalideException.class)
                .isThrownBy(() -> donneesOffreService.compterOffresParStatut(null))
                .withMessage("chatbot.errors.offer_status_required");
        verifyNoInteractions(offreDeStageRepository);
    }

    @Test
    void compterOffreActiveSessionActuellePasseLaDateDuJourEtRetourneLeNombreAttendu() {
        var avantAppel = LocalDate.now();
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        OffreDeStage offre1 = new OffreDeStage();
        offre1.setId(1L);
        OffreDeStage offre2 = new OffreDeStage();
        offre2.setId(2L);
        List<OffreDeStage> offresActives = List.of(offre1, offre2);

        when(offreDeStageRepository.findByDisplayEndDateAfterAndStatutAcceptee(any(LocalDate.class)))
                .thenReturn(offresActives);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerParSession(eq(offresActives), eq(Saison.AUTOMNE), eq(2026), any()))
                .thenReturn(List.of(offre1));

        Long resultat = donneesOffreService.compterOffreActiveSessionActuelle();

        var apresAppel = LocalDate.now();
        assertThat(resultat).isEqualTo(1L);

        var captor = ArgumentCaptor.forClass(LocalDate.class);
        verify(offreDeStageRepository).findByDisplayEndDateAfterAndStatutAcceptee(captor.capture());
        assertThat(captor.getValue()).isBetween(avantAppel, apresAppel);
        verifyNoMoreInteractions(offreDeStageRepository);
    }

    @Test
    void compterOffreSansStagiaireSessionActuellePasseLaDateDuJourEtRetourneLeNombreAttendu() {
        var avantAppel = LocalDate.now();
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        OffreDeStage offre1 = new OffreDeStage();
        offre1.setId(1L);
        OffreDeStage offre2 = new OffreDeStage();
        offre2.setId(2L);
        List<OffreDeStage> offresSansStagiaire = List.of(offre1, offre2);

        when(offreDeStageRepository.findByCandidaturesIsEmptyAndDisplayEndDateIsAfter(any(LocalDate.class)))
                .thenReturn(offresSansStagiaire);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerParSession(eq(offresSansStagiaire), eq(Saison.AUTOMNE), eq(2026), any()))
                .thenReturn(List.of(offre1));

        Long resultat = donneesOffreService.compterOffreSansStagiaireSessionActuelle();

        var apresAppel = LocalDate.now();
        assertThat(resultat).isEqualTo(1L);

        var captor = ArgumentCaptor.forClass(LocalDate.class);
        verify(offreDeStageRepository).findByCandidaturesIsEmptyAndDisplayEndDateIsAfter(captor.capture());
        assertThat(captor.getValue()).isBetween(avantAppel, apresAppel);
        verifyNoMoreInteractions(offreDeStageRepository);
    }

    @Test
    void compterOffreAvecAuMoinsUnStagiaireSessionActuelleRetourneLeNombreAttendu() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        OffreDeStage offre1 = new OffreDeStage();
        offre1.setId(1L);
        OffreDeStage offre2 = new OffreDeStage();
        offre2.setId(2L);
        List<OffreDeStage> offresComblees = List.of(offre1, offre2);

        when(offreDeStageRepository.countOffresComblees()).thenReturn(offresComblees);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerParSession(eq(offresComblees), eq(Saison.AUTOMNE), eq(2026), any()))
                .thenReturn(List.of(offre1));

        Long resultat = donneesOffreService.compterOffreAvecAuMoinsUnStagiaireSessionActuelle();

        assertThat(resultat).isEqualTo(1L);
        verify(offreDeStageRepository).countOffresComblees();
        verifyNoMoreInteractions(offreDeStageRepository);
    }

    @Test
    void getOffresRetourneLeNombreTotalDOffres() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        OffreDeStage offre1 = new OffreDeStage();
        offre1.setId(1L);
        OffreDeStage offre2 = new OffreDeStage();
        offre2.setId(2L);
        List<OffreDeStage> offres = List.of(offre1, offre2);

        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(offreDeStageRepository.findAll()).thenReturn(offres);
        when(sessionService.filtrerParSession(eq(offres), eq(Saison.AUTOMNE), eq(2026), any()))
                .thenReturn(offres);

        Long resultat = donneesOffreService.getOffres();

        assertThat(resultat).isEqualTo(2L);
        verify(offreDeStageRepository).findAll();
        verifyNoMoreInteractions(offreDeStageRepository);
    }
}