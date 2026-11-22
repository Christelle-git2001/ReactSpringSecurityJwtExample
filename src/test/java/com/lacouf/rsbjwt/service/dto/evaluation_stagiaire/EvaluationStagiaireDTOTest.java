package com.lacouf.rsbjwt.service.dto.evaluation_stagiaire;

import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.Enum.ChoixAccueil;
import com.lacouf.rsbjwt.model.Evaluation.*;
import com.lacouf.rsbjwt.model.EvaluationStagiaire;
import com.lacouf.rsbjwt.service.dto.candidature.ResumeStageEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EtudiantDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationStagiaireDTOTest {

    @Test
    void convertitLesDtosDeCriteresDansLesDeuxSensEtGereNull() {
        EvaluationProductiviteDTO productivite = new EvaluationProductiviteDTO(1L, 2L, 3L, 4L, 5L, "productivité");
        assertThat(EvaluationProductiviteDTO.toDto(EvaluationProductiviteDTO.toEntity(productivite))).isEqualTo(productivite);
        assertThat(EvaluationProductiviteDTO.toEntity(null)).isNull();
        assertThat(EvaluationProductiviteDTO.toDto(null)).isNull();

        QualiteTravailDTO qualiteTravail = new QualiteTravailDTO(1L, 2L, 3L, 4L, 5L, "travail");
        assertThat(QualiteTravailDTO.toDto(QualiteTravailDTO.toEntity(qualiteTravail))).isEqualTo(qualiteTravail);
        assertThat(QualiteTravailDTO.toEntity(null)).isNull();
        assertThat(QualiteTravailDTO.toDto(null)).isNull();

        QualiteSocialeDTO qualiteSociale = new QualiteSocialeDTO(1L, 2L, 3L, 4L, 5L, 6L, "social");
        assertThat(QualiteSocialeDTO.toDto(QualiteSocialeDTO.toEntity(qualiteSociale))).isEqualTo(qualiteSociale);
        assertThat(QualiteSocialeDTO.toEntity(null)).isNull();
        assertThat(QualiteSocialeDTO.toDto(null)).isNull();

        HabiletePersonelleDTO habiletes = new HabiletePersonelleDTO(1L, 2L, 3L, 4L, 5L, 6L, "habiletés");
        assertThat(HabiletePersonelleDTO.toDto(HabiletePersonelleDTO.toEntity(habiletes))).isEqualTo(habiletes);
        assertThat(HabiletePersonelleDTO.toEntity(null)).isNull();
        assertThat(HabiletePersonelleDTO.toDto(null)).isNull();
    }

    @Test
    void convertitLeSuperviseurEtLAppreciationDansLesDeuxSensEtGereNull() {
        SuperviseurStageDTO superviseur = new SuperviseurStageDTO("Ada", "Lovelace", "Gestionnaire", "514-555-1212");
        assertThat(SuperviseurStageDTO.toDto(SuperviseurStageDTO.toEntity(superviseur))).isEqualTo(superviseur);
        assertThat(SuperviseurStageDTO.toEntity(null)).isNull();
        assertThat(SuperviseurStageDTO.toDto(null)).isNull();

        AppreciationGlobaleDTO appreciation = new AppreciationGlobaleDTO(
                5L, "Excellent", true, 3.5, ChoixAccueil.OUI, "Formation suffisante");
        assertThat(AppreciationGlobaleDTO.toDto(AppreciationGlobaleDTO.toEntity(appreciation))).isEqualTo(appreciation);
        assertThat(AppreciationGlobaleDTO.toEntity(null)).isNull();
        assertThat(AppreciationGlobaleDTO.toDto(null)).isNull();
    }

    @Test
    void convertitUneEvaluationCompleteDansLesDeuxSensEtGereNull() {
        Candidature candidature = new Candidature();
        candidature.setId(42L);
        EvaluationStagiaireDTO dto = new EvaluationStagiaireDTO(
                42L,
                new SuperviseurStageDTO("Ada", "Lovelace", "Gestionnaire", "514-555-1212"),
                new EvaluationProductiviteDTO(1L, 2L, 3L, 4L, 5L, "productivité"),
                new QualiteTravailDTO(1L, 2L, 3L, 4L, 5L, "travail"),
                new QualiteSocialeDTO(1L, 2L, 3L, 4L, 5L, 6L, "social"),
                new HabiletePersonelleDTO(1L, 2L, 3L, 4L, 5L, 6L, "habiletés"),
                new AppreciationGlobaleDTO(5L, "Excellent", true, 3.5, ChoixAccueil.OUI, "Suffisante"));

        EvaluationStagiaire entity = EvaluationStagiaireDTO.toEntity(candidature, dto);

        assertThat(entity.getCandidature()).isSameAs(candidature);
        assertThat(EvaluationStagiaireDTO.toDto(entity)).isEqualTo(dto);
        assertThat(EvaluationStagiaireDTO.toDto(null)).isNull();

        EvaluationStagiaireDTO dtoAvecChampsOptionnels = new EvaluationStagiaireDTO(
                42L, null, null, null, null, null, null);
        EvaluationStagiaire entityAvecChampsOptionnels =
                EvaluationStagiaireDTO.toEntity(candidature, dtoAvecChampsOptionnels);
        assertThat(entityAvecChampsOptionnels.getSuperviseur()).isNull();
        assertThat(EvaluationStagiaireDTO.toDto(entityAvecChampsOptionnels)).isEqualTo(dtoAvecChampsOptionnels);
    }

    @Test
    void exposeLesValeursDuResumeDeStageEtudiant() {
        EtudiantDTO etudiantDto = EtudiantDTO.empty();
        ResumeStageEtudiantDTO stage = new ResumeStageEtudiantDTO(
                12L, "Stage informatique", "Description", 22.5,
                null, null, "offre.pdf", null);
        StageEtudiantDTO dto = new StageEtudiantDTO(etudiantDto, stage);

        assertThat(dto.etudiant()).isSameAs(etudiantDto);
        assertThat(dto.stage()).isSameAs(stage);
    }
}