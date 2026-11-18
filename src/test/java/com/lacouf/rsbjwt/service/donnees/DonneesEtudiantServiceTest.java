package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.Exception.ParametreDonneesInvalideException;

import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Enum.StatutEtudiant;
import com.lacouf.rsbjwt.repository.CvEtudiantRepository;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonneesEtudiantServiceTest {
    @Mock EtudiantRepository etudiants;
    @Mock CvEtudiantRepository cvs;
    DonneesEtudiantService service;
    Etudiant etudiant;

    @BeforeEach
    void preparer() {
        service = new DonneesEtudiantService(etudiants, cvs);
        etudiant = Etudiant.builder().firstName("Seb").lastName("Test").email("seb@test.local")
                .password("test").phoneNumber("5145550100").matricule("1234567")
                .department(Departement.INFORMATIQUE).build();
        etudiant.setId(1L);
    }

    @Test
    void compteLesEtudiantsSansCvSansChargerLaListe() {
        when(etudiants.countByCvIsNull()).thenReturn(4L);

        assertThat(service.compterEtudiantsSansCv()).isEqualTo(4);
        verify(etudiants).countByCvIsNull();
        verifyNoMoreInteractions(etudiants);
    }

    @Test
    void compteLesEtudiantsSansStageEnExcluantLeStatutStageTrouve() {
        when(etudiants.countByCandidatureStageIsNullAndStatutNot(StatutEtudiant.STAGE_TROUVE)).thenReturn(3L);

        assertThat(service.compterEtudiantsSansStage()).isEqualTo(3);
        verify(etudiants).countByCandidatureStageIsNullAndStatutNot(StatutEtudiant.STAGE_TROUVE);
        verifyNoMoreInteractions(etudiants);
    }

    @Test
    void compteTousLesEtudiantsInscrits() {
        when(etudiants.count()).thenReturn(12L);

        assertThat(service.compterEtudiantsInscrits()).isEqualTo(12);
        verify(etudiants).count();
        verifyNoMoreInteractions(etudiants);
    }

    @Test
    void retourneLesEtudiantsSansCvSousFormeDeDto() {
        when(etudiants.findByCvIsNull()).thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsSansCv()).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(1L);
            assertThat(dto.firstName()).isEqualTo("Seb");
            assertThat(dto.email()).isEqualTo("seb@test.local");
        });
    }

    @Test
    void retourneLesEtudiantsSansStageAvecLeurStatut() {
        when(etudiants.findByCandidatureStageIsNullAndStatutNot(StatutEtudiant.STAGE_TROUVE))
                .thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsSansStage()).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(1L);
            assertThat(dto.statut()).isEqualTo(StatutEtudiant.EN_RECHERCHE);
        });
    }

    @Test
    void compteLesEtudiantsAvecStageChoisiOuStatutStageTrouve() {
        when(etudiants.countByCandidatureStageIsNotNullOrStatut(StatutEtudiant.STAGE_TROUVE)).thenReturn(5L);

        assertThat(service.compterEtudiantsAvecStage()).isEqualTo(5);
        verify(etudiants).countByCandidatureStageIsNotNullOrStatut(StatutEtudiant.STAGE_TROUVE);
        verifyNoMoreInteractions(etudiants);
    }

    @Test
    void retourneLesEtudiantsAvecStageSansExigerUnContratSigne() {
        etudiant.setStatut(StatutEtudiant.STAGE_TROUVE);
        when(etudiants.findByCandidatureStageIsNotNullOrStatut(StatutEtudiant.STAGE_TROUVE)).thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsAvecStage()).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(1L);
            assertThat(dto.statut()).isEqualTo(StatutEtudiant.STAGE_TROUVE);
        });
    }

    @Test
    void retourneLesEtudiantsSansProfesseur() {
        when(etudiants.findByProfesseurIsNull()).thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsSansProfesseur()).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(1L);
            assertThat(dto.professeurId()).isNull();
        });
    }

    @Test
    void retourneLesCvEnAttenteAvecLeurEtudiant() {
        CvEtudiant cv = CvEtudiant.builder().fileName("cv.pdf").etudiant(etudiant).build();
        cv.setId(2L);
        when(cvs.findByStatut(Statut.EN_ATTENTE)).thenReturn(List.of(cv));

        assertThat(service.obtenirCvEnAttente()).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(2L);
            assertThat(dto.statut()).isEqualTo(Statut.EN_ATTENTE);
            assertThat(dto.etudiant().id()).isEqualTo(1L);
        });
    }

    @Test
    void compteLesCvPourChaqueStatutSansChargerLesCv() throws ParametreDonneesInvalideException {
        for (Statut statut : Statut.values()) {
            when(cvs.countByStatut(statut)).thenReturn(2L);
            assertThat(service.compterCvParStatut(statut)).isEqualTo(2);
            verify(cvs).countByStatut(statut);
        }
        verifyNoMoreInteractions(cvs);
    }

    @Test
    void refuseUnStatutDeCvAbsent() throws ParametreDonneesInvalideException {
        assertThatExceptionOfType(ParametreDonneesInvalideException.class).isThrownBy(() -> service.compterCvParStatut(null))
                .withMessage("chatbot.errors.cv_status_required");
        verifyNoInteractions(cvs);
    }

    @Test
    void retourneLesEtudiantsDontLeCvEstRefuse() {
        when(etudiants.findByCvStatut(Statut.REFUSEE)).thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsAvecCvRefuse()).extracting(dto -> dto.id()).containsExactly(1L);
        verify(etudiants).findByCvStatut(Statut.REFUSEE);
    }

    @Test
    void compteLesEtudiantsDuDepartementDemande() throws ParametreDonneesInvalideException {
        when(etudiants.countByDepartment(Departement.INFORMATIQUE)).thenReturn(8L);

        assertThat(service.compterEtudiantsParDepartement(Departement.INFORMATIQUE)).isEqualTo(8);
        verify(etudiants).countByDepartment(Departement.INFORMATIQUE);
        verifyNoMoreInteractions(etudiants);
    }

    @Test
    void refuseUnDepartementAbsent() throws ParametreDonneesInvalideException {
        assertThatExceptionOfType(ParametreDonneesInvalideException.class).isThrownBy(() -> service.compterEtudiantsParDepartement(null))
                .withMessage("chatbot.errors.department_required");
        verifyNoInteractions(etudiants);
    }

    @Test
    void retourneLesEtudiantsSansEntrevue() {
        when(etudiants.findSansEntrevue()).thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsSansEntrevue()).extracting(dto -> dto.id()).containsExactly(1L);
        verify(etudiants).findSansEntrevue();
    }

    @Test
    void retourneUniquementLesEtudiantsDuProfesseurDemande() throws ParametreDonneesInvalideException {
        when(etudiants.findAllByProfesseurId(7L)).thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsParProfesseur(7L)).extracting(dto -> dto.id()).containsExactly(1L);
        verify(etudiants).findAllByProfesseurId(7L);
    }

    @Test
    void refuseUnIdentifiantDeProfesseurAbsentNulOuNegatif() throws ParametreDonneesInvalideException {
        assertThatExceptionOfType(ParametreDonneesInvalideException.class).isThrownBy(() -> service.obtenirEtudiantsParProfesseur(null))
                .withMessage("chatbot.errors.invalid_professor");
        assertThatExceptionOfType(ParametreDonneesInvalideException.class).isThrownBy(() -> service.obtenirEtudiantsParProfesseur(0L));
        assertThatExceptionOfType(ParametreDonneesInvalideException.class).isThrownBy(() -> service.obtenirEtudiantsParProfesseur(-1L));
        verifyNoInteractions(etudiants);
    }

    @Test
    void retourneDesListesVidesQuandAucunEtudiantOuCvNeCorrespond() throws ParametreDonneesInvalideException {
        assertThat(service.obtenirEtudiantsSansCv()).isEmpty();
        assertThat(service.obtenirEtudiantsSansStage()).isEmpty();
        assertThat(service.obtenirEtudiantsAvecStage()).isEmpty();
        assertThat(service.obtenirEtudiantsSansProfesseur()).isEmpty();
        assertThat(service.obtenirCvEnAttente()).isEmpty();
        assertThat(service.obtenirEtudiantsAvecCvRefuse()).isEmpty();
        assertThat(service.obtenirEtudiantsSansEntrevue()).isEmpty();
        assertThat(service.obtenirEtudiantsParProfesseur(7L)).isEmpty();
    }

    @Test
    void retourneZeroQuandAucunEtudiantOuCvNeCorrespondAuxComptages() throws ParametreDonneesInvalideException {
        assertThat(service.compterEtudiantsInscrits()).isZero();
        assertThat(service.compterEtudiantsSansCv()).isZero();
        assertThat(service.compterEtudiantsSansStage()).isZero();
        assertThat(service.compterEtudiantsAvecStage()).isZero();
        assertThat(service.compterCvParStatut(Statut.EN_ATTENTE)).isZero();
        assertThat(service.compterEtudiantsParDepartement(Departement.INFORMATIQUE)).isZero();
    }
}