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
    @Mock OffreDeStageRepository offreDeStageRepository;
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
    void compterOffreActivePasseLaDateDuJourEtRetourneLeNombreAttendu() {
        var avantAppel = LocalDate.now();
        when(offreDeStageRepository.countByDisplayEndDateAfterAndStatutAcceptee(any(LocalDate.class))).thenReturn(6L);

        Long resultat = donneesOffreService.compterOffreActive();

        var apresAppel = LocalDate.now();
        assertThat(resultat).isEqualTo(6L);

        var captor = ArgumentCaptor.forClass(LocalDate.class);
        verify(offreDeStageRepository).countByDisplayEndDateAfterAndStatutAcceptee(captor.capture());
        assertThat(captor.getValue()).isBetween(avantAppel, apresAppel);
        verifyNoMoreInteractions(offreDeStageRepository);
    }

    @Test
    void compterOffreSansStagiairePasseLaDateDuJourEtRetourneLeNombreAttendu() {
        var avantAppel = LocalDate.now();
        when(offreDeStageRepository.countByCandidaturesIsEmptyAndDisplayEndDateIsAfter(any(LocalDate.class))).thenReturn(3L);

        Long resultat = donneesOffreService.compterOffreSansStagiaire();

        var apresAppel = LocalDate.now();
        assertThat(resultat).isEqualTo(3L);

        var captor = ArgumentCaptor.forClass(LocalDate.class);
        verify(offreDeStageRepository).countByCandidaturesIsEmptyAndDisplayEndDateIsAfter(captor.capture());
        assertThat(captor.getValue()).isBetween(avantAppel, apresAppel);
        verifyNoMoreInteractions(offreDeStageRepository);
    }

    @Test
    void compterOffreAvecAtLeastUnStagiaireRetourneLeNombreAttendu() {
        when(offreDeStageRepository.countOffresComblees()).thenReturn(5L);

        Long resultat = donneesOffreService.compterOffreAvecAtLeastUnStagiaire();

        assertThat(resultat).isEqualTo(5L);
        verify(offreDeStageRepository).countOffresComblees();
        verifyNoMoreInteractions(offreDeStageRepository);
    }

    @Test
    void getOffresRetourneLeNombreTotalDOffres() {
        when(offreDeStageRepository.findAll()).thenReturn(List.of(new OffreDeStage(), new OffreDeStage()));

        Long resultat = donneesOffreService.getOffres();

        assertThat(resultat).isEqualTo(2L);
        verify(offreDeStageRepository).findAll();
        verifyNoMoreInteractions(offreDeStageRepository);
    }
}