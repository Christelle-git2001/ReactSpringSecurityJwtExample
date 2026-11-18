package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}