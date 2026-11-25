package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException;

import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Enum.Saison;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Enum.StatutEtudiant;
import com.lacouf.rsbjwt.repository.CvEtudiantRepository;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.service.SessionService;
import com.lacouf.rsbjwt.service.dto.session.SessionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonneesEtudiantServiceTest {
    @Mock EtudiantRepository etudiantRepository;
    @Mock CvEtudiantRepository cvEtudiantRepository;
    @Mock
    SessionService sessionService;
    Etudiant etudiant;

    @InjectMocks
    DonneesEtudiantService donneesEtudiantService;

    @BeforeEach
    void preparer() {
        etudiant = Etudiant.builder().firstName("Seb").lastName("Test").email("seb@test.local")
                .password("test").phoneNumber("5145550100").matricule("1234567")
                .department(Departement.INFORMATIQUE).build();
        etudiant.setId(1L);
    }

    @Test
    void compteLesEtudiantsSansCvSansChargerLaListe() {
        when(etudiantRepository.countByCvIsNull()).thenReturn(4L);

        assertThat(donneesEtudiantService.compterEtudiantsSansCv()).isEqualTo(4);
        verify(etudiantRepository).countByCvIsNull();
        verifyNoMoreInteractions(etudiantRepository);
    }

    @Test
    void compteLesEtudiantsSansStageEnExcluantLeStatutStageTrouve() {
        when(etudiantRepository.countByCandidatureStageIsNullAndStatutNot(StatutEtudiant.STAGE_TROUVE)).thenReturn(3L);

        assertThat(donneesEtudiantService.compterEtudiantsSansStage()).isEqualTo(3);
        verify(etudiantRepository).countByCandidatureStageIsNullAndStatutNot(StatutEtudiant.STAGE_TROUVE);
        verifyNoMoreInteractions(etudiantRepository);
    }

    @Test
    void compteTousLesEtudiantsInscrits() {
        when(etudiantRepository.count()).thenReturn(12L);

        assertThat(donneesEtudiantService.compterEtudiantsInscrits()).isEqualTo(12);
        verify(etudiantRepository).count();
        verifyNoMoreInteractions(etudiantRepository);
    }

    @Test
    void retourneTousLesEtudiantsInscritsSousFormeDeDto() {
        when(etudiantRepository.findAll()).thenReturn(List.of(etudiant));

        assertThat(donneesEtudiantService.obtenirEtudiantsInscrits()).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(1L);
            assertThat(dto.firstName()).isEqualTo("Seb");
            assertThat(dto.lastName()).isEqualTo("Test");
            assertThat(dto.email()).isEqualTo("seb@test.local");
        });
        verify(etudiantRepository).findAll();
        verifyNoMoreInteractions(etudiantRepository);
    }

    @Test
    void retourneLesEtudiantsSansCvSousFormeDeDto() {
        when(etudiantRepository.findByCvIsNull()).thenReturn(List.of(etudiant));

        assertThat(donneesEtudiantService.obtenirEtudiantsSansCv()).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(1L);
            assertThat(dto.firstName()).isEqualTo("Seb");
            assertThat(dto.email()).isEqualTo("seb@test.local");
        });
    }

    @Test
    void retourneLesEtudiantsSansStageAvecLeurStatut() {
        when(etudiantRepository.findByCandidatureStageIsNullAndStatutNot(StatutEtudiant.STAGE_TROUVE))
                .thenReturn(List.of(etudiant));

        assertThat(donneesEtudiantService.obtenirEtudiantsSansStage()).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(1L);
            assertThat(dto.statut()).isEqualTo(StatutEtudiant.EN_RECHERCHE);
        });
    }

    @Test
    void compteLesEtudiantsAvecStageChoisiOuStatutStageTrouve() {
        when(etudiantRepository.countByCandidatureStageIsNotNullOrStatut(StatutEtudiant.STAGE_TROUVE)).thenReturn(5L);

        assertThat(donneesEtudiantService.compterEtudiantsAvecStage()).isEqualTo(5);
        verify(etudiantRepository).countByCandidatureStageIsNotNullOrStatut(StatutEtudiant.STAGE_TROUVE);
        verifyNoMoreInteractions(etudiantRepository);
    }

    @Test
    void retourneLesEtudiantsAvecStageSansExigerUnContratSigne() {
        etudiant.setStatut(StatutEtudiant.STAGE_TROUVE);
        when(etudiantRepository.findByCandidatureStageIsNotNullOrStatut(StatutEtudiant.STAGE_TROUVE)).thenReturn(List.of(etudiant));

        assertThat(donneesEtudiantService.obtenirEtudiantsAvecStage()).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(1L);
            assertThat(dto.statut()).isEqualTo(StatutEtudiant.STAGE_TROUVE);
        });
    }

    @Test
    void retourneLesEtudiantsSansProfesseur() {
        when(etudiantRepository.findByProfesseurIsNull()).thenReturn(List.of(etudiant));

        assertThat(donneesEtudiantService.obtenirEtudiantsSansProfesseur()).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(1L);
            assertThat(dto.professeurId()).isNull();
        });
    }

    @Test
    void retourneLesCvEnAttenteAvecLeurEtudiant() {
        CvEtudiant cv = CvEtudiant.builder().fileName("cv.pdf").etudiant(etudiant).build();
        cv.setId(2L);
        when(cvEtudiantRepository.findByStatut(Statut.EN_ATTENTE)).thenReturn(List.of(cv));

        assertThat(donneesEtudiantService.obtenirCvEnAttente()).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(2L);
            assertThat(dto.statut()).isEqualTo(Statut.EN_ATTENTE);
            assertThat(dto.etudiant().id()).isEqualTo(1L);
        });
    }

    @Test
    void compteLesCvPourChaqueStatutSansChargerLesCv() throws ParametreDonneesInvalideException {
        for (Statut statut : Statut.values()) {
            when(cvEtudiantRepository.countByStatut(statut)).thenReturn(2L);
            assertThat(donneesEtudiantService.compterCvParStatut(statut)).isEqualTo(2);
            verify(cvEtudiantRepository).countByStatut(statut);
        }
        verifyNoMoreInteractions(cvEtudiantRepository);
    }

    @Test
    void refuseUnStatutDeCvAbsent() throws ParametreDonneesInvalideException {
        assertThatExceptionOfType(ParametreDonneesInvalideException.class).isThrownBy(() -> donneesEtudiantService.compterCvParStatut(null))
                .withMessage("chatbot.errors.cv_status_required");
        verifyNoInteractions(cvEtudiantRepository);
    }

    @Test
    void retourneLesEtudiantsDontLeCvEstRefuse() {
        when(etudiantRepository.findByCvStatut(Statut.REFUSEE)).thenReturn(List.of(etudiant));

        assertThat(donneesEtudiantService.obtenirEtudiantsAvecCvRefuse()).extracting(dto -> dto.id()).containsExactly(1L);
        verify(etudiantRepository).findByCvStatut(Statut.REFUSEE);
    }

    @Test
    void compteLesEtudiantsDuDepartementDemande() throws ParametreDonneesInvalideException {
        when(etudiantRepository.countByDepartment(Departement.INFORMATIQUE)).thenReturn(8L);

        assertThat(donneesEtudiantService.compterEtudiantsParDepartement(Departement.INFORMATIQUE)).isEqualTo(8);
        verify(etudiantRepository).countByDepartment(Departement.INFORMATIQUE);
        verifyNoMoreInteractions(etudiantRepository);
    }

    @Test
    void refuseUnDepartementAbsent() throws ParametreDonneesInvalideException {
        assertThatExceptionOfType(ParametreDonneesInvalideException.class).isThrownBy(() -> donneesEtudiantService.compterEtudiantsParDepartement(null))
                .withMessage("chatbot.errors.department_required");
        verifyNoInteractions(etudiantRepository);
    }

    @Test
    void retourneLesEtudiantsSansEntrevue() {
        when(etudiantRepository.findSansEntrevue()).thenReturn(List.of(etudiant));

        assertThat(donneesEtudiantService.obtenirEtudiantsSansEntrevue()).extracting(dto -> dto.id()).containsExactly(1L);
        verify(etudiantRepository).findSansEntrevue();
    }

    @Test
    void retourneUniquementLesEtudiantsDuProfesseurDemande() throws ParametreDonneesInvalideException {
        when(etudiantRepository.findAllByProfesseurId(7L)).thenReturn(List.of(etudiant));

        assertThat(donneesEtudiantService.obtenirEtudiantsParProfesseur(7L)).extracting(dto -> dto.id()).containsExactly(1L);
        verify(etudiantRepository).findAllByProfesseurId(7L);
    }

    @Test
    void refuseUnIdentifiantDeProfesseurAbsentNulOuNegatif() throws ParametreDonneesInvalideException {
        assertThatExceptionOfType(ParametreDonneesInvalideException.class).isThrownBy(() -> donneesEtudiantService.obtenirEtudiantsParProfesseur(null))
                .withMessage("chatbot.errors.invalid_professor");
        assertThatExceptionOfType(ParametreDonneesInvalideException.class).isThrownBy(() -> donneesEtudiantService.obtenirEtudiantsParProfesseur(0L));
        assertThatExceptionOfType(ParametreDonneesInvalideException.class).isThrownBy(() -> donneesEtudiantService.obtenirEtudiantsParProfesseur(-1L));
        verifyNoInteractions(etudiantRepository);
    }

    @Test
    void retourneDesListesVidesQuandAucunEtudiantOuCvNeCorrespond() throws ParametreDonneesInvalideException {
        assertThat(donneesEtudiantService.obtenirEtudiantsInscrits()).isEmpty();
        assertThat(donneesEtudiantService.obtenirEtudiantsSansCv()).isEmpty();
        assertThat(donneesEtudiantService.obtenirEtudiantsSansStage()).isEmpty();
        assertThat(donneesEtudiantService.obtenirEtudiantsAvecStage()).isEmpty();
        assertThat(donneesEtudiantService.obtenirEtudiantsSansProfesseur()).isEmpty();
        assertThat(donneesEtudiantService.obtenirCvEnAttente()).isEmpty();
        assertThat(donneesEtudiantService.obtenirEtudiantsAvecCvRefuse()).isEmpty();
        assertThat(donneesEtudiantService.obtenirEtudiantsSansEntrevue()).isEmpty();
        assertThat(donneesEtudiantService.obtenirEtudiantsParProfesseur(7L)).isEmpty();
    }

    @Test
    void retourneZeroQuandAucunEtudiantOuCvNeCorrespondAuxComptages() throws ParametreDonneesInvalideException {
        assertThat(donneesEtudiantService.compterEtudiantsInscrits()).isZero();
        assertThat(donneesEtudiantService.compterEtudiantsSansCv()).isZero();
        assertThat(donneesEtudiantService.compterEtudiantsSansStage()).isZero();
        assertThat(donneesEtudiantService.compterEtudiantsAvecStage()).isZero();
        assertThat(donneesEtudiantService.compterCvParStatut(Statut.EN_ATTENTE)).isZero();
        assertThat(donneesEtudiantService.compterEtudiantsParDepartement(Departement.INFORMATIQUE)).isZero();
    }

    @Test
    void compteLesEtudiantsInscritsDansLaSessionActuelle() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        Etudiant horsSession = Etudiant.builder().firstName("Autre").lastName("HorsSession")
                .email("autre@test.local").password("test").phoneNumber("5145550101")
                .matricule("7654321").department(Departement.INFORMATIQUE).build();
        horsSession.setId(2L);

        List<Etudiant> tous = List.of(etudiant, horsSession);
        when(etudiantRepository.findAll()).thenReturn(tous);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerUtilisateursParSession(tous, Saison.AUTOMNE, 2026))
                .thenReturn(List.of(etudiant));

        assertThat(donneesEtudiantService.countEtudiantsInscritsSessionActuelle()).isEqualTo(1L);
        verify(etudiantRepository).findAll();
        verify(sessionService).filtrerUtilisateursParSession(tous, Saison.AUTOMNE, 2026);
        verifyNoMoreInteractions(etudiantRepository);
    }

    @Test
    void compteLesCvEnAttenteDansLaSessionActuelle() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        CvEtudiant cvSession = CvEtudiant.builder()
                .fileName("cv1.pdf")
                .uploadDate(LocalDateTime.of(2026, 9, 15, 10, 0))
                .etudiant(etudiant)
                .build();
        cvSession.setId(1L);

        CvEtudiant cvHorsSession = CvEtudiant.builder()
                .fileName("cv2.pdf")
                .uploadDate(LocalDateTime.of(2026, 2, 15, 10, 0))
                .etudiant(etudiant)
                .build();
        cvHorsSession.setId(2L);

        List<CvEtudiant> cvEnAttente = List.of(cvSession, cvHorsSession);
        when(cvEtudiantRepository.findByStatut(Statut.EN_ATTENTE)).thenReturn(cvEnAttente);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerParSession(eq(cvEnAttente), eq(Saison.AUTOMNE), eq(2026), any()))
                .thenReturn(List.of(cvSession));

        assertThat(donneesEtudiantService.countEtudiantsCvEnAttenteSessionActuelle()).isEqualTo(1L);
        verify(cvEtudiantRepository).findByStatut(Statut.EN_ATTENTE);
        verify(sessionService).filtrerParSession(eq(cvEnAttente), eq(Saison.AUTOMNE), eq(2026), any());
        verifyNoMoreInteractions(cvEtudiantRepository);
    }

    @Test
    void compteLesEtudiantsSansStageDansLaSessionActuelle() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        List<Etudiant> sansStage = List.of(etudiant);

        when(etudiantRepository.findByCandidatureStageIsNullAndStatutNot(StatutEtudiant.STAGE_TROUVE))
                .thenReturn(sansStage);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerUtilisateursParSession(sansStage, Saison.AUTOMNE, 2026))
                .thenReturn(sansStage);

        assertThat(donneesEtudiantService.countEtudiantSansStageSessionActuelle()).isEqualTo(1L);
        verify(etudiantRepository).findByCandidatureStageIsNullAndStatutNot(StatutEtudiant.STAGE_TROUVE);
        verify(sessionService).filtrerUtilisateursParSession(sansStage, Saison.AUTOMNE, 2026);
        verifyNoMoreInteractions(etudiantRepository);
    }

    @Test
    void compteLesEtudiantsAvecStageDansLaSessionActuelle() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        List<Etudiant> avecStage = List.of(etudiant);

        when(etudiantRepository.findByCandidatureStageIsNotNullOrStatut(StatutEtudiant.STAGE_TROUVE))
                .thenReturn(avecStage);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerUtilisateursParSession(avecStage, Saison.AUTOMNE, 2026))
                .thenReturn(avecStage);

        assertThat(donneesEtudiantService.countEtudiantAvecStageSessionActuelle()).isEqualTo(1L);
        verify(etudiantRepository).findByCandidatureStageIsNotNullOrStatut(StatutEtudiant.STAGE_TROUVE);
        verify(sessionService).filtrerUtilisateursParSession(avecStage, Saison.AUTOMNE, 2026);
        verifyNoMoreInteractions(etudiantRepository);
    }
}