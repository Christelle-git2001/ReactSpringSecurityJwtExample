package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.Contrat;
import com.lacouf.rsbjwt.model.Gestionnaire;
import com.lacouf.rsbjwt.repository.ContratRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonneesContratServiceTest {
    @Mock ContratRepository contrats;
    @InjectMocks DonneesContratService service;

    @Test
    void retourneLesContratsNonEntierementSignesAvecLeurCandidature() {
        var contrat = creerContrat();
        when(contrats.findNonEntierementSignes()).thenReturn(List.of(contrat));

        assertThat(service.obtenirContratsNonSignes()).singleElement().satisfies(dto -> {
            assertThat(dto.contrat().id()).isEqualTo(9L);
            assertThat(dto.candidature().id()).isEqualTo(3L);
            assertThat(dto.candidature().contratSigne()).isFalse();
        });
    }

    @Test
    void rechercheLesContratsDejaSignesParEtudiantEtEmployeur() {
        var contrat = creerContrat();
        var signature = LocalDateTime.of(2026, 11, 12, 10, 0);
        contrat.setDateSignatureEtudiant(signature);
        contrat.setDateSignatureEmployeur(signature);
        when(contrats.findByDateSignatureGestionnaireIsNullAndDateSignatureEtudiantIsNotNullAndDateSignatureEmployeurIsNotNull())
                .thenReturn(List.of(contrat));

        assertThat(service.obtenirContratsASignerGestionnaire()).singleElement().satisfies(dto -> {
            assertThat(dto.contrat().dateSignatureEtudiant()).isEqualTo(signature);
            assertThat(dto.contrat().dateSignatureEmployeur()).isEqualTo(signature);
            assertThat(dto.contrat().dateSignatureGestionnaire()).isNull();
        });
    }

    private Contrat creerContrat() {
        var candidature = new Candidature();
        candidature.setId(3L);
        var gestionnaire = new Gestionnaire();
        gestionnaire.setId(1L);
        var contrat = new Contrat(candidature, gestionnaire);
        contrat.setId(9L);
        return contrat;
    }

    @Test
    void countContratsASignerGestionnaireRetourneLeNombreAttendu() {
        when(contrats.countByDateSignatureGestionnaireIsNullAndDateSignatureEtudiantIsNotNullAndDateSignatureEmployeurIsNotNull())
                .thenReturn(4L);

        assertThat(service.countContratsASignerGestionnaire()).isEqualTo(4L);
    }

    @Test
    void countContratSignerGestionnaireRetourneLeNombreAttendu() {
        when(contrats.countByDateSignatureGestionnaireIsNotNullAndDateSignatureEtudiantIsNotNullAndDateSignatureEmployeurIsNotNull())
                .thenReturn(7L);

        assertThat(service.countContratSignerGestionnaire()).isEqualTo(7L);
    }

    @Test
    void countAllRetourneLeNombreTotalDeContrats() {
        when(contrats.countAll()).thenReturn(15L);

        assertThat(service.countAll()).isEqualTo(15L);
    }

    @Test
    void contratBloqueRasseUneDateLimiteEnvironDixJoursAvantMaintenant() {
        var avantAppel = LocalDateTime.now().minusDays(10).minusSeconds(1);
        when(contrats.countContratsBloquesOuEnRetard(any(LocalDateTime.class))).thenReturn(2L);

        long resultat = service.contratBloque();

        var apresAppel = LocalDateTime.now().minusDays(10).plusSeconds(1);
        assertThat(resultat).isEqualTo(2L);

        var captor = org.mockito.ArgumentCaptor.forClass(LocalDateTime.class);
        verify(contrats).countContratsBloquesOuEnRetard(captor.capture());
        assertThat(captor.getValue()).isBetween(avantAppel, apresAppel);
    }
}