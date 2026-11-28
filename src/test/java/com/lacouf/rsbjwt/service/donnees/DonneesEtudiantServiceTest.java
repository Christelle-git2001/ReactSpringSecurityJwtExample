package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException;
import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Enum.StatutEtudiant;
import com.lacouf.rsbjwt.repository.CvEtudiantRepository;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.service.SessionService;
import com.lacouf.rsbjwt.service.dto.cv.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EtudiantDTO;

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

    @Mock
    EtudiantRepository etudiants;

    @Mock
    CvEtudiantRepository cvs;

    @Mock
    SessionService sessionService;

    DonneesEtudiantService service;

    Etudiant etudiant;

    @BeforeEach
    void preparer() {
        service = new DonneesEtudiantService(
                etudiants,
                cvs,
                sessionService
        );

        etudiant = Etudiant.builder()
                .firstName("Seb")
                .lastName("Test")
                .email("seb@test.local")
                .password("test")
                .phoneNumber("5145550100")
                .matricule("1234567")
                .department(Departement.INFORMATIQUE)
                .build();

        etudiant.setId(1L);

        lenient()
                .when(sessionService.filtrerUtilisateursParSessionActuelle(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        lenient()
                .when(sessionService.filtrerParSessionActuelle(anyList(), any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
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
        when(etudiants.countByCandidatureStageIsNullAndStatutNot(
                StatutEtudiant.STAGE_TROUVE
        )).thenReturn(3L);

        assertThat(service.compterEtudiantsSansStage()).isEqualTo(3);

        verify(etudiants)
                .countByCandidatureStageIsNullAndStatutNot(
                        StatutEtudiant.STAGE_TROUVE
                );

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
    void compteLesEtudiantsAvecStageChoisiOuStatutStageTrouve() {
        when(etudiants.countByCandidatureStageIsNotNullOrStatut(
                StatutEtudiant.STAGE_TROUVE
        )).thenReturn(5L);

        assertThat(service.compterEtudiantsAvecStage()).isEqualTo(5);

        verify(etudiants)
                .countByCandidatureStageIsNotNullOrStatut(
                        StatutEtudiant.STAGE_TROUVE
                );

        verifyNoMoreInteractions(etudiants);
    }

    @Test
    void compteLesCvPourChaqueStatutSansChargerLesCv()
            throws ParametreDonneesInvalideException {

        for (Statut statut : Statut.values()) {
            when(cvs.countByStatut(statut)).thenReturn(2L);

            assertThat(service.compterCvParStatut(statut))
                    .isEqualTo(2);

            verify(cvs).countByStatut(statut);
        }

        verifyNoMoreInteractions(cvs);
    }

    @Test
    void compteLesEtudiantsDuDepartementDemande()
            throws ParametreDonneesInvalideException {

        when(etudiants.countByDepartment(Departement.INFORMATIQUE))
                .thenReturn(8L);

        assertThat(
                service.compterEtudiantsParDepartement(
                        Departement.INFORMATIQUE
                )
        ).isEqualTo(8);

        verify(etudiants)
                .countByDepartment(Departement.INFORMATIQUE);

        verifyNoMoreInteractions(etudiants);
    }

    @Test
    void refuseUnStatutDeCvAbsent()
            throws ParametreDonneesInvalideException {

        assertThatExceptionOfType(
                ParametreDonneesInvalideException.class
        ).isThrownBy(() -> service.compterCvParStatut(null))
                .withMessage("chatbot.errors.cv_status_required");

        verifyNoInteractions(cvs);
    }

    @Test
    void refuseUnDepartementAbsent()
            throws ParametreDonneesInvalideException {

        assertThatExceptionOfType(
                ParametreDonneesInvalideException.class
        ).isThrownBy(() ->
                service.compterEtudiantsParDepartement(null)
        ).withMessage("chatbot.errors.department_required");

        verifyNoInteractions(etudiants);
    }

    @Test
    void refuseUnIdentifiantDeProfesseurAbsentNulOuNegatif()
            throws ParametreDonneesInvalideException {

        assertThatExceptionOfType(
                ParametreDonneesInvalideException.class
        ).isThrownBy(() ->
                service.obtenirEtudiantsParProfesseur(null)
        ).withMessage("chatbot.errors.invalid_professor");

        assertThatExceptionOfType(
                ParametreDonneesInvalideException.class
        ).isThrownBy(() ->
                service.obtenirEtudiantsParProfesseur(0L)
        );

        assertThatExceptionOfType(
                ParametreDonneesInvalideException.class
        ).isThrownBy(() ->
                service.obtenirEtudiantsParProfesseur(-1L)
        );

        verifyNoInteractions(etudiants);
    }

    @Test
    void retourneLesEtudiantsInscritsSousFormeDeDto() {
        when(etudiants.findAll())
                .thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsInscrits())
                .singleElement()
                .satisfies(dto -> {
                    assertThat(dto.id()).isEqualTo(1L);
                    assertThat(dto.firstName()).isEqualTo("Seb");
                    assertThat(dto.lastName()).isEqualTo("Test");
                    assertThat(dto.email()).isEqualTo("seb@test.local");
                });

        verify(etudiants).findAll();

        verify(sessionService)
                .filtrerUtilisateursParSessionActuelle(
                        List.of(etudiant)
                );
    }

    @Test
    void retourneLesEtudiantsInscritsFiltresParSession() {
        Etudiant autreEtudiant = mock(Etudiant.class);

        when(etudiants.findAll())
                .thenReturn(List.of(etudiant, autreEtudiant));

        when(sessionService.filtrerUtilisateursParSessionActuelle(
                List.of(etudiant, autreEtudiant)
        )).thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsInscrits())
                .extracting(EtudiantDTO::id)
                .containsExactly(1L);
    }

    @Test
    void retourneLesEtudiantsSansCvSousFormeDeDto() {
        when(etudiants.findByCvIsNull())
                .thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsSansCv())
                .singleElement()
                .satisfies(dto -> {
                    assertThat(dto.id()).isEqualTo(1L);
                    assertThat(dto.firstName()).isEqualTo("Seb");
                    assertThat(dto.email()).isEqualTo("seb@test.local");
                });

        verify(etudiants).findByCvIsNull();

        verify(sessionService)
                .filtrerUtilisateursParSessionActuelle(
                        List.of(etudiant)
                );
    }


    @Test
    void retourneLesEtudiantsSansStageAvecLeurStatut() {
        when(etudiants.findByCandidatureStageIsNullAndStatutNot(
                StatutEtudiant.STAGE_TROUVE
        )).thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsSansStage())
                .singleElement()
                .satisfies(dto -> {
                    assertThat(dto.id()).isEqualTo(1L);
                    assertThat(dto.statut())
                            .isEqualTo(StatutEtudiant.EN_RECHERCHE);
                });
    }


    @Test
    void retourneLesEtudiantsAvecStageSansExigerUnContratSigne() {
        etudiant.setStatut(StatutEtudiant.STAGE_TROUVE);

        when(etudiants.findByCandidatureStageIsNotNullOrStatut(
                StatutEtudiant.STAGE_TROUVE
        )).thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsAvecStage())
                .singleElement()
                .satisfies(dto -> {
                    assertThat(dto.id()).isEqualTo(1L);
                    assertThat(dto.statut())
                            .isEqualTo(StatutEtudiant.STAGE_TROUVE);
                });
    }


    @Test
    void retourneLesEtudiantsSansProfesseur() {
        when(etudiants.findByProfesseurIsNull())
                .thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsSansProfesseur())
                .singleElement()
                .satisfies(dto -> {
                    assertThat(dto.id()).isEqualTo(1L);
                    assertThat(dto.professeurId()).isNull();
                });
    }


    @Test
    void retourneLesCvEnAttenteAvecLeurEtudiant() {
        CvEtudiant cv = CvEtudiant.builder()
                .fileName("cv.pdf")
                .etudiant(etudiant)
                .build();

        cv.setId(2L);

        when(cvs.findByStatut(Statut.EN_ATTENTE))
                .thenReturn(List.of(cv));

        assertThat(service.obtenirCvEnAttente())
                .singleElement()
                .satisfies(dto -> {
                    assertThat(dto.id()).isEqualTo(2L);
                    assertThat(dto.statut())
                            .isEqualTo(Statut.EN_ATTENTE);
                    assertThat(dto.etudiant().id())
                            .isEqualTo(1L);
                });

        verify(cvs).findByStatut(Statut.EN_ATTENTE);

        verify(sessionService)
                .filtrerParSessionActuelle(
                        eq(List.of(cv)),
                        any()
                );
    }


    @Test
    void retourneLesEtudiantsDontLeCvEstRefuse() {
        when(etudiants.findByCvStatut(Statut.REFUSEE))
                .thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsAvecCvRefuse())
                .extracting(EtudiantDTO::id)
                .containsExactly(1L);

        verify(etudiants)
                .findByCvStatut(Statut.REFUSEE);
    }

    @Test
    void retourneLesEtudiantsSansEntrevue() {
        when(etudiants.findSansEntrevue())
                .thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsSansEntrevue())
                .extracting(EtudiantDTO::id)
                .containsExactly(1L);

        verify(etudiants).findSansEntrevue();

        verify(sessionService)
                .filtrerUtilisateursParSessionActuelle(
                        List.of(etudiant)
                );
    }

    @Test
    void retourneUniquementLesEtudiantsDuProfesseurDemande()
            throws ParametreDonneesInvalideException {

        when(etudiants.findAllByProfesseurId(7L))
                .thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsParProfesseur(7L))
                .extracting(EtudiantDTO::id)
                .containsExactly(1L);

        verify(etudiants).findAllByProfesseurId(7L);
    }


    @Test
    void retourneLesEtudiantsAyantTrouveStageFiltresParSession() {
        etudiant.setStatut(StatutEtudiant.STAGE_TROUVE);

        when(etudiants.findByStatut(StatutEtudiant.STAGE_TROUVE))
                .thenReturn(List.of(etudiant));

        assertThat(service.obtenirEtudiantsAyantTrouveStage())
                .singleElement()
                .satisfies(dto -> {
                    assertThat(dto.id()).isEqualTo(1L);
                    assertThat(dto.statut())
                            .isEqualTo(StatutEtudiant.STAGE_TROUVE);
                });

        verify(etudiants)
                .findByStatut(StatutEtudiant.STAGE_TROUVE);

        verify(sessionService)
                .filtrerUtilisateursParSessionActuelle(
                        List.of(etudiant)
                );
    }

    @Test
    void retourneDesListesVidesQuandAucunEtudiantOuCvNeCorrespond()
            throws ParametreDonneesInvalideException {

        when(etudiants.findAll()).thenReturn(List.of());
        when(etudiants.findByCvIsNull()).thenReturn(List.of());
        when(etudiants.findByCandidatureStageIsNullAndStatutNot(
                StatutEtudiant.STAGE_TROUVE
        )).thenReturn(List.of());

        when(etudiants.findByCandidatureStageIsNotNullOrStatut(
                StatutEtudiant.STAGE_TROUVE
        )).thenReturn(List.of());

        when(etudiants.findByProfesseurIsNull()).thenReturn(List.of());
        when(cvs.findByStatut(Statut.EN_ATTENTE)).thenReturn(List.of());
        when(etudiants.findByCvStatut(Statut.REFUSEE)).thenReturn(List.of());
        when(etudiants.findSansEntrevue()).thenReturn(List.of());
        when(etudiants.findAllByProfesseurId(7L)).thenReturn(List.of());
        when(etudiants.findByStatut(StatutEtudiant.STAGE_TROUVE))
                .thenReturn(List.of());

        assertThat(service.obtenirEtudiantsInscrits()).isEmpty();
        assertThat(service.obtenirEtudiantsSansCv()).isEmpty();
        assertThat(service.obtenirEtudiantsSansStage()).isEmpty();
        assertThat(service.obtenirEtudiantsAvecStage()).isEmpty();
        assertThat(service.obtenirEtudiantsSansProfesseur()).isEmpty();
        assertThat(service.obtenirCvEnAttente()).isEmpty();
        assertThat(service.obtenirEtudiantsAvecCvRefuse()).isEmpty();
        assertThat(service.obtenirEtudiantsSansEntrevue()).isEmpty();
        assertThat(service.obtenirEtudiantsParProfesseur(7L)).isEmpty();
        assertThat(service.obtenirEtudiantsAyantTrouveStage()).isEmpty();
    }

    @Test
    void retourneZeroQuandAucunEtudiantOuCvNeCorrespondAuxComptages()
            throws ParametreDonneesInvalideException {

        assertThat(service.compterEtudiantsInscrits()).isZero();
        assertThat(service.compterEtudiantsSansCv()).isZero();
        assertThat(service.compterEtudiantsSansStage()).isZero();
        assertThat(service.compterEtudiantsAvecStage()).isZero();
        assertThat(service.compterCvParStatut(Statut.EN_ATTENTE)).isZero();
        assertThat(
                service.compterEtudiantsParDepartement(
                        Departement.INFORMATIQUE
                )
        ).isZero();
    }
}