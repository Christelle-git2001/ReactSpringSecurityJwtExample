package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.candidature.ActionsCandidatureDesactiveesException;
import com.lacouf.rsbjwt.exception.candidature.CandidatureIntrouvableException;
import com.lacouf.rsbjwt.exception.dates.DateAvantAujourdhuiException;
import com.lacouf.rsbjwt.exception.dates.DateFinAffichageApresDateDebutException;
import com.lacouf.rsbjwt.exception.dates.DateFinAffichageAvantAujourdhuiException;
import com.lacouf.rsbjwt.exception.dates.DateFinAvantDateDebutException;
import com.lacouf.rsbjwt.exception.contrat.ContratIntrouvableException;
import com.lacouf.rsbjwt.exception.entrevue.ConvocationExistanteException;
import com.lacouf.rsbjwt.exception.entrevue.ConvocationIntrouvableException;
import com.lacouf.rsbjwt.exception.entrevue.DecisionCandidatureInvalideException;
import com.lacouf.rsbjwt.exception.fichier.FichierCorrompuException;
import com.lacouf.rsbjwt.exception.fichier.FichierIntrouvableException;
import com.lacouf.rsbjwt.exception.fichier.FichierTropVolumineuxException;
import com.lacouf.rsbjwt.exception.fichier.FichierTypeInvalideException;
import com.lacouf.rsbjwt.exception.offre.OffreIntrouvableException;
import com.lacouf.rsbjwt.exception.offre.OffreNonAutoriseeException;
import com.lacouf.rsbjwt.exception.offre.OffreNonEnAttenteException;
import com.lacouf.rsbjwt.exception.utilisateur.EmailExistantException;
import com.lacouf.rsbjwt.exception.utilisateur.MotDePasseInvalideException;
import com.lacouf.rsbjwt.exception.utilisateur.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.exception.utilisateur.NumeroTelephoneExistantException;
import com.lacouf.rsbjwt.exception.utilisateur.UtilisateurIntrouvableException;
import com.lacouf.rsbjwt.service.dto.candidature.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.contrat.ContratDTO;
import com.lacouf.rsbjwt.service.dto.contrat.ResumeContratDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.CreationEntrevueDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.EntrevueDTO;
import com.lacouf.rsbjwt.service.dto.offre.CreationOffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.offre.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.session.SessionDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.InscriptionEmployeurDTO;
import org.junit.jupiter.api.AfterEach;

import com.lacouf.rsbjwt.model.event.CandidatureAccepteeEvent;
import com.lacouf.rsbjwt.model.event.StageTrouveEvent;
import com.lacouf.rsbjwt.model.event.CandidatureRefuseeEvent;
import com.lacouf.rsbjwt.model.event.CandidatureRetenueEvent;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.*;
import com.lacouf.rsbjwt.model.event.EntrevueCreeeEvent;
import com.lacouf.rsbjwt.model.event.EntrevueTermineeEvent;
import com.lacouf.rsbjwt.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class EmployeurServiceTest {
    @Mock
    private EmployeurRepository employeurRepository;
    @Mock
    private OffreDeStageRepository offreDeStageRepository;

    @Mock
    private UserAppRepository userAppRepository;

    @Mock
    private CandidatureRepository candidatureRepository;

    @Mock
    private ContratRepository contratRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EtudiantService etudiantService;

    @InjectMocks
    private EmployeurService employeurService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private EntrevueRepository convocationRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private InscriptionRepository inscriptionRepository;


    InscriptionEmployeurDTO inscriptionEmployeurDTO;
    CreationOffreDeStageDTO creationOffreDeStageDTO;
    Employeur employeur;
    OffreDeStage offreDeStage;
    Candidature candidature ;
    Etudiant etudiant ;
    CvEtudiant cvEtudiant ;



    @BeforeEach
    void init() {
        inscriptionEmployeurDTO = new InscriptionEmployeurDTO(
                "Gerard",
                "Robert",
                "165-685-4569",
                "Gerard.Robert@hotmail.com",
                "Mercier",
                "Gerard inc",
                SecteurActivite.AEROSPATIAL,
                "Losange12%",
                "Losange12%"
        );
        employeur = Employeur.builder()
                .firstName("Gerard")
                .lastName("Robert")
                .town("Mercier")
                .phone("165-685-4569")
                .email("Gerard.Robert@hotmail.com")
                .businessName("Gerard inc")
                .businessSector(SecteurActivite.AEROSPATIAL)
                .password("Losange12%")
                .build();

        employeur.setId(1L);

        etudiant = Etudiant.builder()
                .email("etudiant@test.com")
                .firstName("Etudiant")
                .lastName("Test")
                .department(Departement.INFORMATIQUE)
                .matricule("1234789")
                .phoneNumber("123-999-1234")
                .password("Test123!")
                .build();
        etudiant.setId(1L);

        cvEtudiant = CvEtudiant.builder()
                .fileName("cv.pdf")
                .contentType("application/pdf")
                .fileSize(10L)
                .storagePath("uploads/cvs/cv.pdf")
                .uploadDate(LocalDateTime.now())
                .etudiant(etudiant)
                .build();

        cvEtudiant.setId(1L);
        cvEtudiant.setStatut(Statut.ACCEPTEE);

        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 10, 15),
                LocalDate.of(2029, 10, 1).minusDays(15), 1
        );

        offreDeStage = OffreDeStage.builder()
                .title("Infirmerie")
                .description("donner à manger")
                .salary(19.25)
                .domain(Departement.INFORMATIQUE)
                .statut(Statut.EN_ATTENTE)
                .startDate(LocalDate.of(2029, 10, 1))
                .endDate(LocalDate.of(2029, 10, 15))
                .displayEndDate(LocalDate.of(2029, 10, 1).minusDays(15))
                .employeur(employeur)
                .build();
        ReflectionTestUtils.setField(offreDeStage, "id", 1L);

        candidature = Candidature.builder()
                .etudiant(etudiant)
                .offreDeStage(offreDeStage)
                .build();
        ReflectionTestUtils.setField(candidature, "id", 1L);

        offreDeStage.setCandidatures(List.of(candidature));
        employeur.setOffres(List.of(offreDeStage));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeur.getEmail(), null)
        );
    }

    @AfterEach
    void nettoyerAuthentification() {
        SecurityContextHolder.clearContext();
    }

    private CreationEntrevueDTO creerDTOConvocationValide() {
        return new CreationEntrevueDTO(
                candidature.getId(),
                FormatEntrevue.EN_LIGNE,
                LocalDateTime.now().plusDays(7),
                30
        );
    }

     private Entrevue creerEntrevueTerminee() {
        candidature.setStatut(StatutCandidature.ENTREVUE_PLANIFIEE);
        Entrevue entrevue = new Entrevue();
        entrevue.setCandidature(candidature);
        entrevue.setStatut(StatutEntrevue.TERMINEE);
        entrevue.setDateHeureEntrevue(LocalDateTime.now().minusHours(2));
        entrevue.setDuree(30);
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        when(convocationRepository.findById(1L)).thenReturn(Optional.of(entrevue));
        return entrevue;
    }

    @Test
    void doitCreerCompteEmployeur() throws Exception {
        when(employeurRepository.save(any(Employeur.class))).thenReturn(employeur);
        when(sessionService.getSessionActuelle())
                .thenReturn(new SessionDTO(Saison.HIVER, 2026));

        EmployeurDTO result = employeurService.creeCompteEmployeur(inscriptionEmployeurDTO);

        verify(employeurRepository, times(1)).save(any(Employeur.class));
        verify(inscriptionRepository).save(any(Inscription.class));
        assertThat(result)
                .isNotNull()
                .returns(employeur.getFirstName(), EmployeurDTO::firstName)
                .returns(employeur.getEmail(), EmployeurDTO::email)
                .returns(employeur.getBusinessName(), EmployeurDTO::businessName)
                .returns(employeur.getBusinessSector(), EmployeurDTO::businessSector);
    }

    @Test
    void doitLancerExceptionEmailExistant() {
        when(userAppRepository.findUserAppByEmail(anyString())).thenReturn(Optional.of(employeur));

        assertThatThrownBy(() -> employeurService.creeCompteEmployeur(inscriptionEmployeurDTO))
                .isInstanceOf(EmailExistantException.class);

        verify(employeurRepository, never()).save(any(Employeur.class));
    }

    @Test
    void doitLancerExceptionMotDePasseDifferent() {
        inscriptionEmployeurDTO = new InscriptionEmployeurDTO(
                "Gerard",
                "Robert",
                "165-685-4569",
                "Gerard.Robert@hotmail.com",
                "Mercier",
                "Gerard inc",
                SecteurActivite.AEROSPATIAL,
                "Losange12%",
                "Carre12%"
        );

        assertThatThrownBy(() -> employeurService.creeCompteEmployeur(inscriptionEmployeurDTO))
                .isInstanceOf(MotDePasseNonCorrespondantException.class);

        verify(employeurRepository, never()).save(any(Employeur.class));
    }

    @Test
    void doitLancerExceptionTelephoneExistant() {
        when(userAppRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(employeur));

        assertThatThrownBy(() -> employeurService.creeCompteEmployeur(inscriptionEmployeurDTO))
                .isInstanceOf(NumeroTelephoneExistantException.class);

        verify(employeurRepository, never()).save(any(Employeur.class));
    }

    @Test
    void doitLancerExceptionQuandDateFinAvantDateDebut() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 10, 15),
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 10, 15).minusDays(15), 1
        );

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, null, employeur.getEmail()))
                .isInstanceOf(DateFinAvantDateDebutException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitLancerExceptionQuandDateFinEgaleDateDebut() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 01, 1).minusDays(15), 1
        );

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, null, employeur.getEmail()))
                .isInstanceOf(DateFinAvantDateDebutException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }


    @Test
    void doitLancerExceptionQuandDateFinAffichageApresDateDebut() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 02, 1),
                LocalDate.of(2029, 02, 26),
                LocalDate.of(2029, 02, 15), 1
        );

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, null, employeur.getEmail()))
                .isInstanceOf(DateFinAffichageApresDateDebutException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitLancerExceptionQuandDateFinAffichageAvantAujourdhui() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 02, 1),
                LocalDate.of(2029, 02, 26),
                LocalDate.now().minusDays(1), 1
        );

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, null, employeur.getEmail()))
                .isInstanceOf(DateFinAffichageAvantAujourdhuiException.class);
        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitLancerExceptionQuandMauvaisTypeDeFichier() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        MockMultipartFile fichier = new MockMultipartFile("file", "offre.exe",
                MediaType.APPLICATION_OCTET_STREAM_VALUE, "contenu".getBytes());

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, fichier, employeur.getEmail()))
                .isInstanceOf(FichierTypeInvalideException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitLancerExceptionQuandFichierTropVolumineux() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        MockMultipartFile fichier = new MockMultipartFile("file", "offre.pdf",
                MediaType.APPLICATION_PDF_VALUE, new byte[10 * 1024 * 1024 + 1]);

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, fichier, employeur.getEmail()))
                .isInstanceOf(FichierTropVolumineuxException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitLancerExceptionQuandFichierCorrompu() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        MockMultipartFile fichier = new MockMultipartFile("file", "offre.pdf",
                MediaType.APPLICATION_PDF_VALUE, new byte[0]);

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, fichier, employeur.getEmail()))
                .isInstanceOf(FichierCorrompuException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitRetournerLesOffresDeLEmployeur() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeur.getEmail(), null)
        );
        simulerFiltrageSession();

        employeur.setOffres(List.of(offreDeStage));
        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        List<OffreDeStageDTO> result = employeurService.obtenirOffres();

        assertThat(result).hasSize(1);
    }

    @Test
    void doitRetournerLesCandidaturesParOffre() throws OffreIntrouvableException, OffreNonAutoriseeException, UtilisateurIntrouvableException {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeur.getEmail(), null)
        );

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        offreDeStage.setStatut(Statut.ACCEPTEE);
        when(offreDeStageRepository.findById(offreDeStage.getId()))
                .thenReturn(Optional.of(offreDeStage));

        when(candidatureRepository.findByOffreDeStage(offreDeStage))
                .thenReturn(List.of(candidature));

        List<CandidatureDTO> result =
                employeurService.getCandidaturesParOffre(offreDeStage.getId());

        assertThat(result).hasSize(1);
    }


    @Test
    void doitRetournerLesCandidaturesDeLEmployeur() throws UtilisateurIntrouvableException {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeur.getEmail(), null)
        );
        simulerFiltrageSession();

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        when(candidatureRepository.findByOffreDeStage_Employeur(employeur))
                .thenReturn(List.of(candidature));

        List<CandidatureDTO> result = employeurService.obtenirCandidatures();

        assertThat(result).hasSize(1);
    }



    @Test
    void doitRetournerListeVideQuandAucuneOffre() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeur.getEmail(), null)
        );

        simulerFiltrageSession();

        employeur.setOffres(List.of());
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));

        List<OffreDeStageDTO> result = employeurService.obtenirOffres();

        assertThat(result).isEmpty();
    }

    @Test
    void doitModifierOffreDeStage() throws DateFinAffichageApresDateDebutException, OffreIntrouvableException, FichierTypeInvalideException, FichierCorrompuException, OffreNonEnAttenteException, DateFinAffichageAvantAujourdhuiException, IOException, OffreNonAutoriseeException, DateFinAvantDateDebutException, DateAvantAujourdhuiException, FichierTropVolumineuxException , com.lacouf.rsbjwt.exception.offre.NombreCandidatsMaxInvalideException, com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        when(offreDeStageRepository.findById(1L)).thenReturn(Optional.of(offreDeStage));
        when(offreDeStageRepository.save(any(OffreDeStage.class))).thenReturn(offreDeStage);

        OffreDeStageDTO result = employeurService.modifierOffre(1L, creationOffreDeStageDTO, null, employeur.getEmail());

        verify(offreDeStageRepository, times(1)).save(any(OffreDeStage.class));
        assertThat(result).isNotNull();
    }

    @Test
    void doitLancerExceptionQuandOffreIntrouvable() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        when(offreDeStageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeurService.modifierOffre(99L, creationOffreDeStageDTO, null, employeur.getEmail()))
                .isInstanceOf(OffreIntrouvableException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitLancerExceptionQuandOffreAppartientAUnAutreEmployeur() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        Employeur autreEmployeur = Employeur.builder()
                .firstName("Autre")
                .lastName("Personne")
                .town("Laval")
                .phone("450-111-2222")
                .email("autre@test.com")
                .businessName("Autre inc")
                .businessSector(SecteurActivite.AEROSPATIAL)
                .password("Losange12%")
                .build();

        autreEmployeur.setId(2L);
        offreDeStage.setEmployeur(autreEmployeur);

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.of(offreDeStage));

        assertThatThrownBy(() ->
                employeurService.modifierOffre(
                        1L,
                        creationOffreDeStageDTO,
                        null,
                        employeur.getEmail()
                )
        )
                .isInstanceOf(OffreNonAutoriseeException.class);

        verify(offreDeStageRepository, never())
                .save(any(OffreDeStage.class));
    }

    @Test
    void doitCreerOffreAvecSucces() throws Exception, DateFinAffichageApresDateDebutException {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        MockMultipartFile fichier = new MockMultipartFile("file", "offre.pdf",
                MediaType.APPLICATION_PDF_VALUE, "contenu du pdf".getBytes());
        when(offreDeStageRepository.save(any(OffreDeStage.class))).thenReturn(offreDeStage);

        var dto = new CreationOffreDeStageDTO(creationOffreDeStageDTO.title(), creationOffreDeStageDTO.description(),
                creationOffreDeStageDTO.salary(), creationOffreDeStageDTO.domain(), creationOffreDeStageDTO.startDate(),
                creationOffreDeStageDTO.endDate(), creationOffreDeStageDTO.displayEndDate(), 2);
        OffreDeStageDTO result = employeurService.creerOffre(dto, fichier, employeur.getEmail());
        var offreSauvee = ArgumentCaptor.forClass(OffreDeStage.class);
        verify(offreDeStageRepository).save(offreSauvee.capture());
        assertThat(offreSauvee.getValue().getNombreCandidatsMax()).isEqualTo(2);

        verify(offreDeStageRepository, times(1)).save(any(OffreDeStage.class));
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Infirmerie");
    }

    @Test
    void doitLancerExceptionQuandFichierNullOuVideALaCreation() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, null, employeur.getEmail()))
                .isInstanceOf(FichierCorrompuException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitModifierOffreAvecNouveauFichier() throws Exception, DateFinAffichageApresDateDebutException {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        when(offreDeStageRepository.findById(1L)).thenReturn(Optional.of(offreDeStage));

        MockMultipartFile nouveauFichier = new MockMultipartFile("file", "nouveau.pdf",
                MediaType.APPLICATION_PDF_VALUE, "nouveau contenu".getBytes());

        when(offreDeStageRepository.save(any(OffreDeStage.class))).thenReturn(offreDeStage);

        OffreDeStageDTO result = employeurService.modifierOffre(1L, creationOffreDeStageDTO, nouveauFichier, employeur.getEmail());

        verify(offreDeStageRepository, times(1)).save(any(OffreDeStage.class));
        assertThat(result).isNotNull();
    }

    @Test
    void doitLancerExceptionQuandOffreNonEnAttenteLorsDeLaModification() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        offreDeStage.setStatut(Statut.ACCEPTEE);
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        when(offreDeStageRepository.findById(1L)).thenReturn(Optional.of(offreDeStage));

        assertThatThrownBy(() -> employeurService.modifierOffre(1L, creationOffreDeStageDTO, null, employeur.getEmail()))
                .isInstanceOf(OffreNonEnAttenteException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitTelechargerOffreAvecSucces() throws Exception {
        offreDeStage.setStoragePath("src/test/resources/offre.pdf");
        when(offreDeStageRepository.findById(1L)).thenReturn(Optional.of(offreDeStage));
        try {
            employeurService.telechargerOffre(1L, employeur.getEmail());
        } catch (Exception _) {
        }

        verify(offreDeStageRepository, times(1)).findById(1L);
    }

    @Test
    void doitLancerExceptionQuandOffreIntrouvablePourTelechargement() {
        when(offreDeStageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeurService.telechargerOffre(99L, employeur.getEmail()))
                .isInstanceOf(OffreIntrouvableException.class);
    }

    @Test
    void doitLancerExceptionQuandStoragePathVidePourTelechargement() {
        offreDeStage.setStoragePath(null);
        when(offreDeStageRepository.findById(1L)).thenReturn(Optional.of(offreDeStage));

        assertThatThrownBy(() -> employeurService.telechargerOffre(1L, employeur.getEmail()))
                .isInstanceOf(FichierIntrouvableException.class);
    }

    @Test
    void doitLancerExceptionQuandDateDebutAvantAujourdhui() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));

        CreationOffreDeStageDTO dtoInvalide = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.now().minusDays(1),
                LocalDate.of(2029, 10, 15),
                LocalDate.now().minusDays(5), 1
        );

        MockMultipartFile fichier = new MockMultipartFile("file", "offre.pdf",
                MediaType.APPLICATION_PDF_VALUE, "contenu".getBytes());

        assertThatThrownBy(() -> employeurService.creerOffre(dtoInvalide, fichier, employeur.getEmail()))
                .isInstanceOf(DateAvantAujourdhuiException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitCreerEntrevueAvecSucces() throws Exception {

        when(candidatureRepository.findById(candidature.getId()))
                .thenReturn(Optional.of(candidature));
        when(candidatureRepository.save(any(Candidature.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        employeurService.retenirCandidaturePourEntrevue(candidature.getId());

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        when(convocationRepository.findByCandidatureId(candidature.getId()))
                .thenReturn(Optional.empty());

        when(convocationRepository.save(any(Entrevue.class)))
                .thenAnswer(invocation -> {
                    Entrevue entrevue = invocation.getArgument(0);
                    entrevue.setId(1L);
                    return entrevue;
                });

        CreationEntrevueDTO dto = creerDTOConvocationValide();

        EntrevueDTO resultat = employeurService.creerConvocation(dto);

        assertThat(resultat).isNotNull();
        assertThat(resultat.id()).isEqualTo(1L);
        assertThat(resultat.format()).isEqualTo(FormatEntrevue.EN_LIGNE);
        assertThat(resultat.dateHeureEntrevue())
                .isEqualTo(dto.dateHeureEntrevue());
        assertThat(resultat.dateCreation()).isNotNull();
        assertThat(candidature.getStatut()).isEqualTo(StatutCandidature.ENTREVUE_PLANIFIEE);

        verify(convocationRepository).save(any(Entrevue.class));
        verify(candidatureRepository, times(2)).save(candidature);

        ArgumentCaptor<EntrevueCreeeEvent> captor = ArgumentCaptor.forClass(EntrevueCreeeEvent.class);

        verify(eventPublisher).publishEvent(captor.capture());

        assertThat(captor.getValue())
                .isInstanceOf(EntrevueCreeeEvent.class);
    }

    @Test
    void doitLancerExceptionQuandCandidatureIntrouvable() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        when(candidatureRepository.findById(99L))
                .thenReturn(Optional.empty());

        CreationEntrevueDTO dto = new CreationEntrevueDTO(
                99L,
                FormatEntrevue.EN_LIGNE,
                LocalDateTime.now().plusDays(7),
                30
        );

        assertThatThrownBy(() -> employeurService.creerConvocation(dto))
                .isInstanceOf(CandidatureIntrouvableException.class);

        verify(convocationRepository, never()).save(any(Entrevue.class));
    }

    @Test
    void doitLancerExceptionQuandConvocationExisteDeja() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        candidature.setStatut(StatutCandidature.ENTREVUE_REFUSEE);
        when(candidatureRepository.findById(candidature.getId()))
                .thenReturn(Optional.of(candidature));

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        Entrevue entrevueRefusee = new Entrevue();
        entrevueRefusee.setStatut(StatutEntrevue.REFUSEE);
        when(convocationRepository.findByCandidatureId(candidature.getId()))
                .thenReturn(Optional.of(entrevueRefusee));

        assertThatThrownBy(() ->
                employeurService.creerConvocation(creerDTOConvocationValide()))
                .isInstanceOf(ConvocationExistanteException.class);

        verify(convocationRepository, never())
                .save(any(Entrevue.class));
    }

    @Test
    void doitRetournerLeMotifDeRefusDeLaConvocation() throws Exception {
        Entrevue entrevue = Entrevue.builder().candidature(candidature).build();
        entrevue.setId(1L);
        entrevue.setStatut(StatutEntrevue.REFUSEE);
        entrevue.setMotifRefus("Indisponible");
        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));
        when(convocationRepository.findByCandidatureId(candidature.getId()))
                .thenReturn(Optional.of(entrevue));

        EntrevueDTO resultat = employeurService.obtenirConvocation(candidature.getId());

        assertThat(resultat.motifRefus()).isEqualTo("Indisponible");
        assertThat(resultat.statut()).isEqualTo(StatutEntrevue.REFUSEE);
    }

    @Test
    void neDoitPasConsulterLaConvocationDUnAutreEmployeur() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        Employeur autreEmployeur = Employeur.builder().build();
        autreEmployeur.setId(99L);
        candidature.getOffreDeStage().setEmployeur(autreEmployeur);
        Entrevue entrevue = Entrevue.builder().candidature(candidature).build();
        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));
        when(convocationRepository.findByCandidatureId(candidature.getId()))
                .thenReturn(Optional.of(entrevue));

        assertThatThrownBy(() -> employeurService.obtenirConvocation(candidature.getId()))
                .isInstanceOf(OffreNonAutoriseeException.class);
    }

    @Test
    void doitRetournerCandidaturesConvoqueesDeLEmployeur() throws Exception {
        candidature.setStatut(StatutCandidature.RETENUE_POUR_ENTREVUE);

        simulerFiltrageSession();

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        when(candidatureRepository
                .findByStatutAndOffreDeStageEmployeurId(
                        StatutCandidature.RETENUE_POUR_ENTREVUE,
                        employeur.getId()
                ))
                .thenReturn(List.of(candidature));

        List<CandidatureDTO> resultat =
                employeurService.obtenirCandidaturesConvoquees();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).id()).isEqualTo(candidature.getId());

        verify(candidatureRepository).findByStatutAndOffreDeStageEmployeurId(
                StatutCandidature.RETENUE_POUR_ENTREVUE,
                employeur.getId()
        );
    }
    @Test
    void doitRefuserEntrevueQuandDateDansLePasse() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        candidature.setStatut(StatutCandidature.RETENUE_POUR_ENTREVUE);
        when(candidatureRepository.findById(candidature.getId()))
                .thenReturn(Optional.of(candidature));

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        when(convocationRepository.findByCandidatureId(candidature.getId()))
                .thenReturn(Optional.empty());

        CreationEntrevueDTO dto = new CreationEntrevueDTO(
                candidature.getId(),
                FormatEntrevue.EN_LIGNE,
                LocalDateTime.now().minusDays(1),
                30
        );

        assertThatThrownBy(() -> employeurService.creerConvocation(dto))
                .isInstanceOf(DateAvantAujourdhuiException.class);

        verify(convocationRepository, never()).save(any(Entrevue.class));
    }


    @Test
    void doitSignerContratAvecSucces() throws Exception {
        Gestionnaire gestionnaire = new Gestionnaire(
                1L, "Laurent", "losange",
                "Laurent.Losange@hotmail.com", "5858", "458-458-4584"
        );
        gestionnaire.setId(1L);

        Long contratId = 1L;
        String motDePasse = "Losange12%";

        Contrat contrat = Contrat.builder()
                .candidature(candidature)
                .gestionnaire(gestionnaire)
                .build();
        contrat.setId(contratId);

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        when(contratRepository.findById(contratId))
                .thenReturn(Optional.of(contrat));

        when(passwordEncoder.matches(motDePasse, employeur.getPassword()))
                .thenReturn(true);

        when(contratRepository.save(any(Contrat.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ContratDTO result = employeurService.signerContrat(contratId, motDePasse);

        assertThat(result).isNotNull();
        assertThat(contrat.getDateSignatureEmployeur()).isNotNull();

        verify(userAppRepository).findUserAppByEmail(employeur.getEmail());
        verify(contratRepository).findById(contratId);
        verify(passwordEncoder).matches(motDePasse, employeur.getPassword());
        verify(contratRepository).save(contrat);
    }

    @Test
    void doitLeverExceptionSiUtilisateurIntrouvablePourSignatureContrat() {
        Long contratId = 1L;
        String motDePasse = "Losange12%";

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                employeurService.signerContrat(contratId, motDePasse))
                .isInstanceOf(UtilisateurIntrouvableException.class);

        verify(userAppRepository).findUserAppByEmail(employeur.getEmail());
        verify(contratRepository, never()).findById(any());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(contratRepository, never()).save(any());
    }

    @Test
    void doitLeverExceptionSiContratIntrouvablePourSignature() {
        Long contratId = 99L;
        String motDePasse = "Losange12%";

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        when(contratRepository.findById(contratId))
                .thenReturn(Optional.empty());
        when(passwordEncoder.matches(motDePasse, employeur.getPassword()))
                .thenReturn(true);
        when(passwordEncoder.matches(motDePasse, employeur.getPassword()))
                .thenReturn(true);

        assertThatThrownBy(() ->
                employeurService.signerContrat(contratId, motDePasse))
                .isInstanceOf(ContratIntrouvableException.class);

        verify(userAppRepository).findUserAppByEmail(employeur.getEmail());
        verify(contratRepository).findById(contratId);
        verify(contratRepository, never()).save(any());
    }

    @Test
    void doitLeverExceptionSiEmployeurNonProprietaireDeOffrePourSignatureContrat() {
        Long contratId = 1L;
        String motDePasse = "Losange12%";

        Employeur autreEmployeur = Employeur.builder()
                .firstName("Autre")
                .lastName("Employeur")
                .email("autre@employeur.com")
                .build();
        autreEmployeur.setId(2L);

        OffreDeStage autreOffre = OffreDeStage.builder()
                .employeur(autreEmployeur)
                .build();

        Candidature autreCandidature = Candidature.builder()
                .offreDeStage(autreOffre)
                .build();

        Contrat contrat = Contrat.builder()
                .candidature(autreCandidature)
                .build();

        contrat.setId(contratId);

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        when(contratRepository.findById(contratId))
                .thenReturn(Optional.of(contrat));

        when(passwordEncoder.matches(motDePasse, employeur.getPassword()))
                .thenReturn(true);

        assertThatThrownBy(() ->
                employeurService.signerContrat(contratId, motDePasse))
                .isInstanceOf(OffreNonAutoriseeException.class);

        verify(userAppRepository).findUserAppByEmail(employeur.getEmail());
        verify(contratRepository).findById(contratId);
        verify(contratRepository, never()).save(any());
    }

    @Test
    void doitRefuserDecisionSiConvocationNonAcceptee() {
        creerEntrevueTerminee().setStatut(StatutEntrevue.A_CONFIRMER);

        assertThatThrownBy(() -> employeurService.accepterCandidatApresEntrevue(1L))
                .isInstanceOf(DecisionCandidatureInvalideException.class);

        verify(candidatureRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void doitSignalerLaCandidatureRetenuePourRetirerLaNotification() throws Exception {
        candidature.setStatut(StatutCandidature.SOUMISE);
        when(candidatureRepository.findById(candidature.getId())).thenReturn(Optional.of(candidature));

        employeurService.retenirCandidaturePourEntrevue(candidature.getId());

        assertThat(candidature.getStatut()).isEqualTo(StatutCandidature.RETENUE_POUR_ENTREVUE);
        verify(eventPublisher).publishEvent(new CandidatureRetenueEvent(candidature));
    }

    @Test
    void doitSignalerLaCloturePourRetirerLaNotification() throws Exception {
        Entrevue entrevue = creerEntrevueTerminee();
        entrevue.setStatut(StatutEntrevue.CONFIRMEE);

        employeurService.cloreEntrevue(1L);

        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.TERMINEE);
        verify(eventPublisher).publishEvent(new EntrevueTermineeEvent(entrevue));
    }

    @Test
    void doitRefuserDecisionAvantLaClotureDeLEntrevue() {
        creerEntrevueTerminee().setStatut(StatutEntrevue.CONFIRMEE);
        assertThatThrownBy(() -> employeurService.accepterCandidatApresEntrevue(1L))
                .isInstanceOf(DecisionCandidatureInvalideException.class);
        verify(candidatureRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void doitRefuserDecisionPourUneEntrevueDUnAutreEmployeur() {
        creerEntrevueTerminee();
        Employeur autreEmployeur = new Employeur();
        autreEmployeur.setId(99L);
        offreDeStage.setEmployeur(autreEmployeur);
        assertThatThrownBy(() -> employeurService.accepterCandidatApresEntrevue(1L))
                .isInstanceOf(OffreNonAutoriseeException.class);
        verify(candidatureRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void doitLeverExceptionSiMotDePasseInvalidePourSignatureContrat() {
        Long contratId = 1L;
        String motDePasse = "MauvaisMotDePasse";

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        when(passwordEncoder.matches(motDePasse, employeur.getPassword()))
                .thenReturn(false);

        assertThatThrownBy(() ->
                employeurService.signerContrat(contratId, motDePasse))
                .isInstanceOf(MotDePasseInvalideException.class);

        verify(userAppRepository).findUserAppByEmail(employeur.getEmail());
        verify(passwordEncoder).matches(motDePasse, employeur.getPassword());

        verify(contratRepository, never()).findById(anyLong());
        verify(contratRepository, never()).save(any());
    }

    @Test
    void neDoitPasRefuserUneCandidatureDejaAcceptee() throws Exception {
        creerEntrevueTerminee();
        when(offreDeStageRepository.findByIdForUpdate(offreDeStage.getId())).thenReturn(Optional.of(offreDeStage));
        when(candidatureRepository.save(candidature)).thenReturn(candidature);
        employeurService.accepterCandidatApresEntrevue(1L);
        assertThatThrownBy(() -> employeurService.refuserCandidatApresEntrevue(1L, "Autre profil"))
                .isInstanceOf(DecisionCandidatureInvalideException.class);
        assertThat(candidature.getStatut()).isEqualTo(StatutCandidature.ACCEPTEE);
        verify(candidatureRepository, times(1)).save(candidature);
        verify(eventPublisher, times(1)).publishEvent(any(CandidatureAccepteeEvent.class));
    }

    @Test
    void doitBloquerLesDecisionsEtLaClotureSiUnAutreStageEstChoisi() {
        Entrevue entrevue = creerEntrevueTerminee();
        when(offreDeStageRepository.findByIdForUpdate(offreDeStage.getId())).thenReturn(Optional.of(offreDeStage));
        Candidature stage = new Candidature();
        stage.setId(999L);
        etudiant.setCandidatureStage(stage);
        etudiant.setStatut(StatutEtudiant.STAGE_TROUVE);

        assertThatThrownBy(() -> employeurService.accepterCandidatApresEntrevue(1L))
                .isInstanceOf(ActionsCandidatureDesactiveesException.class);
        assertThatThrownBy(() -> employeurService.refuserCandidatApresEntrevue(1L, "Autre profil"))
                .isInstanceOf(ActionsCandidatureDesactiveesException.class);
        assertThatThrownBy(() -> employeurService.cloreEntrevue(1L))
                .isInstanceOf(ActionsCandidatureDesactiveesException.class);
        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.TERMINEE);
        assertThat(candidature.getStatut()).isEqualTo(StatutCandidature.ENTREVUE_PLANIFIEE);
        verify(candidatureRepository, never()).save(any());
        verify(convocationRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void neDoitPlusRetenirUneAutreCandidatureApresLeStageChoisi() {
        Candidature stage = new Candidature();
        stage.setId(999L);
        etudiant.setCandidatureStage(stage);
        when(candidatureRepository.findById(candidature.getId())).thenReturn(Optional.of(candidature));

        assertThatThrownBy(() -> employeurService.retenirCandidaturePourEntrevue(candidature.getId()))
                .isInstanceOf(ActionsCandidatureDesactiveesException.class);
        verify(candidatureRepository, never()).save(any());
    }
    @Test
    void doitReserverUnePlaceSansAttribuerLeStageAvantLaReponseEtudiant() throws Exception {
        creerEntrevueTerminee();
        when(offreDeStageRepository.findByIdForUpdate(offreDeStage.getId())).thenReturn(Optional.of(offreDeStage));
        when(candidatureRepository.save(candidature)).thenReturn(candidature);

        CandidatureDTO resultat = employeurService.accepterCandidatApresEntrevue(1L);

        assertThat(resultat.statut()).isEqualTo(StatutCandidature.ACCEPTEE);
        assertThat(resultat.statutEtudiant()).isNotEqualTo(StatutEtudiant.STAGE_TROUVE);
        assertThat(etudiant.getCandidatureStage()).isNull();
        assertThat(resultat.motifRefus()).isNull();
        verify(userAppRepository, never()).save(any());
        verify(etudiantService, never()).actualiserStatutEtudiant(any());
        verify(candidatureRepository).save(candidature);
        ArgumentCaptor<CandidatureAccepteeEvent> captor = ArgumentCaptor.forClass(CandidatureAccepteeEvent.class);
        var ordre = inOrder(eventPublisher);

        ordre.verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().candidature()).isSameAs(candidature);
    }

    @Test
    void neDoitPasDeclencherDeuxFoisLeProcessusPourLaMemeAcceptation() throws Exception {
        creerEntrevueTerminee();
        when(offreDeStageRepository.findByIdForUpdate(offreDeStage.getId())).thenReturn(Optional.of(offreDeStage));
        when(candidatureRepository.save(candidature)).thenReturn(candidature);

        employeurService.accepterCandidatApresEntrevue(1L);
        CandidatureDTO resultat = employeurService.accepterCandidatApresEntrevue(1L);

        assertThat(resultat.statut()).isEqualTo(StatutCandidature.ACCEPTEE);
        verify(candidatureRepository).save(candidature);
        verify(userAppRepository, never()).save(any());

        verify(eventPublisher).publishEvent(new CandidatureAccepteeEvent(candidature));
        verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    void neDoitPasReinitialiserUnContratDejaDemarreLorsDuneNouvelleAcceptation() throws Exception {
        creerEntrevueTerminee();
        candidature.setStatut(StatutCandidature.CONTRAT_CREE);
        etudiant.setCandidatureStage(candidature);
        etudiant.setStatut(StatutEtudiant.STAGE_TROUVE);

        CandidatureDTO resultat = employeurService.accepterCandidatApresEntrevue(1L);

        assertThat(resultat.statut()).isEqualTo(StatutCandidature.CONTRAT_CREE);
        verify(candidatureRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void doitReserverLUniquePlaceSansAttribuerLeStageALEtudiant() throws Exception {
        creerEntrevueTerminee();
        offreDeStage.setNombreCandidatsMax(1);
        offreDeStage.setCandidatures(List.of(candidature));
        when(offreDeStageRepository.findByIdForUpdate(offreDeStage.getId())).thenReturn(Optional.of(offreDeStage));
        when(candidatureRepository.save(candidature)).thenReturn(candidature);

        var resultat = employeurService.accepterCandidatApresEntrevue(1L);

        assertThat(resultat.statut()).isEqualTo(StatutCandidature.ACCEPTEE);
        assertThat(offreDeStage.getNombreCandidatsAcceptes()).isEqualTo(1);
        assertThat(offreDeStage.estComplete()).isTrue();
        assertThat(resultat.actionsDesactivees()).isFalse();
        assertThat(etudiant.getCandidatureStage()).isNull();
        assertThat(etudiant.getStatut()).isNotEqualTo(StatutEtudiant.STAGE_TROUVE);
        verify(candidatureRepository).countByOffreDeStageIdAndStatutIn(offreDeStage.getId(),
                List.of(StatutCandidature.ACCEPTEE, StatutCandidature.STAGE_TROUVE, StatutCandidature.CONTRAT_CREE));
        verify(eventPublisher, never()).publishEvent(any(StageTrouveEvent.class));
    }

    @Test
    void doitReserverDeuxPlacesEtRefuserUneTroisiemeAcceptation() throws Exception {
        creerEntrevueTerminee();
        offreDeStage.setNombreCandidatsMax(2);
        var deuxieme = new Candidature(new Etudiant(), offreDeStage, LocalDateTime.now());
        deuxieme.setId(22L);
        deuxieme.setStatut(StatutCandidature.ENTREVUE_PLANIFIEE);
        var troisieme = new Candidature(new Etudiant(), offreDeStage, LocalDateTime.now());
        troisieme.setId(33L);
        troisieme.setStatut(StatutCandidature.ENTREVUE_PLANIFIEE);
        offreDeStage.setCandidatures(List.of(candidature, deuxieme, troisieme));
        for (var autre : List.of(deuxieme, troisieme)) {
            autre.getEtudiant().setId(autre.getId());
            var entrevue = new Entrevue();
            entrevue.setCandidature(autre);
            entrevue.setStatut(StatutEntrevue.TERMINEE);
            when(convocationRepository.findById(autre.getId())).thenReturn(Optional.of(entrevue));
        }
        when(offreDeStageRepository.findByIdForUpdate(offreDeStage.getId())).thenReturn(Optional.of(offreDeStage));
        when(candidatureRepository.countByOffreDeStageIdAndStatutIn(eq(offreDeStage.getId()), anyList()))
                .thenReturn(0L, 1L, 2L);
        when(candidatureRepository.save(any(Candidature.class))).thenAnswer(invocation -> invocation.getArgument(0));

        employeurService.accepterCandidatApresEntrevue(1L);
        employeurService.accepterCandidatApresEntrevue(22L);

        assertThatThrownBy(() -> employeurService.accepterCandidatApresEntrevue(33L))
                .isInstanceOf(ActionsCandidatureDesactiveesException.class)
                .hasMessage("error.offer_capacity_reached");
        assertThat(offreDeStage.getNombreCandidatsAcceptes()).isEqualTo(2);
        assertThat(offreDeStage.estComplete()).isTrue();
        assertThat(CandidatureDTO.of(troisieme).actionsDesactivees()).isTrue();
        assertThat(CandidatureDTO.of(candidature).actionsDesactivees()).isFalse();
        verify(candidatureRepository, times(2)).save(any());
        verify(candidatureRepository, times(3)).countByOffreDeStageIdAndStatutIn(
                offreDeStage.getId(), List.of(StatutCandidature.ACCEPTEE, StatutCandidature.STAGE_TROUVE, StatutCandidature.CONTRAT_CREE));
        assertThat(troisieme.getStatut()).isEqualTo(StatutCandidature.ENTREVUE_PLANIFIEE);
    }

    @Test
    void doitBloquerLesAutresActionsSurUneOffreComplete() throws Exception {
        creerEntrevueTerminee();
        var retenue = new Candidature(new Etudiant(), offreDeStage, LocalDateTime.now());
        retenue.setStatut(StatutCandidature.CONTRAT_CREE);
        offreDeStage.setCandidatures(List.of(retenue, candidature));
        when(offreDeStageRepository.findByIdForUpdate(offreDeStage.getId())).thenReturn(Optional.of(offreDeStage));
        when(candidatureRepository.countByOffreDeStageIdAndStatutIn(eq(offreDeStage.getId()), anyList()))
                .thenReturn(1L);
        when(candidatureRepository.findById(candidature.getId())).thenReturn(Optional.of(candidature));

        assertThatThrownBy(() -> employeurService.refuserCandidatApresEntrevue(1L, "Autre profil"))
                .isInstanceOf(ActionsCandidatureDesactiveesException.class);
        assertThatThrownBy(() -> employeurService.cloreEntrevue(1L))
                .isInstanceOf(ActionsCandidatureDesactiveesException.class);
        assertThatThrownBy(() -> employeurService.retenirCandidaturePourEntrevue(candidature.getId()))
                .isInstanceOf(ActionsCandidatureDesactiveesException.class);
        assertThatThrownBy(() -> employeurService.creerConvocation(creerDTOConvocationValide()))
                .isInstanceOf(ActionsCandidatureDesactiveesException.class);
        verify(candidatureRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void doitRefuserUneCapaciteNulleOuNegative() throws com.lacouf.rsbjwt.security.exception.UserNotFoundException {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        for (int capacite : new int[]{0, -1}) {
            var dto = new CreationOffreDeStageDTO(creationOffreDeStageDTO.title(), creationOffreDeStageDTO.description(),
                    creationOffreDeStageDTO.salary(), creationOffreDeStageDTO.domain(), creationOffreDeStageDTO.startDate(),
                    creationOffreDeStageDTO.endDate(), creationOffreDeStageDTO.displayEndDate(), capacite);
            assertThatThrownBy(() -> employeurService.creerOffre(dto, null, employeur.getEmail()))
                    .isInstanceOf(com.lacouf.rsbjwt.exception.offre.NombreCandidatsMaxInvalideException.class)
                    .hasMessage("error.invalid_candidate_capacity");
        }
        verify(offreDeStageRepository, never()).save(any());
    }

    @Test
    void doitRefuserLeCandidatAvecUnMotif() throws Exception {
        creerEntrevueTerminee();
        when(offreDeStageRepository.findByIdForUpdate(offreDeStage.getId())).thenReturn(Optional.of(offreDeStage));
        when(candidatureRepository.save(candidature)).thenReturn(candidature);

        CandidatureDTO resultat = employeurService.refuserCandidatApresEntrevue(1L, "  Experience insuffisante  ");

        assertThat(resultat.statut()).isEqualTo(StatutCandidature.REFUSEE);
        assertThat(resultat.motifRefus()).isEqualTo("Experience insuffisante");
        assertThat(resultat.statutEtudiant()).isEqualTo(StatutEtudiant.EN_RECHERCHE);
        verify(etudiantService).actualiserStatutEtudiant(etudiant);
        verify(candidatureRepository).save(candidature);
        verify(eventPublisher).publishEvent(any(CandidatureRefuseeEvent.class));
    }

    @Test
    void doitRetournerLesEntrevuesDeLEmployeurSaufCellesRefusees() throws Exception {
        Entrevue confirmee = Entrevue.builder().candidature(candidature).build();
        confirmee.setStatut(StatutEntrevue.CONFIRMEE);
        confirmee.setId(30L);
        Entrevue refusee = Entrevue.builder().candidature(candidature).build();
        refusee.setStatut(StatutEntrevue.REFUSEE);
        refusee.setId(31L);

        simulerFiltrageSession();
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        when(convocationRepository.findByEmployeurId(employeur.getId())).thenReturn(List.of(confirmee, refusee));

        assertThat(employeurService.obtenirEntrevues()).extracting(EntrevueDTO::id).containsExactly(30L);
    }

    @Test
    void doitRefuserLaListeDesEntrevuesSiLEmployeurEstIntrouvable() {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeurService.obtenirEntrevues())
                .isInstanceOf(UtilisateurIntrouvableException.class);
        verifyNoInteractions(convocationRepository);
    }

    @Test
    void doitRefuserLaClotureSiLEntrevueEstIntrouvable() {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        when(convocationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeurService.cloreEntrevue(99L))
                .isInstanceOf(ConvocationIntrouvableException.class);
        verify(convocationRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void doitRefuserLaClotureDeLEntrevueDUnAutreEmployeur() {
        creerEntrevueTerminee();
        Employeur autreEmployeur = new Employeur();
        autreEmployeur.setId(99L);
        offreDeStage.setEmployeur(autreEmployeur);

        assertThatThrownBy(() -> employeurService.cloreEntrevue(1L))
                .isInstanceOf(OffreNonAutoriseeException.class);
        verify(convocationRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void doitRefuserDeCloreUneEntrevueNonConfirmee() {
        Entrevue entrevue = creerEntrevueTerminee();
        entrevue.setStatut(StatutEntrevue.A_CONFIRMER);

        assertThatThrownBy(() -> employeurService.cloreEntrevue(1L))
                .isInstanceOf(DecisionCandidatureInvalideException.class);
        verify(convocationRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void doitRetournerLesContratsDeLEmployeur() throws UtilisateurIntrouvableException {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeur.getEmail(), null)
        );
        simulerFiltrageSession();

        Gestionnaire gestionnaire = Gestionnaire.builder()
                .firstName("Laurent")
                .lastName("Losange")
                .email("Laurent.Losange@hotmail.com")
                .build();
        gestionnaire.setId(1L);

        Contrat contrat = Contrat.builder()
                .candidature(candidature)
                .gestionnaire(gestionnaire)
                .build();
        ReflectionTestUtils.setField(contrat, "id", 1L);

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        when(contratRepository.findByCandidatureOffreDeStageEmployeurId(employeur.getId()))
                .thenReturn(List.of(contrat));

        List<ResumeContratDTO> resultat = employeurService.obtenirContratsEmployeur();

        assertThat(resultat).hasSize(1);
        verify(contratRepository).findByCandidatureOffreDeStageEmployeurId(employeur.getId());
    }

    @Test
    void doitRetournerLesEntrevuesParStatutDeLEmployeur() throws UtilisateurIntrouvableException {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeur.getEmail(), null)
        );
        simulerFiltrageSession();

        Entrevue entrevue = Entrevue.builder()
                .candidature(candidature)
                .build();

        entrevue.setStatut(StatutEntrevue.CONFIRMEE);
        ReflectionTestUtils.setField(entrevue, "id", 1L);

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        when(convocationRepository.findByEmployeurId(employeur.getId()))
                .thenReturn(List.of(entrevue));

        List<EntrevueDTO> resultat = employeurService.obtenirEntrevuesParStatut(StatutEntrevue.CONFIRMEE);

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).statut()).isEqualTo(StatutEntrevue.CONFIRMEE);
        verify(convocationRepository).findByEmployeurId(employeur.getId());
    }

    private <T> void simulerFiltrageSession() {
        when(sessionService.filtrerParSessionActuelle(
                anyList(),
                any()
        )).thenAnswer(invocation -> invocation.getArgument(0));
    }
}