package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.model.Enum.*;
import com.lacouf.rsbjwt.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonneesTemporellesServiceTest {
    @Mock OffreDeStageRepository offres;
    @Mock CandidatureRepository candidatures;
    @Mock CvEtudiantRepository cvs;
    @Mock EntrevueRepository entrevues;
    @Mock ContratRepository contrats;
    DonneesTemporellesService service;
    final Clock horloge = Clock.fixed(Instant.parse("2026-11-12T15:00:00Z"), ZoneId.of("America/Toronto"));

    @BeforeEach
    void preparer() {
        service = new DonneesTemporellesService(offres, candidatures, cvs, entrevues, contrats, horloge);
    }

    @Test
    void inclutLaJourneeEntiereSansChargerLaListePourCompter() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        when(offres.countByApprovedAtGreaterThanEqualAndApprovedAtLessThan(jour.atStartOfDay(), jour.plusDays(1).atStartOfDay())).thenReturn(3L);

        assertThat(service.compterOffresAfficheesEntre(jour, jour)).isEqualTo(3);
        verify(offres).countByApprovedAtGreaterThanEqualAndApprovedAtLessThan(jour.atStartOfDay(), jour.plusDays(1).atStartOfDay());
        verifyNoMoreInteractions(offres);
    }

    @Test
    void refuseUnePeriodeInverseeOuUneDateAbsente() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        assertThatIllegalArgumentException().isThrownBy(() -> service.obtenirCvDeposesEntre(jour, jour.minusDays(1)));
        assertThatIllegalArgumentException().isThrownBy(() -> service.compterEntrevuesEntre(null, jour));
        assertThatIllegalArgumentException().isThrownBy(() -> service.compterOffresAvecFinAffichageApres(null));
        verifyNoInteractions(offres, cvs, entrevues);
    }

    @Test
    void distingueLesSignaturesDesTroisParties() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        for (SignataireContrat signataire : SignataireContrat.values()) {
            service.compterContratsSignesEntre(signataire, jour, jour);
            verify(contrats).countSignesEntre(signataire.name(), jour.atStartOfDay(), jour.plusDays(1).atStartOfDay());
        }
        assertThatIllegalArgumentException().isThrownBy(() -> service.compterContratsSignesEntre(null, jour, jour));
    }

    @Test
    void fournitLaDateLocaleEtLaSemaineDuLundiAuDimanche() {
        var reference = service.obtenirDateReference();
        assertThat(reference.date()).isEqualTo(LocalDate.of(2026, 11, 12));
        assertThat(reference.dateHeure().getHour()).isEqualTo(10);
        assertThat(reference.debutSemaine()).isEqualTo(LocalDate.of(2026, 11, 9));
        assertThat(reference.finSemaine()).isEqualTo(LocalDate.of(2026, 11, 15));
    }

    @Test
    void recherchePlusieursCorrespondancesSansChoisirUnEtudiantArbitrairement() {
        Entrevue premiere = entrevue(1, "Développeur Web Junior", "2026-11-13T10:00:00");
        Entrevue seconde = entrevue(2, "Dev junior", "2026-11-14T10:00:00");
        Entrevue autre = entrevue(3, "Comptable", "2026-11-14T11:00:00");
        when(entrevues.findByCandidatureOffreDeStageEmployeurBusinessNameContainingIgnoreCaseOrderByDateHeureEntrevueAsc("malinois"))
                .thenReturn(List.of(premiere, seconde, autre));

        assertThat(service.rechercherEntrevues("dev junior", "Malinois inc")).extracting(e -> e.id()).containsExactly(1L, 2L);
    }

    @Test
    void indiqueQuUneEntrevueDemainEstAVenir() {
        Entrevue entrevue = entrevue(1, "Dev junior", "2026-11-13T10:00:00");
        when(entrevues.findById(1L)).thenReturn(Optional.of(entrevue));

        var resultat = service.calculerTempsAvantEntrevue(1L);

        assertThat(resultat.etat()).isEqualTo("A_VENIR");
        assertThat(resultat.secondesAvant()).isEqualTo(86400);
        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.A_CONFIRMER);
        verify(entrevues, never()).save(any());
    }

    @Test
    void indiqueQuUneEntrevueCommenceeDepuisQuinzeMinutesEstEnCours() {
        Entrevue entrevue = entrevue(1, "Dev junior", "2026-11-12T09:45:00");
        when(entrevues.findById(1L)).thenReturn(Optional.of(entrevue));

        var resultat = service.calculerTempsAvantEntrevue(1L);

        assertThat(resultat.etat()).isEqualTo("EN_COURS");
        assertThat(resultat.secondesAvant()).isEqualTo(-900);
        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.A_CONFIRMER);
        verify(entrevues, never()).save(any());
    }

    @Test
    void indiqueQuUneEntrevueHierEstPassee() {
        Entrevue entrevue = entrevue(1, "Dev junior", "2026-11-11T10:00:00");
        when(entrevues.findById(1L)).thenReturn(Optional.of(entrevue));

        var resultat = service.calculerTempsAvantEntrevue(1L);

        assertThat(resultat.etat()).isEqualTo("PASSEE");
        assertThat(resultat.secondesAvant()).isEqualTo(-86400);
        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.A_CONFIRMER);
        verify(entrevues, never()).save(any());
    }

    @Test
    void nePresentePasUneEntrevueRefuseeOuFigeeCommeUnRendezVousActif() {
        Entrevue entrevue = entrevue(1, "Dev junior", "2026-11-13T10:00:00");
        when(entrevues.findById(1L)).thenReturn(Optional.of(entrevue));
        entrevue.setStatut(StatutEntrevue.REFUSEE);
        assertThat(service.calculerTempsAvantEntrevue(1L).etat()).isEqualTo("REFUSEE");
        Etudiant etudiant = new Etudiant();
        etudiant.setStatut(StatutEtudiant.STAGE_TROUVE);
        entrevue.getCandidature().setEtudiant(etudiant);
        assertThat(service.calculerTempsAvantEntrevue(1L).etat()).isEqualTo("INDISPONIBLE");
    }

    @Test
    void tientCompteDuChangementDHeurePourLeDelaiReel() {
        Clock changementHeure = Clock.fixed(Instant.parse("2026-11-01T04:30:00Z"), ZoneId.of("America/Toronto"));
        service = new DonneesTemporellesService(offres, candidatures, cvs, entrevues, contrats, changementHeure);
        when(entrevues.findById(1L)).thenReturn(Optional.of(entrevue(1, "Dev junior", "2026-11-01T02:30:00")));
        assertThat(service.calculerTempsAvantEntrevue(1L).secondesAvant()).isEqualTo(10800);
    }

    private Entrevue entrevue(long id, String titre, String date) {
        Employeur employeur = new Employeur();
        employeur.setId(7L);
        employeur.setCredentials(new Credentials());
        employeur.setBusinessName("Malinois inc");
        OffreDeStage offre = new OffreDeStage();
        offre.setTitle(titre);
        offre.setEmployeur(employeur);
        Candidature candidature = new Candidature(null, offre);
        candidature.setId(id + 10);
        Entrevue entrevue = Entrevue.builder().candidature(candidature).format(FormatEntrevue.EN_LIGNE)
                .dateHeureEntrevue(LocalDateTime.parse(date)).duree(30).build();
        entrevue.setId(id);
        return entrevue;
    }

    @Test
    void retourneLesOffresAfficheesPendantLesJourneesIncluses() {
        LocalDate debut = LocalDate.of(2026, 11, 9);
        LocalDate fin = LocalDate.of(2026, 11, 15);
        OffreDeStage offre = offre(1);
        offre.setCreatedAt(debut.minusMonths(1).atStartOfDay());
        offre.setApprovedAt(debut.atTime(14, 0));
        when(offres.findByApprovedAtGreaterThanEqualAndApprovedAtLessThanOrderByApprovedAtAsc(
                debut.atStartOfDay(), fin.plusDays(1).atStartOfDay())).thenReturn(List.of(offre));

        assertThat(service.obtenirOffresAfficheesEntre(debut, fin))
                .singleElement().satisfies(dto -> {
                    assertThat(dto.title()).isEqualTo("Dev junior");
                    assertThat(dto.approvedAt()).isEqualTo(debut.atTime(14, 0));
                    assertThat(dto.createdAt()).isEqualTo(debut.minusMonths(1).atStartOfDay());
                });
    }

    @Test
    void rechercheLaFinDAffichageApresLaDateDemandee() {
        LocalDate date = LocalDate.of(2026, 11, 15);
        when(offres.findByDisplayEndDateAfterOrderByDisplayEndDateAsc(date)).thenReturn(List.of(offre(1)));
        when(offres.countByDisplayEndDateAfter(date)).thenReturn(1L);

        assertThat(service.obtenirOffresAvecFinAffichageApres(date)).extracting(dto -> dto.id()).containsExactly(1L);
        assertThat(service.compterOffresAvecFinAffichageApres(date)).isEqualTo(1);
    }

    @Test
    void rechercheLesOffresExpirantDansLaPeriodeSansChangerLesDates() {
        LocalDate debut = LocalDate.of(2026, 11, 9);
        LocalDate fin = LocalDate.of(2026, 11, 15);
        when(offres.findByDisplayEndDateBetweenOrderByDisplayEndDateAsc(debut, fin)).thenReturn(List.of(offre(1)));

        assertThat(service.obtenirOffresExpirantEntre(debut, fin)).extracting(dto -> dto.id()).containsExactly(1L);
    }

    @Test
    void rechercheLesOffresParDebutDuStageEtNonParCreation() {
        LocalDate debut = LocalDate.of(2027, 1, 1);
        LocalDate fin = LocalDate.of(2027, 1, 31);
        when(offres.findByStartDateBetweenOrderByStartDateAsc(debut, fin)).thenReturn(List.of(offre(1)));

        assertThat(service.obtenirOffresAvecDebutStageEntre(debut, fin)).extracting(dto -> dto.id()).containsExactly(1L);
        verifyNoMoreInteractions(offres);
    }

    @Test
    void listeLesCandidaturesEtDistingueUnContratPartiellementSigne() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        Candidature soumise = entrevue(1, "Dev junior", "2026-11-13T10:00:00").getCandidature();
        Candidature retenue = entrevue(2, "Dev junior", "2026-11-13T10:00:00").getCandidature();
        retenue.setStatut(StatutCandidature.CONTRAT_CREE);
        Contrat contrat = contrat(retenue);
        contrat.setDateSignatureEtudiant(jour.atTime(9, 0));
        when(candidatures.findByDateCandidatureGreaterThanEqualAndDateCandidatureLessThanOrderByDateCandidatureAsc(
                jour.atStartOfDay(), jour.plusDays(1).atStartOfDay())).thenReturn(List.of(soumise, retenue));
        when(contrats.findByCandidatureId(retenue.getId())).thenReturn(Optional.of(contrat));

        assertThat(service.obtenirCandidaturesDeposeesEntre(jour, jour))
                .extracting(dto -> dto.contratSigne()).containsExactly(false, false);
        verify(contrats, never()).findByCandidatureId(soumise.getId());
        contrat.setDateSignatureEmployeur(jour.atTime(9, 30));
        contrat.setDateSignatureGestionnaire(jour.atTime(10, 0));
        assertThat(service.obtenirCandidaturesDeposeesEntre(jour, jour).get(1).contratSigne()).isTrue();
    }

    @Test
    void uneCandidatureSansContratEnBaseResteNonSignee() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        Candidature candidature = entrevue(1, "Dev junior", "2026-11-13T10:00:00").getCandidature();
        candidature.setStatut(StatutCandidature.CONTRAT_CREE);
        when(candidatures.findByDateCandidatureGreaterThanEqualAndDateCandidatureLessThanOrderByDateCandidatureAsc(
                jour.atStartOfDay(), jour.plusDays(1).atStartOfDay())).thenReturn(List.of(candidature));

        assertThat(service.obtenirCandidaturesDeposeesEntre(jour, jour).getFirst().contratSigne()).isFalse();
    }

    @Test
    void compteLesCandidaturesSansChargerLesContratsOuLaListe() {
        LocalDate jour = LocalDate.of(2026, 12, 31);
        when(candidatures.countByDateCandidatureGreaterThanEqualAndDateCandidatureLessThan(
                jour.atStartOfDay(), LocalDate.of(2027, 1, 1).atStartOfDay())).thenReturn(3L);

        assertThat(service.compterCandidaturesDeposeesEntre(jour, jour)).isEqualTo(3);
        verifyNoMoreInteractions(candidatures);
        verifyNoInteractions(contrats);
    }

    @Test
    void listeLesCvDeposesSansExclureLesCvRefuses() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        Etudiant etudiant = new Etudiant();
        etudiant.setId(1L);
        etudiant.setCredentials(new Credentials());
        etudiant.setDepartment(Departement.INFORMATIQUE);
        CvEtudiant cv = CvEtudiant.builder().fileName("cv.pdf").etudiant(etudiant)
                .uploadDate(jour.atTime(14, 0)).build();
        cv.setId(4L);
        cv.setStatut(Statut.REFUSEE);
        when(cvs.findByUploadDateGreaterThanEqualAndUploadDateLessThanOrderByUploadDateAsc(
                jour.atStartOfDay(), jour.plusDays(1).atStartOfDay())).thenReturn(List.of(cv));

        assertThat(service.obtenirCvDeposesEntre(jour, jour)).singleElement().satisfies(dto -> {
            assertThat(dto.fileName()).isEqualTo("cv.pdf");
            assertThat(dto.statut()).isEqualTo(Statut.REFUSEE);
        });
    }

    @Test
    void compteLesCvDeposesSansChargerLaListe() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        when(cvs.countByUploadDateGreaterThanEqualAndUploadDateLessThan(
                jour.atStartOfDay(), jour.plusDays(1).atStartOfDay())).thenReturn(2L);

        assertThat(service.compterCvDeposesEntre(jour, jour)).isEqualTo(2);
        verifyNoMoreInteractions(cvs);
    }

    @Test
    void listeLesEntrevuesDeLaPeriodeAvecLeurStatutReel() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        Entrevue entrevue = entrevue(1, "Dev junior", "2026-11-12T10:00:00");
        entrevue.setStatut(StatutEntrevue.REFUSEE);
        when(entrevues.findByDateHeureEntrevueGreaterThanEqualAndDateHeureEntrevueLessThanOrderByDateHeureEntrevueAsc(
                jour.atStartOfDay(), jour.plusDays(1).atStartOfDay())).thenReturn(List.of(entrevue));

        assertThat(service.obtenirEntrevuesEntre(jour, jour)).singleElement()
                .satisfies(dto -> assertThat(dto.statut()).isEqualTo(StatutEntrevue.REFUSEE));
    }

    @Test
    void compteLesEntrevuesSansChargerLaListe() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        when(entrevues.countByDateHeureEntrevueGreaterThanEqualAndDateHeureEntrevueLessThan(
                jour.atStartOfDay(), jour.plusDays(1).atStartOfDay())).thenReturn(5L);

        assertThat(service.compterEntrevuesEntre(jour, jour)).isEqualTo(5);
        verifyNoMoreInteractions(entrevues);
    }

    @Test
    void listeLesContratsSelonLeSignataireSansExigerLesTroisSignatures() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        Contrat contrat = contrat(entrevue(1, "Dev junior", "2026-11-13T10:00:00").getCandidature());
        contrat.setDateSignatureEtudiant(jour.atTime(9, 0));
        for (SignataireContrat signataire : SignataireContrat.values()) {
            when(contrats.findSignesEntre(signataire.name(), jour.atStartOfDay(), jour.plusDays(1).atStartOfDay()))
                    .thenReturn(List.of(contrat));

            assertThat(service.obtenirContratsSignesEntre(signataire, jour, jour)).singleElement().satisfies(dto -> {
                assertThat(dto.contrat().dateSignatureEtudiant()).isEqualTo(jour.atTime(9, 0));
                assertThat(dto.candidature().contratSigne()).isFalse();
            });
        }
    }

    @Test
    void unePeriodeSansDonneesRetourneDesListesVidesEtDesComptagesNuls() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        assertThat(service.obtenirOffresAfficheesEntre(jour, jour)).isEmpty();
        assertThat(service.obtenirOffresAvecFinAffichageApres(jour)).isEmpty();
        assertThat(service.obtenirOffresExpirantEntre(jour, jour)).isEmpty();
        assertThat(service.obtenirOffresAvecDebutStageEntre(jour, jour)).isEmpty();
        assertThat(service.obtenirCandidaturesDeposeesEntre(jour, jour)).isEmpty();
        assertThat(service.obtenirCvDeposesEntre(jour, jour)).isEmpty();
        assertThat(service.obtenirEntrevuesEntre(jour, jour)).isEmpty();
        assertThat(service.obtenirContratsSignesEntre(SignataireContrat.ETUDIANT, jour, jour)).isEmpty();
        assertThat(service.compterOffresAfficheesEntre(jour, jour)).isZero();
        assertThat(service.compterOffresAvecFinAffichageApres(jour)).isZero();
        assertThat(service.compterCandidaturesDeposeesEntre(jour, jour)).isZero();
        assertThat(service.compterCvDeposesEntre(jour, jour)).isZero();
        assertThat(service.compterEntrevuesEntre(jour, jour)).isZero();
        assertThat(service.compterContratsSignesEntre(SignataireContrat.ETUDIANT, jour, jour)).isZero();
    }

    @Test
    void refuseLesAnneesHorsLimitesEtAccepteLaDerniereJourneeSupportee() {
        assertThatIllegalArgumentException().isThrownBy(() -> service.compterOffresAvecFinAffichageApres(LocalDate.of(0, 1, 1)));
        assertThatIllegalArgumentException().isThrownBy(() -> service.compterOffresAvecFinAffichageApres(LocalDate.of(9999, 1, 1)));
        verifyNoInteractions(offres);
        LocalDate dernierJour = LocalDate.of(9998, 12, 31);
        service.compterOffresAfficheesEntre(dernierJour, dernierJour);
        verify(offres).countByApprovedAtGreaterThanEqualAndApprovedAtLessThan(
                dernierJour.atStartOfDay(), LocalDate.of(9999, 1, 1).atStartOfDay());
    }

    @Test
    void refuseUnSignataireAbsentPourLaListeDesContrats() {
        LocalDate jour = LocalDate.of(2026, 11, 12);
        assertThatIllegalArgumentException().isThrownBy(() -> service.obtenirContratsSignesEntre(null, jour, jour));
        verifyNoInteractions(contrats);
    }

    @Test
    void refuseUneRechercheSansPosteOuEntrepriseValide() {
        assertThatIllegalArgumentException().isThrownBy(() -> service.rechercherEntrevues(null, "Malinois"));
        assertThatIllegalArgumentException().isThrownBy(() -> service.rechercherEntrevues("Dev", " "));
        assertThatIllegalArgumentException().isThrownBy(() -> service.rechercherEntrevues("!", "Malinois"));
        assertThatIllegalArgumentException().isThrownBy(() -> service.rechercherEntrevues("Dev", "!"));
        assertThatIllegalArgumentException().isThrownBy(() -> service.rechercherEntrevues("x".repeat(201), "Malinois"));
        assertThatIllegalArgumentException().isThrownBy(() -> service.rechercherEntrevues("Dev", "x".repeat(201)));
        verifyNoInteractions(entrevues);
    }

    @Test
    void rechercheSansCorrespondanceRetourneUneListeVide() {
        assertThat(service.rechercherEntrevues("Dev junior", "Malinois inc")).isEmpty();
    }

    @Test
    void refuseUnIdentifiantDEntrevueInvalideOuInexistant() {
        assertThatIllegalArgumentException().isThrownBy(() -> service.calculerTempsAvantEntrevue(null));
        assertThatIllegalArgumentException().isThrownBy(() -> service.calculerTempsAvantEntrevue(0L));
        assertThatIllegalArgumentException().isThrownBy(() -> service.calculerTempsAvantEntrevue(-1L));
        verifyNoInteractions(entrevues);
        assertThatIllegalArgumentException().isThrownBy(() -> service.calculerTempsAvantEntrevue(99L))
                .withMessage("chatbot.errors.interview_missing");
    }

    @Test
    void distingueLeDebutExactLaFinExacteEtLeStatutTermine() {
        Entrevue entrevue = entrevue(1, "Dev junior", "2026-11-12T10:00:00");
        when(entrevues.findById(1L)).thenReturn(Optional.of(entrevue));
        assertThat(service.calculerTempsAvantEntrevue(1L).etat()).isEqualTo("EN_COURS");
        assertThat(service.calculerTempsAvantEntrevue(1L).secondesAvant()).isZero();
        entrevue.setDateHeureEntrevue(LocalDateTime.of(2026, 11, 12, 9, 30));
        assertThat(service.calculerTempsAvantEntrevue(1L).etat()).isEqualTo("PASSEE");
        assertThat(service.calculerTempsAvantEntrevue(1L).secondesRestantes()).isZero();
        entrevue.setStatut(StatutEntrevue.TERMINEE);
        assertThat(service.calculerTempsAvantEntrevue(1L).etat()).isEqualTo("TERMINEE");
        verify(entrevues, never()).save(any());
    }

    private OffreDeStage offre(long id) {
        OffreDeStage offre = new OffreDeStage();
        offre.setId(id);
        offre.setTitle("Dev junior");
        return offre;
    }

    private Contrat contrat(Candidature candidature) {
        Gestionnaire gestionnaire = new Gestionnaire();
        gestionnaire.setId(8L);
        Contrat contrat = new Contrat(candidature, gestionnaire);
        contrat.setId(9L);
        return contrat;
    }
}