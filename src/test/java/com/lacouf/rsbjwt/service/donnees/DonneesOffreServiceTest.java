package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
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
    @Mock OffreDeStageRepository offres;
    @InjectMocks DonneesOffreService service;

    @ParameterizedTest
    @EnumSource(value = Statut.class, names = {"EN_ATTENTE", "ACCEPTEE"})
    void rechercheLesOffresAvecLeBonStatut(Statut statut) {
        var offre = new OffreDeStage();
        offre.setId(5L);
        offre.setTitle("Dev junior");
        offre.setStatut(statut);
        when(offres.findByStatut(statut)).thenReturn(List.of(offre));

        var resultat = statut == Statut.EN_ATTENTE
                ? service.obtenirOffresEnAttente() : service.obtenirOffresApprouvees();

        assertThat(resultat).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(5L);
            assertThat(dto.title()).isEqualTo("Dev junior");
            assertThat(dto.statut()).isEqualTo(statut);
        });
    }

    @ParameterizedTest
    @EnumSource(Statut.class)
    void compteLesOffresSansChargerLaListe(Statut statut) throws ParametreDonneesInvalideException {
        when(offres.countByStatut(statut)).thenReturn(4L);

        assertThat(service.compterOffresParStatut(statut)).isEqualTo(4L);
        verify(offres).countByStatut(statut);
        verifyNoMoreInteractions(offres);
    }

    @Test
    void refuseUnStatutAbsent() {
        assertThatExceptionOfType(ParametreDonneesInvalideException.class)
                .isThrownBy(() -> service.compterOffresParStatut(null))
                .withMessage("chatbot.errors.offer_status_required");
        verifyNoInteractions(offres);
    }

    @Test
    void compterOffreActivePasseLaDateDuJourEtRetourneLeNombreAttendu() {
        var avantAppel = LocalDate.now();
        when(offres.countByDisplayEndDateAfterAndStatutAcceptee(any(LocalDate.class))).thenReturn(6L);

        Long resultat = service.compterOffreActive();

        var apresAppel = LocalDate.now();
        assertThat(resultat).isEqualTo(6L);

        var captor = ArgumentCaptor.forClass(LocalDate.class);
        verify(offres).countByDisplayEndDateAfterAndStatutAcceptee(captor.capture());
        assertThat(captor.getValue()).isBetween(avantAppel, apresAppel);
        verifyNoMoreInteractions(offres);
    }

    @Test
    void compterOffreSansStagiairePasseLaDateDuJourEtRetourneLeNombreAttendu() {
        var avantAppel = LocalDate.now();
        when(offres.findByCandidaturesIsEmptyAndDisplayEndDateIsAfter(any(LocalDate.class))).thenReturn(3L);

        Long resultat = service.compterOffreSansStagiaire();

        var apresAppel = LocalDate.now();
        assertThat(resultat).isEqualTo(3L);

        var captor = ArgumentCaptor.forClass(LocalDate.class);
        verify(offres).findByCandidaturesIsEmptyAndDisplayEndDateIsAfter(captor.capture());
        assertThat(captor.getValue()).isBetween(avantAppel, apresAppel);
        verifyNoMoreInteractions(offres);
    }

    @Test
    void compterOffreAvecAtLeastUnStagiaireRetourneLeNombreAttendu() {
        when(offres.countOffresComblees()).thenReturn(5L);

        Long resultat = service.compterOffreAvecAtLeastUnStagiaire();

        assertThat(resultat).isEqualTo(5L);
        verify(offres).countOffresComblees();
        verifyNoMoreInteractions(offres);
    }

    @Test
    void getOffresRetourneLeNombreTotalDOffres() {
        when(offres.findAll()).thenReturn(List.of(new OffreDeStage(), new OffreDeStage()));

        Long resultat = service.getOffres();

        assertThat(resultat).isEqualTo(2L);
        verify(offres).findAll();
        verifyNoMoreInteractions(offres);
    }
}