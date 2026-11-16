package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.Exception.ParametreDonneesInvalideException;
import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.Contrat;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.repository.CandidatureRepository;
import com.lacouf.rsbjwt.repository.ContratRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonneesCandidatureServiceTest {
    @Mock CandidatureRepository candidatures;
    @Mock ContratRepository contrats;
    @InjectMocks DonneesCandidatureService service;

    @Test
    void rechercheLesStagesTrouvesSansContrat() {
        var candidature = creerCandidature(StatutCandidature.STAGE_TROUVE);
        when(candidatures.findByStatutSansContrat(StatutCandidature.STAGE_TROUVE))
                .thenReturn(List.of(candidature));

        assertThat(service.obtenirStagesSansContrat()).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(3L);
            assertThat(dto.statut()).isEqualTo(StatutCandidature.STAGE_TROUVE);
            assertThat(dto.contratSigne()).isFalse();
        });
    }

    @ParameterizedTest
    @EnumSource(StatutCandidature.class)
    void compteLesCandidaturesSansChargerLaListe(StatutCandidature statut) throws ParametreDonneesInvalideException {
        when(candidatures.countByStatut(statut)).thenReturn(8L);

        assertThat(service.compterCandidaturesParStatut(statut)).isEqualTo(8L);
        verify(candidatures).countByStatut(statut);
        verifyNoMoreInteractions(candidatures);
        verifyNoInteractions(contrats);
    }

    @ParameterizedTest
    @EnumSource(value = StatutCandidature.class, mode = EnumSource.Mode.EXCLUDE, names = "CONTRAT_CREE")
    void retourneLesCandidaturesSansRechercherDeContratPourLesAutresStatuts(StatutCandidature statut)
            throws ParametreDonneesInvalideException {
        when(candidatures.findByStatut(statut)).thenReturn(List.of(creerCandidature(statut)));

        assertThat(service.obtenirCandidaturesParStatut(statut)).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(3L);
            assertThat(dto.statut()).isEqualTo(statut);
            assertThat(dto.contratSigne()).isFalse();
        });
        verifyNoInteractions(contrats);
    }

    @Test
    void indiqueSiLeContratCreeEstEntierementSigne() throws ParametreDonneesInvalideException {
        var candidature = creerCandidature(StatutCandidature.CONTRAT_CREE);
        var contrat = new Contrat();
        var signature = LocalDateTime.of(2026, 11, 12, 10, 0);
        contrat.setDateSignatureEtudiant(signature);
        contrat.setDateSignatureEmployeur(signature);
        contrat.setDateSignatureGestionnaire(signature);
        when(candidatures.findByStatut(StatutCandidature.CONTRAT_CREE)).thenReturn(List.of(candidature));
        when(contrats.findByCandidatureId(3L)).thenReturn(Optional.of(contrat));

        assertThat(service.obtenirCandidaturesParStatut(StatutCandidature.CONTRAT_CREE))
                .singleElement().satisfies(dto -> assertThat(dto.contratSigne()).isTrue());

        contrat.setDateSignatureGestionnaire(null);
        assertThat(service.obtenirCandidaturesParStatut(StatutCandidature.CONTRAT_CREE))
                .singleElement().satisfies(dto -> assertThat(dto.contratSigne()).isFalse());
    }

    @Test
    void accepteUneCandidatureDontLeContratEstIntrouvable() throws ParametreDonneesInvalideException {
        when(candidatures.findByStatut(StatutCandidature.CONTRAT_CREE))
                .thenReturn(List.of(creerCandidature(StatutCandidature.CONTRAT_CREE)));
        when(contrats.findByCandidatureId(3L)).thenReturn(Optional.empty());

        assertThat(service.obtenirCandidaturesParStatut(StatutCandidature.CONTRAT_CREE))
                .singleElement().satisfies(dto -> assertThat(dto.contratSigne()).isFalse());
    }

    @Test
    void refuseUnStatutAbsentSansAccederAuxRepositories() {
        assertThatExceptionOfType(ParametreDonneesInvalideException.class)
                .isThrownBy(() -> service.compterCandidaturesParStatut(null))
                .withMessage("chatbot.errors.application_status_required");
        assertThatExceptionOfType(ParametreDonneesInvalideException.class)
                .isThrownBy(() -> service.obtenirCandidaturesParStatut(null))
                .withMessage("chatbot.errors.application_status_required");
        verifyNoInteractions(candidatures, contrats);
    }

    private Candidature creerCandidature(StatutCandidature statut) {
        var candidature = new Candidature();
        candidature.setId(3L);
        candidature.setStatut(statut);
        return candidature;
    }
}