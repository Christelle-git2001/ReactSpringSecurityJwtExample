package com.lacouf.rsbjwt.service.chatbot;

import com.lacouf.rsbjwt.model.Enum.ModeResultat;
import com.lacouf.rsbjwt.model.Enum.SignataireContrat;
import com.lacouf.rsbjwt.service.chatbot.tools.*;
import com.lacouf.rsbjwt.service.donnees.DonneesTemporellesService;
import com.lacouf.rsbjwt.service.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.ai.support.ToolCallbacks;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatbotTemporelToolsTest {
    final DonneesTemporellesService donnees = mock(DonneesTemporellesService.class);
    final ChatbotGestionnaireTemporelTools tools = new ChatbotGestionnaireTemporelTools(donnees);

    @Test
    void expose35OutilsMetierEtTemporelsSansAncienParcoursDeConfirmation() {
        var schemas = ToolCallbacks.from(mock(ChatbotGestionnaireTools.class), tools);
        assertThat(schemas).hasSize(35);
        assertThat(Arrays.stream(schemas).map(s -> s.getToolDefinition().name()).toList()).doesNotHaveDuplicates();
        assertThat(Arrays.stream(schemas).map(s -> s.getToolDefinition().name()).toList()).doesNotContain("rechercherEntrevuesPourConfirmation");
        var offres = Arrays.stream(schemas).filter(s -> s.getToolDefinition().name().equals("consulterOffresCreeesEntre")).findFirst().orElseThrow();
        assertThat(offres.getToolDefinition().inputSchema()).contains("LISTE", "NOMBRE");
    }

    @Test
    void unComptageNeChargePasLaListe() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        when(donnees.compterOffresCreeesEntre(jour, jour)).thenReturn(4L);
        assertThat(tools.consulterOffresCreeesEntre(jour, jour, ModeResultat.NOMBRE).nombre()).isEqualTo(4);
        verify(donnees, never()).obtenirOffresCreeesEntre(any(), any());
    }

    @Test
    void retourneLesOffresCreeesEnModeListe() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        var offres = List.of(OffreDeStageDTO.empty());
        when(donnees.obtenirOffresCreeesEntre(jour, jour)).thenReturn(offres);

        var resultat = tools.consulterOffresCreeesEntre(jour, jour, ModeResultat.LISTE);

        assertThat(resultat.nombre()).isNull();
        assertThat(resultat.resultats()).isEqualTo(offres);
        verify(donnees, never()).compterOffresCreeesEntre(any(), any());
    }

    @Test
    void consulteLaFinDAffichageEnModeNombreEtListe() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        var offres = List.of(OffreDeStageDTO.empty());
        when(donnees.compterOffresAvecFinAffichageApres(jour)).thenReturn(3L);
        when(donnees.obtenirOffresAvecFinAffichageApres(jour)).thenReturn(offres);

        assertThat(tools.consulterOffresAvecFinAffichageApres(jour, ModeResultat.NOMBRE).nombre()).isEqualTo(3);
        verify(donnees, never()).obtenirOffresAvecFinAffichageApres(any());
        assertThat(tools.consulterOffresAvecFinAffichageApres(jour, ModeResultat.LISTE).resultats()).isEqualTo(offres);
        verify(donnees, times(1)).compterOffresAvecFinAffichageApres(jour);
    }

    @Test
    void consulteLesCandidaturesEnModeNombreEtListe() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        when(donnees.compterCandidaturesDeposeesEntre(jour, jour)).thenReturn(2L);

        assertThat(tools.consulterCandidaturesDeposeesEntre(jour, jour, ModeResultat.NOMBRE).nombre()).isEqualTo(2);
        verify(donnees, never()).obtenirCandidaturesDeposeesEntre(any(), any());
        var resultat = tools.consulterCandidaturesDeposeesEntre(jour, jour, ModeResultat.LISTE);
        assertThat(resultat.nombre()).isNull();
        assertThat(resultat.resultats()).isEmpty();
        verify(donnees).obtenirCandidaturesDeposeesEntre(jour, jour);
        verify(donnees, times(1)).compterCandidaturesDeposeesEntre(jour, jour);
    }

    @Test
    void consulteLesCvEnModeNombreEtListe() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        when(donnees.compterCvDeposesEntre(jour, jour)).thenReturn(5L);

        assertThat(tools.consulterCvDeposesEntre(jour, jour, ModeResultat.NOMBRE).nombre()).isEqualTo(5);
        verify(donnees, never()).obtenirCvDeposesEntre(any(), any());
        var resultat = tools.consulterCvDeposesEntre(jour, jour, ModeResultat.LISTE);
        assertThat(resultat.nombre()).isNull();
        assertThat(resultat.resultats()).isEmpty();
        verify(donnees).obtenirCvDeposesEntre(jour, jour);
        verify(donnees, times(1)).compterCvDeposesEntre(jour, jour);
    }

    @Test
    void consulteLesEntrevuesEnModeNombreEtListe() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        when(donnees.compterEntrevuesEntre(jour, jour)).thenReturn(7L);

        assertThat(tools.consulterEntrevuesEntre(jour, jour, ModeResultat.NOMBRE).nombre()).isEqualTo(7);
        verify(donnees, never()).obtenirEntrevuesEntre(any(), any());
        var resultat = tools.consulterEntrevuesEntre(jour, jour, ModeResultat.LISTE);
        assertThat(resultat.nombre()).isNull();
        assertThat(resultat.resultats()).isEmpty();
        verify(donnees).obtenirEntrevuesEntre(jour, jour);
        verify(donnees, times(1)).compterEntrevuesEntre(jour, jour);
    }

    @Test
    void consulteLesSignaturesEnTransmettantLeSignataireEtLeMode() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        when(donnees.compterContratsSignesEntre(SignataireContrat.EMPLOYEUR, jour, jour)).thenReturn(4L);

        assertThat(tools.consulterContratsSignesEntre(SignataireContrat.EMPLOYEUR, jour, jour, ModeResultat.NOMBRE).nombre()).isEqualTo(4);
        verify(donnees, never()).obtenirContratsSignesEntre(any(), any(), any());
        var resultat = tools.consulterContratsSignesEntre(SignataireContrat.EMPLOYEUR, jour, jour, ModeResultat.LISTE);
        assertThat(resultat.nombre()).isNull();
        assertThat(resultat.resultats()).isEmpty();
        verify(donnees).obtenirContratsSignesEntre(SignataireContrat.EMPLOYEUR, jour, jour);
        verify(donnees, times(1)).compterContratsSignesEntre(SignataireContrat.EMPLOYEUR, jour, jour);
    }

    @Test
    void refuseUnModeAbsentAvantDeConsulterLesDonnees() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        assertThatIllegalArgumentException().isThrownBy(() -> tools.consulterOffresCreeesEntre(jour, jour, null));
        assertThatIllegalArgumentException().isThrownBy(() -> tools.consulterOffresAvecFinAffichageApres(jour, null));
        assertThatIllegalArgumentException().isThrownBy(() -> tools.consulterCandidaturesDeposeesEntre(jour, jour, null));
        assertThatIllegalArgumentException().isThrownBy(() -> tools.consulterCvDeposesEntre(jour, jour, null));
        assertThatIllegalArgumentException().isThrownBy(() -> tools.consulterEntrevuesEntre(jour, jour, null));
        assertThatIllegalArgumentException().isThrownBy(() -> tools.consulterContratsSignesEntre(SignataireContrat.ETUDIANT, jour, jour, null));
        verifyNoInteractions(donnees);
    }

    @Test
    void transmetLesDatesDesListesEtLaReferenceTemporelle() {
        LocalDate debut = LocalDate.of(2026, 11, 9);
        LocalDate fin = LocalDate.of(2026, 11, 15);
        var offres = List.of(OffreDeStageDTO.empty());
        var reference = new ReferenceTemporelleDTO(debut, debut.atTime(10, 0), "America/Toronto", debut, fin);
        when(donnees.obtenirOffresExpirantEntre(debut, fin)).thenReturn(offres);
        when(donnees.obtenirOffresAvecDebutStageEntre(debut, fin)).thenReturn(offres);
        when(donnees.obtenirDateReference()).thenReturn(reference);

        assertThat(tools.obtenirOffresExpirantEntre(debut, fin)).isEqualTo(offres);
        assertThat(tools.obtenirOffresAvecDebutStageEntre(debut, fin)).isEqualTo(offres);
        assertThat(tools.obtenirDateReference()).isEqualTo(reference);
    }

    @Test
    void springAiConvertitLesDatesEtLesEnumsDesArgumentsJson() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        when(donnees.compterContratsSignesEntre(SignataireContrat.GESTIONNAIRE, jour, jour)).thenReturn(6L);
        var callback = Arrays.stream(ToolCallbacks.from(tools))
                .filter(t -> t.getToolDefinition().name().equals("consulterContratsSignesEntre"))
                .findFirst().orElseThrow();

        String reponse = callback.call("""
                {"signataire":"GESTIONNAIRE","debut":"2026-11-12","fin":"2026-11-12","mode":"NOMBRE"}
                """);

        assertThat(reponse).contains("\"nombre\":6");
        verify(donnees).compterContratsSignesEntre(SignataireContrat.GESTIONNAIRE, jour, jour);
    }
}