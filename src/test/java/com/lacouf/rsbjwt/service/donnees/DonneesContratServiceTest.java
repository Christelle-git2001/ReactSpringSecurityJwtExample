package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.Contrat;
import com.lacouf.rsbjwt.model.Enum.Saison;
import com.lacouf.rsbjwt.model.Gestionnaire;
import com.lacouf.rsbjwt.repository.ContratRepository;
import com.lacouf.rsbjwt.service.SessionService;
import com.lacouf.rsbjwt.service.dto.session.SessionDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonneesContratServiceTest {
    @Mock ContratRepository contratRepository;
    @Mock
    SessionService sessionService;
    @InjectMocks DonneesContratService donneesContratService;

    @Test
    void retourneLesContratsNonEntierementSignesAvecLeurCandidature() {
        var contrat = creerContrat();
        when(contratRepository.findNonEntierementSignes()).thenReturn(List.of(contrat));

        assertThat(donneesContratService.obtenirContratsNonSignes()).singleElement().satisfies(dto -> {
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
        when(contratRepository.findByDateSignatureGestionnaireIsNullAndDateSignatureEtudiantIsNotNullAndDateSignatureEmployeurIsNotNull())
                .thenReturn(List.of(contrat));

        assertThat(donneesContratService.obtenirContratsASignerGestionnaire()).singleElement().satisfies(dto -> {
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
    void countContratsASignerGestionnaireSessionActuelleRetourneLeNombreAttendu() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        Contrat contrat1 = new Contrat();
        contrat1.setId(1L);
        Contrat contrat2 = new Contrat();
        contrat2.setId(2L);
        List<Contrat> listeContrats = List.of(contrat1, contrat2);

        when(contratRepository.findByDateSignatureGestionnaireIsNullAndDateSignatureEtudiantIsNotNullAndDateSignatureEmployeurIsNotNull())
                .thenReturn(listeContrats);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerParSession(any(), any(), anyInt(), any()))
                .thenReturn(List.of(contrat1));

        assertThat(donneesContratService.countContratsASignerGestionnaireSessionActuelle()).isEqualTo(1L);
    }

    @Test
    void countAllSessionActuelleRetourneLeNombreDeContratsDeLaSessionActuelle() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        Contrat contrat1 = new Contrat();
        contrat1.setId(1L);
        Contrat contrat2 = new Contrat();
        contrat2.setId(2L);
        List<Contrat> tousLesContrats = List.of(contrat1, contrat2);

        when(contratRepository.findAll()).thenReturn(tousLesContrats);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerParSession(any(), any(), anyInt(), any()))
                .thenReturn(List.of(contrat1));

        Long resultat = donneesContratService.countAllSessionActuelle();

        assertThat(resultat).isEqualTo(1L);
        verify(contratRepository).findAll();
    }

    @Test
    void countContratSignerGestionnaireSessionActuelleRetourneLeNombreAttendu() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        Contrat contrat1 = new Contrat();
        contrat1.setId(1L);
        Contrat contrat2 = new Contrat();
        contrat2.setId(2L);
        List<Contrat> contratsSignes = List.of(contrat1, contrat2);

        when(contratRepository.findByDateSignatureGestionnaireIsNotNullAndDateSignatureEtudiantIsNotNullAndDateSignatureEmployeurIsNotNull())
                .thenReturn(contratsSignes);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerParSession(any(), any(), anyInt(), any()))
                .thenReturn(List.of(contrat1));

        assertThat(donneesContratService.countContratSignerGestionnaireSessionActuelle()).isEqualTo(1L);
        verify(contratRepository).findByDateSignatureGestionnaireIsNotNullAndDateSignatureEtudiantIsNotNullAndDateSignatureEmployeurIsNotNull();
    }

    @Test
    void contratBloqueRasseUneDateLimiteEnvironDixJoursAvantMaintenant() {
        var avantAppel = LocalDateTime.now().minusDays(10).minusSeconds(1);
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        Contrat contrat1 = new Contrat();
        contrat1.setId(1L);
        Contrat contrat2 = new Contrat();
        contrat2.setId(2L);
        List<Contrat> contratsBloques = List.of(contrat1, contrat2);

        when(contratRepository.findContratsBloquesOuEnRetard(any(LocalDateTime.class)))
                .thenReturn(contratsBloques);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerParSession(any(), any(), anyInt(), any()))
                .thenReturn(List.of(contrat1));

        long resultat = donneesContratService.contratBloque();

        var apresAppel = LocalDateTime.now().minusDays(10).plusSeconds(1);
        assertThat(resultat).isEqualTo(1L);

        var captor = org.mockito.ArgumentCaptor.forClass(LocalDateTime.class);
        verify(contratRepository).findContratsBloquesOuEnRetard(captor.capture());
        assertThat(captor.getValue()).isBetween(avantAppel, apresAppel);
    }
}