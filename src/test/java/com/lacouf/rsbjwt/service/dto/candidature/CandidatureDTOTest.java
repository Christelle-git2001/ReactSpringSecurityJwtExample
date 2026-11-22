package com.lacouf.rsbjwt.service.dto.candidature;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.model.Enum.StatutEtudiant;
import com.lacouf.rsbjwt.service.dto.entrevue.EntrevueDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class CandidatureDTOTest {
    private Candidature candidature(StatutCandidature statut) {
        Etudiant etudiant = new Etudiant();
        etudiant.setId(1L);
        Candidature candidature = new Candidature();
        candidature.setId(2L);
        candidature.setEtudiant(etudiant);
        candidature.setStatut(statut);
        return candidature;
    }

    @ParameterizedTest
    @EnumSource(value = StatutCandidature.class, names = {"SOUMISE", "RETENUE_POUR_ENTREVUE", "ENTREVUE_PLANIFIEE", "ACCEPTEE"})
    void doitDeclinerUneCandidatureInactiveSansExposerLeStatutEtudiant(StatutCandidature statut) {
        var candidature = candidature(statut);
        var autreStage = new Candidature();
        autreStage.setId(99L);
        candidature.getEtudiant().setCandidatureStage(autreStage);
        candidature.getEtudiant().setStatut(StatutEtudiant.STAGE_TROUVE);
        var dto = CandidatureDTO.ofEmployeur(candidature);
        assertThat(dto.statut()).isEqualTo(StatutCandidature.OFFRE_DECLINEE);
        assertThat(dto.statutEtudiant()).isNull();
        assertThat(dto.actionsDesactivees()).isTrue();
        assertThat(CandidatureDTO.of(candidature).statut()).isEqualTo(statut);
        assertThat(CandidatureDTO.of(candidature).statutEtudiant()).isEqualTo(StatutEtudiant.STAGE_TROUVE);
        assertThat(candidature.getStatut()).isEqualTo(statut);
    }

    @Test
    void doitRetirerLeStatutEtudiantDuJsonEmployeur() {
        var candidature = candidature(StatutCandidature.SOUMISE);
        candidature.getEtudiant().setStatut(StatutEtudiant.EN_RECHERCHE);
        var mapper = JsonMapper.builder().build();
        assertThat(mapper.valueToTree(CandidatureDTO.ofEmployeur(candidature)).has("statutEtudiant")).isFalse();
        assertThat(mapper.valueToTree(CandidatureDTO.of(candidature)).get("statutEtudiant").asString())
                .isEqualTo("EN_RECHERCHE");
    }

    @ParameterizedTest
    @EnumSource(value = StatutCandidature.class, names = {"STAGE_TROUVE", "CONTRAT_CREE"})
    void doitConserverLeSuiviDuStageChoisi(StatutCandidature statut) {
        var candidature = candidature(statut);
        candidature.getEtudiant().setStatut(StatutEtudiant.STAGE_TROUVE);
        candidature.getEtudiant().setCandidatureStage(candidature);
        var dto = CandidatureDTO.ofEmployeur(candidature);
        assertThat(dto.statut()).isEqualTo(statut == StatutCandidature.STAGE_TROUVE
                ? StatutCandidature.ACCEPTEE : StatutCandidature.CONTRAT_CREE);
        assertThat(dto.statutEtudiant()).isNull();
        assertThat(dto.actionsDesactivees()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = StatutCandidature.class, names = {"REFUSEE", "ENTREVUE_REFUSEE"})
    void doitConserverUneDecisionDeRefusDejaPrise(StatutCandidature statut) {
        var candidature = candidature(statut);
        candidature.getEtudiant().setStatut(StatutEtudiant.STAGE_TROUVE);
        assertThat(CandidatureDTO.ofEmployeur(candidature).statut()).isEqualTo(statut);
    }

    @Test
    void doitAfficherLeRefusEtudiantCommeUneOffreDeclinee() {
        var candidature = candidature(StatutCandidature.REFUSEE);
        candidature.setMotifRefus("stage.refused_by_student");
        var dto = CandidatureDTO.ofEmployeur(candidature);
        assertThat(dto.statut()).isEqualTo(StatutCandidature.OFFRE_DECLINEE);
        assertThat(dto.actionsDesactivees()).isTrue();
        assertThat(CandidatureDTO.of(candidature).statut()).isEqualTo(StatutCandidature.REFUSEE);
    }

    @Test
    void doitUtiliserLeMemeStatutPourLesEntrevuesEmployeur() {
        var candidature = candidature(StatutCandidature.ENTREVUE_PLANIFIEE);
        candidature.getEtudiant().setStatut(StatutEtudiant.STAGE_TROUVE);
        var entrevue = new Entrevue();
        entrevue.setId(3L);
        entrevue.setCandidature(candidature);
        assertThat(EntrevueDTO.ofEmployeur(entrevue).statutCandidature()).isEqualTo(StatutCandidature.OFFRE_DECLINEE);
        assertThat(EntrevueDTO.of(entrevue).statutCandidature()).isEqualTo(StatutCandidature.ENTREVUE_PLANIFIEE);
    }

    @Test
    void doitAccepterLesValeursAbsentes() {
        assertThat(CandidatureDTO.ofEmployeur(null)).isNull();
        assertThat(CandidatureDTO.statutPourEmployeur(null)).isNull();
        assertThat(EntrevueDTO.ofEmployeur(null)).isNull();
        var entrevue = new Entrevue();
        entrevue.setId(3L);
        assertThat(EntrevueDTO.ofEmployeur(entrevue).statutCandidature()).isNull();
    }
}
