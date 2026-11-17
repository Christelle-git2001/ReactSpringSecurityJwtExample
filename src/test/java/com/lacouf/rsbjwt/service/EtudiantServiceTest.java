package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.candidature.ActionsCandidatureDesactiveesException;
import com.lacouf.rsbjwt.exception.candidature.CandidatureDejaExistanteException;
import com.lacouf.rsbjwt.exception.candidature.CandidatureIntrouvableException;
import com.lacouf.rsbjwt.exception.candidature.DecisionStageInvalideException;
import com.lacouf.rsbjwt.exception.contrat.ContratIntrouvableException;
import com.lacouf.rsbjwt.exception.cv.CurriculumVitaeIntrouvable;
import com.lacouf.rsbjwt.exception.cv.CurriculumVitaeNonApprouveException;
import com.lacouf.rsbjwt.exception.entrevue.ConvocationIntrouvableException;
import com.lacouf.rsbjwt.exception.entrevue.ConvocationInvalideException;
import com.lacouf.rsbjwt.exception.fichier.FichierCorrompuException;
import com.lacouf.rsbjwt.exception.fichier.FichierIntrouvableException;
import com.lacouf.rsbjwt.exception.fichier.FichierTypeInvalideException;
import com.lacouf.rsbjwt.exception.offre.OffreDeStageIndisponibleException;
import com.lacouf.rsbjwt.exception.offre.OffreIntrouvableException;
import com.lacouf.rsbjwt.exception.offre.OffreNonAutoriseeException;
import com.lacouf.rsbjwt.exception.utilisateur.EmailExistantException;
import com.lacouf.rsbjwt.exception.utilisateur.EtudiantIntrouvableException;
import com.lacouf.rsbjwt.exception.utilisateur.MatriculeExistantException;
import com.lacouf.rsbjwt.exception.utilisateur.MatriculeNonConforme;
import com.lacouf.rsbjwt.exception.utilisateur.MotDePasseInvalideException;
import com.lacouf.rsbjwt.exception.utilisateur.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.exception.utilisateur.NumeroTelephoneExistantException;
import com.lacouf.rsbjwt.service.dto.candidature.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.contrat.ContratDTO;
import com.lacouf.rsbjwt.service.dto.cv.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.cv.MonCvTeleverseDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.EntrevueDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.ReponseConvocationDTO;
import com.lacouf.rsbjwt.service.dto.offre.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.session.SessionDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.InscriptionEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.MonProfesseurAssigneDTO;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.*;
import com.lacouf.rsbjwt.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.lacouf.rsbjwt.model.event.NouvelleCandidatureEvent;
import com.lacouf.rsbjwt.model.event.ConvocationRepondueEvent;
import com.lacouf.rsbjwt.model.event.StageTrouveEvent;
import com.lacouf.rsbjwt.model.event.ContratSigneEvent;
import com.lacouf.rsbjwt.model.event.EntrevueTermineeEvent;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EtudiantServiceTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private UserAppRepository userAppRepository;

    @InjectMocks
    private EtudiantService etudiantService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CvEtudiantRepository cvEtudiantRepository;

    @Mock
    private OffreDeStageRepository offreDeStageRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private CandidatureRepository candidatureRepository;

    @Mock
    private EntrevueRepository entrevueRepository;

    @Mock ContratRepository contratRepository;
    @Mock GestionnaireRepository gestionnaireRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private InscriptionRepository inscriptionRepository;

    @TempDir
    Path dossierCvTest;

    InscriptionEtudiantDTO inscriptionEtudiantDTO;
    Etudiant etudiant;
    Employeur employeur;
    Entrevue entrevue;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(etudiantService, "STORAGE_CV", dossierCvTest);

        inscriptionEtudiantDTO = new InscriptionEtudiantDTO(
                "Christelle",
                "Altineus",
                "438-297-8191",
                "christelle@gmail.com",
                "2226252",
                "INFORMATIQUE",
                "111111",
                "111111"
        );

        etudiant = Etudiant.builder()
                .firstName("Steve")
                .lastName("Jean")
                .phoneNumber("514-327-9021")
                .matricule("214578")
                .department(Departement.INFORMATIQUE)
                .email("steveJean@gmail.com")
                .password("Passewod123")
                .build();
        etudiant.setId(1L);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(etudiant.getEmail(), null));

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

        OffreDeStage offre = new OffreDeStage();
        offre.setId(10L);
        offre.setTitle("Stage informatique");
        offre.setEmployeur(employeur);

        Candidature candidature = new Candidature(
                etudiant,
                offre,
                LocalDateTime.now()
        );

        candidature.setId(20L);

        entrevue = Entrevue.builder()
                .candidature(candidature)
                .format(FormatEntrevue.EN_LIGNE)
                .dateHeureEntrevue(LocalDateTime.now().plusDays(1))
                .dateCreation(LocalDateTime.now())
                .build();
        entrevue.setId(30L);

        lenient().when(sessionService.getSessionActuelle())
                .thenReturn(new SessionDTO(Saison.HIVER,
                        LocalDate.now().getYear()
                ));

        lenient().when(sessionService.filtrerParSessionActuelle(
                anyList(),
                any()
        )).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void inscrireEtudiantSessionActuelle() {
        SessionDTO sessionActuelle = sessionService.getSessionActuelle();

        Inscription inscription = new Inscription(
                etudiant,
                sessionActuelle.saison(),
                sessionActuelle.annee()
        );

        etudiant.getInscriptions().add(inscription);
    }


    @AfterEach
    void nettoyerAuthentification() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doitCreerCompteEtudiant() throws Exception{

        when(etudiantRepository.save(any(Etudiant.class)))
                .thenReturn(etudiant);

        EtudiantDTO result =
                etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO);

        verify(etudiantRepository, times(1))
                .save(any(Etudiant.class));

        assertThat(result)
                .isNotNull()
                .returns(etudiant.getFirstName(), EtudiantDTO::firstName)
                .returns(etudiant.getEmail(), EtudiantDTO::email)
                .returns(etudiant.getMatricule(), EtudiantDTO::matricule)
                .returns(etudiant.getPhoneNumber(), EtudiantDTO::phoneNumber);



    }

    @Test
    void doitLancerExceptionEmailExistant() {

        when(userAppRepository.findUserAppByEmail(anyString()))
                .thenReturn(Optional.of(etudiant));

        assertThatThrownBy(() ->
                etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO))
                .isInstanceOf(EmailExistantException.class);

        verify(etudiantRepository, never())
                .save(any(Etudiant.class));
    }

    @Test
    void doitLancerExceptionMotDePasseDifferent() {

        inscriptionEtudiantDTO = new InscriptionEtudiantDTO(
                "Christelle",
                "Altineus",
                "438-297-8191",
                "christelle@gmail.com",
                "2226252",
                "INFORMATIQUE",
                "111111",
                "222222"
        );

        assertThatThrownBy(() ->
                etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO))
                .isInstanceOf(MotDePasseNonCorrespondantException.class);

        verify(etudiantRepository, never())
                .save(any(Etudiant.class));
    }

    @Test
    void doitLancerExceptionMatriculeExistant() {

        when(userAppRepository.findUserAppByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(etudiantRepository.findByMatricule(anyString()))
                .thenReturn(Optional.of(etudiant));

        assertThatThrownBy(() ->
                etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO))
                .isInstanceOf(MatriculeExistantException.class);

        verify(etudiantRepository, never())
                .save(any(Etudiant.class));
    }

    @Test
    void doitLancerExceptionNumeroTelephoneExistant() {
        when(userAppRepository.findByPhoneNumber("438-297-8191"))
                .thenReturn(Optional.of(etudiant));

        assertThatThrownBy(() ->
                etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO)
        ).isInstanceOf(NumeroTelephoneExistantException.class);

        verify(etudiantRepository, never()).save(any(Etudiant.class));
    }

    @Test
    void doitLancerExceptionMatriculeNonConforme() {
        inscriptionEtudiantDTO = new InscriptionEtudiantDTO(
                "Christelle",
                "Altineus",
                "438-297-8191",
                "christelle@gmail.com",
                "22262523",
                "INFORMATIQUE",
                "111111",
                "222222"
        );
        assertThatThrownBy(() ->
                etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO)
        ).isInstanceOf(MatriculeNonConforme.class);

        verify(etudiantRepository, never()).save(any(Etudiant.class));
    }

    @Test
    void doitTeleverserCv() throws Exception{
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cv.pdf",
                "application/pdf",
                "contenu pdf".getBytes()
        );

        when(etudiantRepository.findByCredentialsEmail("steveJean@gmail.com"))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.empty());

        when(cvEtudiantRepository.save(any(CvEtudiant.class)))
                .thenAnswer(invocation -> {
                    CvEtudiant cv = invocation.getArgument(0);
                    cv.setId(1L);
                    return cv;
                });

        CvEtudiantDTO result = etudiantService.televerserCv(file, "steveJean@gmail.com");

        assertThat(result).isNotNull();
        assertThat(result.fileName()).isEqualTo("cv.pdf");
        assertThat(result.contentType()).isEqualTo("application/pdf");

        verify(cvEtudiantRepository).save(any(CvEtudiant.class));
    }

    @Test
    void doitRemplacerAncienCv() throws Exception{
        Path ancienFichier = dossierCvTest.resolve( "ancien.pdf");
        Files.createDirectories(ancienFichier.getParent());
        Files.writeString(ancienFichier, "ancien contenu");

        CvEtudiant ancienCv = CvEtudiant.builder()
                .fileName("ancien.pdf")
                .contentType("application/pdf")
                .fileSize(10L)
                .storagePath(ancienFichier.toString())
                .uploadDate(LocalDateTime.now())
                .etudiant(etudiant)
                .build();
        ancienCv.setId(1L);
        ancienCv.setStatut(Statut.REFUSEE);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "nouveau.pdf",
                "application/pdf",
                "nouveau contenu".getBytes()
        );

        when(etudiantRepository.findByCredentialsEmail("steveJean@gmail.com"))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(ancienCv));

        when(cvEtudiantRepository.save(any(CvEtudiant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CvEtudiantDTO result = etudiantService.televerserCv(file, "steveJean@gmail.com");

        assertThat(result.fileName()).isEqualTo("nouveau.pdf");
        assertThat(Files.exists(ancienFichier)).isFalse();

        verify(cvEtudiantRepository).save(any(CvEtudiant.class));
    }

    @Test
    void doitLancerExceptionFichierTypeInvalide() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "nouveau.pdf",
                "text/plain",
                "contenu".getBytes()
        );

        when(etudiantRepository.findByCredentialsEmail("steveJean@gmail.com"))
                .thenReturn(Optional.of(etudiant));

        assertThatThrownBy(() ->
                etudiantService.televerserCv(file, "steveJean@gmail.com"))
                .isInstanceOf(FichierTypeInvalideException.class);

        verify(cvEtudiantRepository, never()).save(any(CvEtudiant.class));
    }

    @Test
    void doitLancerExceptionFichierCorrompu() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "nouveau.pdf",
                "application/pdf",
                new byte[0]
        );

        when(etudiantRepository.findByCredentialsEmail("steveJean@gmail.com"))
                .thenReturn(Optional.of(etudiant));

        assertThatThrownBy(() ->
                etudiantService.televerserCv(file, "steveJean@gmail.com"))
                .isInstanceOf(FichierCorrompuException.class);

        verify(cvEtudiantRepository, never()).save(any(CvEtudiant.class));
    }

    @Test
    void doitRetournerCv() throws Exception{
        CvEtudiant cv = CvEtudiant.builder()
                .fileName("cv.pdf")
                .contentType("application/pdf")
                .fileSize(10L)
                .storagePath(dossierCvTest.resolve("cv.pdf").toString())
                .uploadDate(LocalDateTime.now())
                .etudiant(etudiant)
                .build();
        cv.setId(1L);

        when(etudiantRepository.findByCredentialsEmail("steveJean@gmail.com"))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        CvEtudiantDTO result = etudiantService.getCv("steveJean@gmail.com");

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.fileName()).isEqualTo("cv.pdf");
    }

    @Test
    void doitLancerExceptionFichierIntrouvableQuandAucunCv() {
        when(etudiantRepository.findByCredentialsEmail("steveJean@gmail.com"))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                etudiantService.getCv("steveJean@gmail.com"))
                .isInstanceOf(FichierIntrouvableException.class);
    }

    @Test
    void doitSupprimerCv() throws Exception {
        Path fichier = dossierCvTest.resolve("cv.pdf");
        Files.createDirectories(fichier.getParent());
        Files.writeString(fichier, "contenu");

        CvEtudiant cv = CvEtudiant.builder()
                .fileName("cv.pdf")
                .contentType("application/pdf")
                .fileSize(10L)
                .storagePath(fichier.toString())
                .uploadDate(java.time.LocalDateTime.now())
                .etudiant(etudiant)
                .build();
        cv.setId(1L);

        when(etudiantRepository.findByCredentialsEmail("steveJean@gmail.com"))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        etudiantService.supprimerCv("steveJean@gmail.com");

        assertThat(Files.exists(fichier)).isFalse();
        verify(cvEtudiantRepository).delete(cv);
    }

    @Test
    void doitRetournerLesOffresAcceptees() throws Exception {
        inscrireEtudiantSessionActuelle();

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setTitle("Développeur Java");
        offre.setDescription("Développement d'applications");
        offre.setSalary(25.0);
        offre.setDomain(Departement.INFORMATIQUE);
        offre.setStartDate(LocalDate.of(2029, 1, 1));
        offre.setEndDate(LocalDate.of(2029, 4, 30));
        offre.setDisplayEndDate(LocalDate.of(2029, 1, 1).minusDays(15));
        offre.setStatut(Statut.ACCEPTEE);
        offre.setEmployeur(employeur);

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        CvEtudiant cv = new CvEtudiant();
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.ACCEPTEE);

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        when(offreDeStageRepository.findOffresGeneralesDisponibles(
                eq(Statut.ACCEPTEE),
                eq(offre.getDomain()),
                any(LocalDate.class)))
                .thenReturn(List.of(offre));

        List<OffreDeStageDTO> result =
                etudiantService.getOffresDisponibles(
                        offre.getDomain(),
                        etudiant.getEmail());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Développeur Java");
    }

    @Test
    void doitRetournerListeVideQuandAucuneOffreAcceptee() throws Exception {
        inscrireEtudiantSessionActuelle();

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        CvEtudiant cv = new CvEtudiant();
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.ACCEPTEE);

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        when(offreDeStageRepository.findOffresGeneralesDisponibles(
                eq(Statut.ACCEPTEE),
                eq(etudiant.getDepartment()),
                any(LocalDate.class)))
                .thenReturn(List.of());

        List<OffreDeStageDTO> result =
                etudiantService.getOffresDisponibles(
                        etudiant.getDepartment(),
                        etudiant.getEmail());

        assertThat(result).isEmpty();

        verify(etudiantRepository)
                .findByCredentialsEmail(etudiant.getEmail());

        verify(cvEtudiantRepository)
                .findByEtudiant(etudiant);

        verify(offreDeStageRepository)
                .findOffresGeneralesDisponibles(
                        eq(Statut.ACCEPTEE),
                        eq(etudiant.getDepartment()),
                        any(LocalDate.class));
    }

    @Test
    void doitRefuserAccesAuxOffresSiCvNonApprouve() throws Exception {
        inscrireEtudiantSessionActuelle();

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        CvEtudiant cv = new CvEtudiant();
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.EN_ATTENTE);

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        assertThatThrownBy(() ->
                etudiantService.getOffresDisponibles(
                        etudiant.getDepartment(),
                        etudiant.getEmail()))
                .isInstanceOf(CurriculumVitaeNonApprouveException.class);

        verify(etudiantRepository)
                .findByCredentialsEmail(etudiant.getEmail());

        verify(cvEtudiantRepository)
                .findByEtudiant(etudiant);

        verify(offreDeStageRepository, never())
                .findOffresGeneralesDisponibles(
                        any(Statut.class),
                        any(Departement.class),
                        any(LocalDate.class));
    }

    @Test
    void doitTelechargerFichierCv() throws Exception {
        Path fichier = dossierCvTest.resolve("cv.pdf");
        Files.createDirectories(fichier.getParent());
        Files.writeString(fichier, "contenu du cv pour test");

        CvEtudiant cv = CvEtudiant.builder()
                .fileName("cv.pdf")
                .storagePath(fichier.toString())
                .etudiant(etudiant)
                .build();

        when(etudiantRepository.findByCredentialsEmail("steveJean@gmail.com"))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        Resource result = etudiantService.telechargerCv("steveJean@gmail.com");

        assertThat(result).isNotNull();
        assertThat(result.exists()).isTrue();
    }

    @Test
    void doitLancerExceptionFichierIntrouvableLorsDuTelechargement() {
        when(etudiantRepository.findByCredentialsEmail("steveJean@gmail.com"))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                etudiantService.telechargerCv("steveJean@gmail.com"))
                .isInstanceOf(FichierIntrouvableException.class);
    }

    @Test
    void doitRetournerCandidaturesDeEtudiant() throws Exception {
        etudiant.setId(2L);

        OffreDeStage offre = new OffreDeStage();
        offre.setId(10L);

        Candidature candidature = new Candidature(
                etudiant,
                offre,
                LocalDateTime.now()
        );
        candidature.setId(1L);
        offre.setEmployeur(employeur);

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(candidatureRepository.findByEtudiant(etudiant))
                .thenReturn(List.of(candidature));

        List<CandidatureDTO> result =
                etudiantService.getCandidaturesEtudiant();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).etudiantId()).isEqualTo(2L);
        assertThat(result.get(0).offreId()).isEqualTo(10L);

        verify(candidatureRepository).findByEtudiant(etudiant);
    }

    @Test
    void doitRetournerListeVideQuandAucuneCandidature() throws Exception {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(candidatureRepository.findByEtudiant(etudiant))
                .thenReturn(List.of());

        List<CandidatureDTO> result =
                etudiantService.getCandidaturesEtudiant();

        assertThat(result).isEmpty();
    }

    @Test
    void doitLancerExceptionQuandEtudiantIntrouvablePourCandidatures() {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                etudiantService.getCandidaturesEtudiant())
                .isInstanceOf(EtudiantIntrouvableException.class);

        verify(candidatureRepository, never()).findByEtudiant(any(Etudiant.class));
    }

    @Test
    void doitPostulerAUneOffre() throws Exception {
        OffreDeStage offre = new OffreDeStage();
        offre.setId(10L);
        offre.setStatut(Statut.ACCEPTEE);
        offre.setDisplayEndDate(LocalDate.now().plusDays(10));
        offre.setEmployeur(employeur);

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        CvEtudiant cv = new CvEtudiant();
        cv.setId(5L);
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.ACCEPTEE);

        etudiant.setCv(cv);

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        when(offreDeStageRepository.findById(10L))
                .thenReturn(Optional.of(offre));

        when(candidatureRepository.existsByEtudiantAndOffreDeStage(
                etudiant, offre))
                .thenReturn(false);

        when(candidatureRepository.save(any(Candidature.class)))
                .thenAnswer(invocation -> {
                    Candidature candidature = invocation.getArgument(0);
                    candidature.setId(1L);
                    return candidature;
                });

        CandidatureDTO result =
                etudiantService.postuler(10L, etudiant.getEmail());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.offreId()).isEqualTo(10L);
        assertThat(result.cvId()).isEqualTo(5L);

        verify(candidatureRepository).save(any(Candidature.class));
    }

    @Test
    void doitLancerExceptionQuandEtudiantIntrouvablePourPostuler() {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.empty());

        CvEtudiant cv = new CvEtudiant();
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.ACCEPTEE);

        assertThatThrownBy(() ->
                etudiantService.postuler(10L, etudiant.getEmail()))
                .isInstanceOf(EtudiantIntrouvableException.class);

        verify(offreDeStageRepository, never()).findById(anyLong());
        verify(candidatureRepository, never()).save(any(Candidature.class));
        verify(applicationEventPublisher, never())
                .publishEvent(any(NouvelleCandidatureEvent.class));
    }

    @Test
    void doitLancerExceptionQuandOffreIntrouvable() {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        CvEtudiant cv = new CvEtudiant();
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.ACCEPTEE);

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        when(offreDeStageRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                etudiantService.postuler(10L, etudiant.getEmail()))
                .isInstanceOf(OffreIntrouvableException.class);

        verify(candidatureRepository, never()).save(any(Candidature.class));
        verify(applicationEventPublisher, never())
                .publishEvent(any(NouvelleCandidatureEvent.class));
    }

    @Test
    void doitLancerExceptionQuandOffreExpiree() {
        OffreDeStage offre = new OffreDeStage();
        offre.setId(10L);
        offre.setStatut(Statut.ACCEPTEE);
        offre.setDisplayEndDate(LocalDate.now().minusDays(1));

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        CvEtudiant cv = new CvEtudiant();
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.ACCEPTEE);

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        when(offreDeStageRepository.findById(10L))
                .thenReturn(Optional.of(offre));

        assertThatThrownBy(() ->
                etudiantService.postuler(10L, etudiant.getEmail()))
                .isInstanceOf(OffreDeStageIndisponibleException.class);

        verify(candidatureRepository, never()).save(any(Candidature.class));
        verify(applicationEventPublisher, never())
                .publishEvent(any(NouvelleCandidatureEvent.class));
    }


    @Test
    void doitRefuserCandidatureSiCvIntrouvable() throws Exception {
        etudiant.setId(2L);

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                etudiantService.postuler(10L, etudiant.getEmail()))
                .isInstanceOf(CurriculumVitaeIntrouvable.class);

        verify(candidatureRepository, never()).save(any(Candidature.class));
        verify(offreDeStageRepository, never()).findById(anyLong());
        verify(applicationEventPublisher, never())
                .publishEvent(any(NouvelleCandidatureEvent.class));
    }

    @Test
    void doitRefuserCandidatureSiCvNonApprouve() throws Exception {
        etudiant.setId(2L);

        CvEtudiant cv = new CvEtudiant();
        cv.setStatut(Statut.EN_ATTENTE);

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        assertThatThrownBy(() ->
                etudiantService.postuler(10L, etudiant.getEmail()))
                .isInstanceOf(CurriculumVitaeNonApprouveException.class);

        verify(candidatureRepository, never()).save(any(Candidature.class));
        verify(offreDeStageRepository, never()).findById(anyLong());
        verify(applicationEventPublisher, never())
                .publishEvent(any(NouvelleCandidatureEvent.class));
    }

    @Test
    void doitRefuserCandidatureEnDoubleSansPublierEvenement() {
        OffreDeStage offre = new OffreDeStage();
        offre.setId(10L);
        offre.setStatut(Statut.ACCEPTEE);
        offre.setDisplayEndDate(LocalDate.now().plusDays(10));
        offre.setEmployeur(employeur);

        CvEtudiant cv = new CvEtudiant();
        cv.setId(5L);
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.ACCEPTEE);

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        when(offreDeStageRepository.findById(10L))
                .thenReturn(Optional.of(offre));

        when(candidatureRepository.existsByEtudiantAndOffreDeStage(
                etudiant, offre))
                .thenReturn(true);

        assertThatThrownBy(() ->
                etudiantService.postuler(10L, etudiant.getEmail()))
                .isInstanceOf(CandidatureDejaExistanteException.class);

        verify(candidatureRepository, never())
                .save(any(Candidature.class));

        verify(applicationEventPublisher, never())
                .publishEvent(any(NouvelleCandidatureEvent.class));
    }

    @Test
    void doitRetournerListeVideSansConvocation() throws Exception {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(entrevueRepository.findByCandidatureEtudiantIdOrderByDateHeureEntrevueAsc(
                etudiant.getId()))
                .thenReturn(List.of());

        List<EntrevueDTO> resultat =
                etudiantService.getConvocationsEtudiant();

        assertThat(resultat).isEmpty();
    }

    @Test
    void doitObtenirConvocationNonConfirmee() throws Exception {
        entrevue.setDateHeureEntrevue(LocalDateTime.now().minusDays(1));

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(entrevueRepository.findByIdAndCandidatureEtudiantId(
                entrevue.getId(), etudiant.getId()))
                .thenReturn(Optional.of(entrevue));

        EntrevueDTO resultat = etudiantService.getConvocationEtudiant(entrevue.getId());

        assertThat(resultat.id()).isEqualTo(entrevue.getId());
        assertThat(resultat.offreTitle()).isEqualTo("Stage informatique");
        assertThat(resultat.dateConfirmation()).isNull();
        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.A_CONFIRMER);

        verify(entrevueRepository, never()).save(any(Entrevue.class));
    }

    @Test
    void doitRefuserConvocationIntrouvableOuNonAutorisee() {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(entrevueRepository.findByIdAndCandidatureEtudiantId(
                entrevue.getId(), etudiant.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> etudiantService.getConvocationEtudiant(entrevue.getId()))
                .isInstanceOf(ConvocationIntrouvableException.class);

        assertThatThrownBy(() -> etudiantService.accepterConvocationEtudiant(entrevue.getId()))
                .isInstanceOf(ConvocationIntrouvableException.class);

        verify(entrevueRepository, never()).save(any(Entrevue.class));
    }

    @Test
    void doitRetournerLesConvocationsEtActualiserLeStatut() throws Exception {
        entrevue.setDuree(60);
        entrevue.setDateConfirmation(LocalDateTime.now().minusDays(2));
        entrevue.setDateHeureEntrevue(LocalDateTime.now().minusDays(1));
        entrevue.setStatut(StatutEntrevue.CONFIRMEE);

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(entrevueRepository.findByCandidatureEtudiantIdOrderByDateHeureEntrevueAsc(
                etudiant.getId()))
                .thenReturn(List.of(entrevue));

        List<EntrevueDTO> resultat =
                etudiantService.getConvocationsEtudiant();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).id()).isEqualTo(entrevue.getId());
        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.TERMINEE);
    }

    @Test
    void doitConfirmerLaPresenceDeLEtudiant() throws Exception {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(entrevueRepository.findByIdAndCandidatureEtudiantId(
                entrevue.getId(), etudiant.getId()))
                .thenReturn(Optional.of(entrevue));

        ReponseConvocationDTO resultat = etudiantService.accepterConvocationEtudiant(entrevue.getId());

        assertThat(resultat.confirmee()).isTrue();
        assertThat(resultat.dateConfirmation()).isNotNull();
        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.CONFIRMEE);
        assertThat(entrevue.getCandidature().getStatut()).isEqualTo(StatutCandidature.ENTREVUE_PLANIFIEE);
        verify(candidatureRepository).save(entrevue.getCandidature());

        etudiantService.getConvocationEtudiant(entrevue.getId());

        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.CONFIRMEE);
        verify(entrevueRepository, times(1)).save(entrevue);
    }

    @Test
    void doitConserverLaPremiereDateDeConfirmation() throws Exception {
        LocalDateTime dateInitiale = LocalDateTime.now().minusDays(1);
        entrevue.setDateConfirmation(dateInitiale);
        entrevue.setStatut(StatutEntrevue.CONFIRMEE);

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(entrevueRepository.findByIdAndCandidatureEtudiantId(
                entrevue.getId(), etudiant.getId()))
                .thenReturn(Optional.of(entrevue));

        ReponseConvocationDTO resultat = etudiantService.accepterConvocationEtudiant(entrevue.getId());

        assertThat(resultat.dateConfirmation()).isEqualTo(dateInitiale);
        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.CONFIRMEE);

        verify(entrevueRepository, never()).save(any(Entrevue.class));
    }

    @Test
    void doitPublierUneSeuleNotificationPourUneAcceptationRepetee() throws Exception {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));
        when(entrevueRepository.findByIdAndCandidatureEtudiantId(entrevue.getId(), etudiant.getId()))
                .thenReturn(Optional.of(entrevue));

        etudiantService.accepterConvocationEtudiant(entrevue.getId());
        etudiantService.accepterConvocationEtudiant(entrevue.getId());

        verify(applicationEventPublisher, times(1)).publishEvent(new ConvocationRepondueEvent(entrevue));
        verify(entrevueRepository, times(1)).save(entrevue);
    }

    @Test
    void doitPublierUneSeuleNotificationPourUnRefusRepete() throws Exception {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));
        when(entrevueRepository.findByIdAndCandidatureEtudiantId(entrevue.getId(), etudiant.getId()))
                .thenReturn(Optional.of(entrevue));

        etudiantService.refuserConvocationEtudiant(entrevue.getId(), " Indisponible ");
        etudiantService.refuserConvocationEtudiant(entrevue.getId(), "Autre raison");

        assertThat(entrevue.getMotifRefus()).isEqualTo("Indisponible");
        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.REFUSEE);
        assertThat(entrevue.getCandidature().getStatut()).isEqualTo(StatutCandidature.ENTREVUE_REFUSEE);

        verify(applicationEventPublisher, times(1)).publishEvent(new ConvocationRepondueEvent(entrevue));
        verify(entrevueRepository, times(1)).save(entrevue);
    }

    @Test
    void neDoitPasNotifierPourUneConvocationNonAutorisee() {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));
        when(entrevueRepository.findByIdAndCandidatureEtudiantId(entrevue.getId(), etudiant.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> etudiantService.accepterConvocationEtudiant(entrevue.getId()))
                .isInstanceOf(ConvocationIntrouvableException.class);
        assertThatThrownBy(() -> etudiantService.refuserConvocationEtudiant(entrevue.getId(), "Indisponible"))
                .isInstanceOf(ConvocationIntrouvableException.class);
        verify(applicationEventPublisher, never()).publishEvent(any(ConvocationRepondueEvent.class));
    }

    @Test
    void doitRefuserUneConvocationSansMotif() {
        assertThatThrownBy(() -> etudiantService.refuserConvocationEtudiant(entrevue.getId(), "   "))
                .isInstanceOf(ConvocationInvalideException.class);

        verify(entrevueRepository, never()).save(any(Entrevue.class));
        verify(applicationEventPublisher, never()).publishEvent(any(ConvocationRepondueEvent.class));
    }

    @Test
    void doitActualiserLeSuiviLorsDeLaConsultation() throws Exception {
        entrevue.setDuree(60);
        entrevue.setDateConfirmation(LocalDateTime.now().minusDays(2));
        entrevue.setDateHeureEntrevue(LocalDateTime.now().minusDays(1));
        entrevue.setStatut(StatutEntrevue.CONFIRMEE);

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(entrevueRepository.findByIdAndCandidatureEtudiantId(
                entrevue.getId(), etudiant.getId()))
                .thenReturn(Optional.of(entrevue));

        when(entrevueRepository.findByCandidatureEtudiantIdOrderByDateHeureEntrevueAsc(etudiant.getId()))
                .thenReturn(List.of(entrevue));

        etudiantService.getConvocationEtudiant(entrevue.getId());

        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.TERMINEE);
    }

    @Test
    void doitResterEnRechercheAvantUneEntrevueFuture() {
        Candidature candidature = new Candidature(etudiant, new OffreDeStage(), LocalDateTime.now());
        candidature.setStatut(StatutCandidature.ENTREVUE_PLANIFIEE);

        Entrevue entrevueAConfirmer = Entrevue.builder()
                .candidature(candidature)
                .format(FormatEntrevue.EN_LIGNE)
                .dateHeureEntrevue(LocalDateTime.now().plusDays(1))
                .dateCreation(LocalDateTime.now())
                .build();
        entrevueAConfirmer.setId(55L);
        entrevueAConfirmer.setStatut(StatutEntrevue.A_CONFIRMER);

        when(entrevueRepository.findByCandidatureEtudiantIdOrderByDateHeureEntrevueAsc(etudiant.getId()))
                .thenReturn(List.of(entrevueAConfirmer));

        etudiant.setStatut(StatutEtudiant.EN_RECHERCHE);
        etudiantService.actualiserStatutEtudiant(etudiant);

        assertThat(etudiant.getStatut()).isEqualTo(StatutEtudiant.EN_RECHERCHE);

        entrevueAConfirmer.setStatut(StatutEntrevue.CONFIRMEE);
        etudiantService.actualiserStatutEtudiant(etudiant);

        assertThat(etudiant.getStatut()).isEqualTo(StatutEtudiant.EN_RECHERCHE);
    }

    @Test
    void doitRefuserUnStageNonAttribueParLEmployeur() throws Exception {
        Candidature candidature = entrevue.getCandidature();
        candidature.setStatut(StatutCandidature.SOUMISE);
        when(etudiantRepository.findByEmailForUpdate(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(candidatureRepository.findByIdAndEtudiantId(20L, etudiant.getId())).thenReturn(Optional.of(candidature));

        assertThatThrownBy(() -> etudiantService.accepterStage(20L))
                .isInstanceOf(DecisionStageInvalideException.class);
        assertThat(etudiant.getCandidatureStage()).isNull();
        verify(etudiantRepository, never()).save(any());
        verify(candidatureRepository, never()).save(any());
        verifyNoInteractions(applicationEventPublisher);
    }

    @Test
    void doitConserverLeContratSurUneAcceptationRepetee() throws Exception {
        Candidature candidature = entrevue.getCandidature();
        candidature.setStatut(StatutCandidature.CONTRAT_CREE);
        etudiant.setCandidatureStage(candidature);
        etudiant.setStatut(StatutEtudiant.STAGE_TROUVE);
        when(etudiantRepository.findByEmailForUpdate(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(candidatureRepository.findByIdAndEtudiantId(20L, etudiant.getId())).thenReturn(Optional.of(candidature));

        CandidatureDTO resultat = etudiantService.accepterStage(20L);

        assertThat(resultat.statut()).isEqualTo(StatutCandidature.CONTRAT_CREE);
        verify(applicationEventPublisher, never()).publishEvent(any(StageTrouveEvent.class));
        verify(candidatureRepository, never()).save(any());
    }

    @Test
    void doitRefuserUneCandidatureNonAccepteeParLEmployeur() {
        when(etudiantRepository.findByEmailForUpdate(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(candidatureRepository.findByIdAndEtudiantId(20L, etudiant.getId()))
                .thenReturn(Optional.of(entrevue.getCandidature()));

        assertThatThrownBy(() -> etudiantService.accepterStage(20L))
                .isInstanceOf(DecisionStageInvalideException.class);
        verify(etudiantRepository, never()).save(any());
    }

    @Test
    void doitRefuserLaDecisionSurLaCandidatureDUnAutreEtudiant() {
        when(etudiantRepository.findByEmailForUpdate(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(candidatureRepository.findByIdAndEtudiantId(99L, etudiant.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> etudiantService.accepterStage(99L))
                .isInstanceOf(CandidatureIntrouvableException.class);
        assertThatThrownBy(() -> etudiantService.refuserStage(99L))
                .isInstanceOf(CandidatureIntrouvableException.class);
        verify(etudiantRepository, never()).save(any());
    }

    @Test
    void doitRefuserUnDeuxiemeStage() {
        Candidature stageChoisi = new Candidature();
        stageChoisi.setId(30L);
        etudiant.setCandidatureStage(stageChoisi);
        when(etudiantRepository.findByEmailForUpdate(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(candidatureRepository.findByIdAndEtudiantId(20L, etudiant.getId()))
                .thenReturn(Optional.of(entrevue.getCandidature()));

        assertThatThrownBy(() -> etudiantService.accepterStage(20L))
                .isInstanceOf(DecisionStageInvalideException.class);
        assertThat(etudiant.getCandidatureStage()).isSameAs(stageChoisi);
        assertThatThrownBy(() -> etudiantService.refuserStage(20L))
                .isInstanceOf(DecisionStageInvalideException.class);
    }

    @Test
    void doitRefuserLaConfirmationSiLesAnciennesReservationsDepassentLaCapacite() {
        Candidature candidature = entrevue.getCandidature();
        candidature.setStatut(StatutCandidature.ACCEPTEE);
        var offre = candidature.getOffreDeStage();
        offre.setNombreCandidatsMax(1);
        when(etudiantRepository.findByEmailForUpdate(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(candidatureRepository.findByIdAndEtudiantId(20L, etudiant.getId())).thenReturn(Optional.of(candidature));
        when(offreDeStageRepository.findByIdForUpdate(offre.getId())).thenReturn(Optional.of(offre));
        when(candidatureRepository.countByOffreDeStageIdAndStatutIn(offre.getId(),
                List.of(StatutCandidature.ACCEPTEE, StatutCandidature.STAGE_TROUVE, StatutCandidature.CONTRAT_CREE))).thenReturn(2L);

        assertThatThrownBy(() -> etudiantService.accepterStage(20L))
                .isInstanceOf(ActionsCandidatureDesactiveesException.class)
                .hasMessage("error.offer_capacity_reached");

        var ordre = inOrder(offreDeStageRepository, candidatureRepository);
        ordre.verify(offreDeStageRepository).findByIdForUpdate(offre.getId());
        ordre.verify(candidatureRepository).countByOffreDeStageIdAndStatutIn(offre.getId(),
                List.of(StatutCandidature.ACCEPTEE, StatutCandidature.STAGE_TROUVE, StatutCandidature.CONTRAT_CREE));
        assertThat(candidature.getStatut()).isEqualTo(StatutCandidature.ACCEPTEE);
        assertThat(etudiant.getCandidatureStage()).isNull();
        verify(etudiantRepository, never()).save(any());
        verify(candidatureRepository, never()).save(any());
        verifyNoInteractions(contratRepository, gestionnaireRepository, applicationEventPublisher);
    }

    @Test
    void doitCreerLeContratEtAttribuerLeStageDesLAcceptationEtudiante() throws Exception {
        Candidature candidature = entrevue.getCandidature();
        candidature.setStatut(StatutCandidature.ACCEPTEE);
        var offre = candidature.getOffreDeStage();
        offre.setCandidatures(List.of(candidature));
        assertThat(offre.getNombreCandidatsAcceptes()).isEqualTo(1);
        when(etudiantRepository.findByEmailForUpdate(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(candidatureRepository.findByIdAndEtudiantId(20L, etudiant.getId())).thenReturn(Optional.of(candidature));
        when(offreDeStageRepository.findByIdForUpdate(offre.getId())).thenReturn(Optional.of(offre));
        when(candidatureRepository.countByOffreDeStageIdAndStatutIn(offre.getId(),
                List.of(StatutCandidature.ACCEPTEE, StatutCandidature.STAGE_TROUVE, StatutCandidature.CONTRAT_CREE)))
                .thenReturn(1L);
        Gestionnaire gestionnaire = new Gestionnaire();
        when(gestionnaireRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(gestionnaire));
        when(contratRepository.save(any(Contrat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CandidatureDTO resultat = etudiantService.accepterStage(20L);

        assertThat(resultat.statut()).isEqualTo(StatutCandidature.CONTRAT_CREE);
        assertThat(resultat.statutEtudiant()).isEqualTo(StatutEtudiant.STAGE_TROUVE);
        assertThat(etudiant.getCandidatureStage()).isSameAs(candidature);
        assertThat(offre.getNombreCandidatsAcceptes()).isEqualTo(1);
        assertThat(offre.estComplete()).isTrue();
        ArgumentCaptor<Contrat> contratCapture = ArgumentCaptor.forClass(Contrat.class);
        verify(contratRepository).save(contratCapture.capture());
        assertThat(contratCapture.getValue().getGestionnaire()).isSameAs(gestionnaire);
        assertThat(contratCapture.getValue().getCandidature()).isSameAs(candidature);
        verify(applicationEventPublisher).publishEvent(any(StageTrouveEvent.class));
        verify(applicationEventPublisher).publishEvent(any(com.lacouf.rsbjwt.model.event.NouveauContratCreeEvent.class));
    }

    @Test
    void doitLibererLaPlaceReserveeQuandLEtudiantRefuseLeStage() throws Exception {
        Candidature candidature = entrevue.getCandidature();
        candidature.setStatut(StatutCandidature.ACCEPTEE);
        var offre = candidature.getOffreDeStage();
        var autreCandidature = new Candidature(new Etudiant(), offre, LocalDateTime.now());
        autreCandidature.setId(30L);
        offre.setCandidatures(List.of(candidature, autreCandidature));
        assertThat(offre.estComplete()).isTrue();
        assertThat(candidature.actionsDesactivees()).isFalse();
        assertThat(autreCandidature.actionsDesactivees()).isTrue();
        when(etudiantRepository.findByEmailForUpdate(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(candidatureRepository.findByIdAndEtudiantId(20L, etudiant.getId())).thenReturn(Optional.of(candidature));
        when(offreDeStageRepository.findByIdForUpdate(offre.getId())).thenReturn(Optional.of(offre));

        var resultat = etudiantService.refuserStage(20L);

        assertThat(resultat.statut()).isEqualTo(StatutCandidature.REFUSEE);
        assertThat(offre.estComplete()).isFalse();
        assertThat(autreCandidature.actionsDesactivees()).isFalse();
        assertThat(etudiant.getCandidatureStage()).isNull();
        verifyNoInteractions(contratRepository);
        verify(applicationEventPublisher).publishEvent(any(com.lacouf.rsbjwt.model.event.StageRefuseEvent.class));
    }

    @Test
    void doitConserverLaCandidatureSiAucunGestionnaireNePeutPrendreLeContrat() {
        Candidature candidature = entrevue.getCandidature();
        candidature.setStatut(StatutCandidature.ACCEPTEE);
        var offre = candidature.getOffreDeStage();
        when(etudiantRepository.findByEmailForUpdate(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(candidatureRepository.findByIdAndEtudiantId(20L, etudiant.getId())).thenReturn(Optional.of(candidature));
        when(offreDeStageRepository.findByIdForUpdate(offre.getId())).thenReturn(Optional.of(offre));

        assertThatThrownBy(() -> etudiantService.accepterStage(20L))
                .isInstanceOf(com.lacouf.rsbjwt.exception.contrat.CreationContratImpossibleException.class);
        assertThat(candidature.getStatut()).isEqualTo(StatutCandidature.ACCEPTEE);
        assertThat(etudiant.getCandidatureStage()).isNull();
        verifyNoInteractions(contratRepository, applicationEventPublisher);
    }
    @Test
    void doitAttendreLaFinDeLEntrevue() {
        entrevue.getCandidature().setStatut(StatutCandidature.ENTREVUE_PLANIFIEE);
        entrevue.setStatut(StatutEntrevue.CONFIRMEE);
        entrevue.setDateHeureEntrevue(LocalDateTime.now().minusMinutes(10));
        entrevue.setDuree(60);
        when(entrevueRepository.findByCandidatureEtudiantIdOrderByDateHeureEntrevueAsc(etudiant.getId()))
                .thenReturn(List.of(entrevue));
        etudiantService.actualiserStatutEtudiant(etudiant);
        assertThat(etudiant.getStatut()).isEqualTo(StatutEtudiant.EN_RECHERCHE);
        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.CONFIRMEE);
    }

    @Test
    void doitPasserAutomatiquementEnAttenteDeReponse() {
        entrevue.getCandidature().setStatut(StatutCandidature.ENTREVUE_PLANIFIEE);
        entrevue.setStatut(StatutEntrevue.CONFIRMEE);
        entrevue.setDuree(60);
        when(entrevueRepository.findByCandidatureEtudiantIdOrderByDateHeureEntrevueAsc(etudiant.getId()))
                .thenReturn(List.of(entrevue));
        entrevue.setDateHeureEntrevue(LocalDateTime.now().minusHours(2));
        when(etudiantRepository.findAll()).thenReturn(List.of(etudiant));
        etudiantService.actualiserStatutsEtudiants();
        assertThat(etudiant.getStatut()).isEqualTo(StatutEtudiant.EN_ATTENTE_REPONSE);
        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.TERMINEE);
        verify(entrevueRepository).save(entrevue);
        verify(applicationEventPublisher).publishEvent(new EntrevueTermineeEvent(entrevue));
    }

    @Test
    void doitSignerContratAvecSucces() throws Exception {
        Long contratId = 1L;
        String motDePasse = "Passewod123";

        Gestionnaire gestionnaire = new Gestionnaire(
                1L,
                "Laurent",
                "Losange",
                "Laurent.Losange@hotmail.com",
                "5858",
                "458-458-4584"
        );

        gestionnaire.setId(1L);

        Candidature candidature = Candidature.builder()
                .etudiant(etudiant)
                .offreDeStage(new OffreDeStage())
                .build();

        candidature.setId(1L);

        Contrat contrat = Contrat.builder()
                .candidature(candidature)
                .gestionnaire(gestionnaire)
                .build();

        contrat.setId(contratId);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(etudiant.getEmail(), null)
        );

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(contratRepository.findById(contratId))
                .thenReturn(Optional.of(contrat));

        when(passwordEncoder.matches(motDePasse, etudiant.getPassword()))
                .thenReturn(true);

        when(contratRepository.save(any(Contrat.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ContratDTO result = etudiantService.signerContrat(contratId, motDePasse);
        verify(applicationEventPublisher).publishEvent(new ContratSigneEvent(contrat));

        assertThat(result).isNotNull();
        assertThat(contrat.getDateSignatureEtudiant()).isNotNull();

        verify(etudiantRepository).findByCredentialsEmail(etudiant.getEmail());
        verify(contratRepository).findById(contratId);
        verify(passwordEncoder).matches(motDePasse, etudiant.getPassword());
        verify(contratRepository).save(contrat);
    }

    @Test
    void doitLeverExceptionSiEtudiantIntrouvablePourSignatureContrat() {
        Long contratId = 1L;
        String motDePasse = "Passewod123";

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(etudiant.getEmail(), null)
        );

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                etudiantService.signerContrat(contratId, motDePasse))
                .isInstanceOf(EtudiantIntrouvableException.class);

        verify(contratRepository, never()).findById(anyLong());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(contratRepository, never()).save(any());
    }

    @Test
    void doitLeverExceptionSiContratIntrouvablePourSignature() {
        Long contratId = 99L;
        String motDePasse = "Passewod123";

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(etudiant.getEmail(), null)
        );

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(contratRepository.findById(contratId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                etudiantService.signerContrat(contratId, motDePasse))
                .isInstanceOf(ContratIntrouvableException.class);

        verify(contratRepository).findById(contratId);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(contratRepository, never()).save(any());
    }

    @Test
    void doitLeverExceptionSiMotDePasseInvalidePourSignatureContrat() {
        Long contratId = 1L;
        String motDePasse = "MauvaisMotDePasse";

        Gestionnaire gestionnaire = new Gestionnaire(
                1L,
                "Laurent",
                "Losange",
                "Laurent.Losange@hotmail.com",
                "5858",
                "458-458-4584"
        );

        Candidature candidature = Candidature.builder()
                .etudiant(etudiant)
                .offreDeStage(new OffreDeStage())
                .build();

        Contrat contrat = Contrat.builder()
                .candidature(candidature)
                .gestionnaire(gestionnaire)
                .build();

        contrat.setId(contratId);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(etudiant.getEmail(), null)
        );

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(contratRepository.findById(contratId))
                .thenReturn(Optional.of(contrat));

        when(passwordEncoder.matches(motDePasse, etudiant.getPassword()))
                .thenReturn(false);

        assertThatThrownBy(() ->
                etudiantService.signerContrat(contratId, motDePasse))
                .isInstanceOf(MotDePasseInvalideException.class);

        verify(passwordEncoder).matches(motDePasse, etudiant.getPassword());
        verify(contratRepository, never()).save(any());
    }


    @Test
    void doitRevenirEnRechercheApresLaDecision() {
        entrevue.setStatut(StatutEntrevue.CONFIRMEE);
        entrevue.setDuree(60);
        etudiant.setStatut(StatutEtudiant.EN_ATTENTE_REPONSE);
        entrevue.setDateHeureEntrevue(LocalDateTime.now().minusHours(2));
        entrevue.getCandidature().setStatut(StatutCandidature.REFUSEE);
        when(entrevueRepository.findByCandidatureEtudiantIdOrderByDateHeureEntrevueAsc(etudiant.getId()))
                .thenReturn(List.of(entrevue));
        etudiantService.actualiserStatutEtudiant(etudiant);
        assertThat(etudiant.getStatut()).isEqualTo(StatutEtudiant.EN_RECHERCHE);
        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.TERMINEE);
    }

    @Test
    void doitBloquerLesReponsesAuxConvocationsQuandLOffreEstComplete() {
        var offre = entrevue.getCandidature().getOffreDeStage();
        var acceptee = new Candidature(new Etudiant(), offre, LocalDateTime.now());
        acceptee.setStatut(StatutCandidature.STAGE_TROUVE);
        offre.setCandidatures(List.of(acceptee, entrevue.getCandidature()));
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(entrevueRepository.findByIdAndCandidatureEtudiantId(entrevue.getId(), etudiant.getId()))
                .thenReturn(Optional.of(entrevue));

        assertThatThrownBy(() -> etudiantService.accepterConvocationEtudiant(entrevue.getId()))
                .isInstanceOf(ActionsCandidatureDesactiveesException.class)
                .hasMessage("error.offer_capacity_reached");
        assertThatThrownBy(() -> etudiantService.refuserConvocationEtudiant(entrevue.getId(), "Indisponible"))
                .isInstanceOf(ActionsCandidatureDesactiveesException.class);
        verify(candidatureRepository, never()).save(any());
        verify(entrevueRepository, never()).save(any());
        verifyNoInteractions(applicationEventPublisher);
    }

    @Test
    void doitInitialiserLaCapaciteEtLesCandidaturesAvecLeConstructeurDesDonneesDemo() {
        var offre = new OffreDeStage("Technician Reseaux", "Support informatique", 20.0,
                Departement.INFORMATIQUE, LocalDate.now().plusDays(5), LocalDate.now().plusMonths(2),
                LocalDate.now().plusDays(2), Statut.ACCEPTEE, "offre.pdf", "uploads/offres",
                null, "", LocalDateTime.now());

        assertThat(offre.getNombreCandidatsMax()).isEqualTo(1);
        assertThat(offre.getCandidatures()).isEmpty();
        assertThat(offre.estComplete()).isFalse();
    }

    @Test
    void doitCompterLesPlacesOccupeesEtExposerLesActionsDesactivees() {
        var offre = new OffreDeStage();
        offre.setId(10L);
        offre.setNombreCandidatsMax(3);
        var candidatures = new java.util.ArrayList<Candidature>();
        for (var statut : List.of(StatutCandidature.ACCEPTEE, StatutCandidature.STAGE_TROUVE,
                StatutCandidature.CONTRAT_CREE, StatutCandidature.REFUSEE, StatutCandidature.SOUMISE)) {
            var candidat = new Etudiant();
            candidat.setId((long) candidatures.size() + 1);
            var candidature = new Candidature(candidat, offre, LocalDateTime.now());
            candidature.setId((long) candidatures.size() + 1);
            candidature.setStatut(statut);
            candidatures.add(candidature);
        }
        offre.setCandidatures(candidatures);

        var resultat = OffreDeStageDTO.of(offre);
        assertThat(resultat.nombreCandidatsMax()).isEqualTo(3);
        assertThat(resultat.nombreCandidatsAcceptes()).isEqualTo(3);
        assertThat(CandidatureDTO.of(candidatures.get(4)).actionsDesactivees()).isTrue();
        assertThat(CandidatureDTO.of(candidatures.get(0)).actionsDesactivees()).isFalse();
        assertThat(CandidatureDTO.of(candidatures.get(1)).actionsDesactivees()).isFalse();
    }

    @Test
    void doitConserverLeStageTrouveMalgreUneAutreEntrevue() {
        etudiant.setCandidatureStage(new Candidature());
        when(entrevueRepository.findByCandidatureEtudiantIdOrderByDateHeureEntrevueAsc(etudiant.getId()))
                .thenReturn(List.of(entrevue));
        etudiantService.actualiserStatutEtudiant(etudiant);
        assertThat(etudiant.getStatut()).isEqualTo(StatutEtudiant.STAGE_TROUVE);
    }

    @Test
    void contratSigneSeulementApresLesTroisSignatures() throws Exception {
        for (int signatureManquante = 0; signatureManquante <= 4; signatureManquante++) {
            Candidature candidature = new Candidature(etudiant, new OffreDeStage(), LocalDateTime.now());
            candidature.setId(20L);
            candidature.setStatut(StatutCandidature.CONTRAT_CREE);

            Contrat contrat = new Contrat();
            contrat.setCandidature(candidature);

            if (signatureManquante != 1)
                contrat.setDateSignatureEmployeur(LocalDateTime.now());
            if (signatureManquante != 2)
                contrat.setDateSignatureEtudiant(LocalDateTime.now());
            if (signatureManquante != 3)
                contrat.setDateSignatureGestionnaire(LocalDateTime.now());

            when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                    .thenReturn(Optional.of(etudiant));
            when(candidatureRepository.findByEtudiant(etudiant))
                    .thenReturn(List.of(candidature));
            when(contratRepository.findByCandidatureId(20L))
                    .thenReturn(signatureManquante == 0 ? Optional.empty() : Optional.of(contrat));

            CandidatureDTO resultat = etudiantService.getCandidaturesEtudiant().get(0);

            assertThat(resultat.contratSigne()).isEqualTo(signatureManquante == 4);
            assertThat(resultat.statut()).isEqualTo(StatutCandidature.CONTRAT_CREE);
        }
    }

    @Test
    void doitBloquerLesReponsesAuxAutresConvocationsApresStageChoisi() {
        Candidature stage = new Candidature();
        stage.setId(999L);
        etudiant.setCandidatureStage(stage);
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(entrevueRepository.findByIdAndCandidatureEtudiantId(entrevue.getId(), etudiant.getId()))
                .thenReturn(Optional.of(entrevue));

        assertThatThrownBy(() -> etudiantService.accepterConvocationEtudiant(entrevue.getId()))
                .isInstanceOf(ActionsCandidatureDesactiveesException.class);
        assertThatThrownBy(() -> etudiantService.refuserConvocationEtudiant(entrevue.getId(), "Autre stage choisi"))
                .isInstanceOf(ActionsCandidatureDesactiveesException.class);
        verify(entrevueRepository, never()).save(any());
        verify(candidatureRepository, never()).save(any());
        verifyNoInteractions(applicationEventPublisher);
    }

    @Test
    void doitRefuserDeSignerLeContratDUnAutreEtudiant() {
        Etudiant autreEtudiant = new Etudiant();
        autreEtudiant.setId(99L);
        Contrat contrat = Contrat.builder()
                .candidature(new Candidature(autreEtudiant, new OffreDeStage(), LocalDateTime.now())).build();
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(contratRepository.findById(1L)).thenReturn(Optional.of(contrat));

        assertThatThrownBy(() -> etudiantService.signerContrat(1L, "motDePasse"))
                .isInstanceOf(OffreNonAutoriseeException.class);
        verify(contratRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder, applicationEventPublisher);
    }

    @Test
    void doitConserverLaConfirmationDUneEntrevueDejaTerminee() throws Exception {
        entrevue.setStatut(StatutEntrevue.TERMINEE);
        LocalDateTime confirmation = LocalDateTime.of(2026, 11, 1, 10, 0);
        entrevue.setDateConfirmation(confirmation);
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(entrevueRepository.findByIdAndCandidatureEtudiantId(30L, etudiant.getId()))
                .thenReturn(Optional.of(entrevue));

        etudiantService.accepterConvocationEtudiant(30L);

        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.TERMINEE);
        assertThat(entrevue.getDateConfirmation()).isEqualTo(confirmation);
        verify(entrevueRepository, never()).save(any());
        verifyNoInteractions(applicationEventPublisher);
    }

    @Test
    void doitRefuserDeConfirmerUneConvocationDejaRefusee() {
        entrevue.setStatut(StatutEntrevue.REFUSEE);
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(entrevueRepository.findByIdAndCandidatureEtudiantId(30L, etudiant.getId()))
                .thenReturn(Optional.of(entrevue));

        assertThatThrownBy(() -> etudiantService.accepterConvocationEtudiant(30L))
                .isInstanceOf(ConvocationInvalideException.class);
        verify(entrevueRepository, never()).save(any());
        verifyNoInteractions(applicationEventPublisher);
    }

    @Test
    void doitRefuserDeConfirmerUneConvocationDontLaDateEstPassee() {
        entrevue.setDateHeureEntrevue(LocalDateTime.now().minusHours(1));
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(entrevueRepository.findByIdAndCandidatureEtudiantId(30L, etudiant.getId()))
                .thenReturn(Optional.of(entrevue));

        assertThatThrownBy(() -> etudiantService.accepterConvocationEtudiant(30L))
                .isInstanceOf(ConvocationInvalideException.class);
        assertThat(entrevue.getStatut()).isEqualTo(StatutEntrevue.A_CONFIRMER);
        verify(entrevueRepository, never()).save(any());
        verifyNoInteractions(applicationEventPublisher);
    }

    @Test
    void doitRetournerMonCvQuandIlExiste() throws Exception {
        CvEtudiant cv = CvEtudiant.builder()
                .fileName("mon-cv.pdf")
                .contentType("application/pdf")
                .fileSize(1024L)
                .uploadDate(LocalDateTime.now())
                .etudiant(etudiant)
                .build();
        cv.setId(1L);

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        MonCvTeleverseDTO resultat = etudiantService.getMonCv();

        assertThat(resultat).isNotNull();
        assertThat(resultat.cvTeleverse()).isTrue();
        assertThat(resultat.cv()).isNotNull();
        assertThat(resultat.cv().fileName()).isEqualTo("mon-cv.pdf");

        verify(cvEtudiantRepository).findByEtudiant(etudiant);
    }

    @Test
    void doitRetournerAucunCvQuandEtudiantNenPossedePas() throws Exception {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.empty());

        MonCvTeleverseDTO resultat = etudiantService.getMonCv();

        assertThat(resultat).isNotNull();
        assertThat(resultat.cvTeleverse()).isFalse();
        assertThat(resultat.cv()).isNull();
    }

    @Test
    void doitLeverExceptionSiEtudiantIntrouvablePourMonCv() {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> etudiantService.getMonCv())
                .isInstanceOf(EtudiantIntrouvableException.class);

        verify(cvEtudiantRepository, never()).findByEtudiant(any());
    }

    @Test
    void doitRetournerAucunProfesseurQuandEtudiantNenPossedePas() throws Exception {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        etudiant.setProfesseur(null);

        MonProfesseurAssigneDTO resultat =
                etudiantService.getMonProfesseur();

        assertThat(resultat).isNotNull();
        assertThat(resultat.professeurAssigne()).isFalse();
        assertThat(resultat.professeur()).isNull();
    }

    @Test
    void doitLeverExceptionSiEtudiantIntrouvablePourMonProfesseur() {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> etudiantService.getMonProfesseur())
                .isInstanceOf(EtudiantIntrouvableException.class);
    }

    @Test
    void doitRetournerUniquementLesOffresNonPostulees() throws Exception {
        inscrireEtudiantSessionActuelle();

        OffreDeStage offre1 = creerOffre(1L, "Développeur Java");
        OffreDeStage offre2 = creerOffre(2L, "Développeur Web");

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        CvEtudiant cv = new CvEtudiant();
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.ACCEPTEE);

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        when(offreDeStageRepository.findOffresGeneralesDisponibles(
                eq(Statut.ACCEPTEE),
                eq(etudiant.getDepartment()),
                any(LocalDate.class)))
                .thenReturn(List.of(offre1, offre2));

        when(offreDeStageRepository.findOffresCibleesPourEtudiant(
                eq(Statut.ACCEPTEE),
                eq(etudiant.getDepartment()),
                any(LocalDate.class),
                eq(etudiant)))
                .thenReturn(List.of());

        Candidature candidature = new Candidature(
                etudiant, offre1, LocalDateTime.now());

        when(candidatureRepository.findByEtudiant(etudiant))
                .thenReturn(List.of(candidature));

        List<OffreDeStageDTO> resultat =
                etudiantService.getOffresNonPostulees();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).id()).isEqualTo(2L);
        assertThat(resultat.get(0).title()).isEqualTo("Développeur Web");

        verify(candidatureRepository).findByEtudiant(etudiant);
    }

    @Test
    void doitRetournerListeVideQuandToutesLesOffresSontDejaPostulees()
            throws Exception {
        inscrireEtudiantSessionActuelle();

        OffreDeStage offre = creerOffre(1L, "Développeur Java");

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        CvEtudiant cv = new CvEtudiant();
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.ACCEPTEE);

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        when(offreDeStageRepository.findOffresGeneralesDisponibles(
                eq(Statut.ACCEPTEE),
                eq(etudiant.getDepartment()),
                any(LocalDate.class)))
                .thenReturn(List.of(offre));

        when(offreDeStageRepository.findOffresCibleesPourEtudiant(
                eq(Statut.ACCEPTEE),
                eq(etudiant.getDepartment()),
                any(LocalDate.class),
                eq(etudiant)))
                .thenReturn(List.of());

        Candidature candidature = new Candidature(
                etudiant, offre, LocalDateTime.now());

        when(candidatureRepository.findByEtudiant(etudiant))
                .thenReturn(List.of(candidature));

        List<OffreDeStageDTO> resultat =
                etudiantService.getOffresNonPostulees();

        assertThat(resultat).isEmpty();
    }

    @Test
    void doitRetournerListeVideQuandAucuneOffreDisponible()
            throws Exception {
        inscrireEtudiantSessionActuelle();

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        CvEtudiant cv = new CvEtudiant();
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.ACCEPTEE);

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        when(offreDeStageRepository.findOffresGeneralesDisponibles(
                eq(Statut.ACCEPTEE),
                eq(etudiant.getDepartment()),
                any(LocalDate.class)))
                .thenReturn(List.of());

        when(offreDeStageRepository.findOffresCibleesPourEtudiant(
                eq(Statut.ACCEPTEE),
                eq(etudiant.getDepartment()),
                any(LocalDate.class),
                eq(etudiant)))
                .thenReturn(List.of());

        when(candidatureRepository.findByEtudiant(etudiant))
                .thenReturn(List.of());

        List<OffreDeStageDTO> resultat =
                etudiantService.getOffresNonPostulees();

        assertThat(resultat).isEmpty();
    }

    @Test
    void doitLeverExceptionSiCvNonApprouvePourOffresNonPostulees() {
        inscrireEtudiantSessionActuelle();

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        CvEtudiant cv = new CvEtudiant();
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.EN_ATTENTE);

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        assertThatThrownBy(() -> etudiantService.getOffresNonPostulees())
                .isInstanceOf(CurriculumVitaeNonApprouveException.class);

        verify(candidatureRepository, never()).findByEtudiant(any());
    }

    @Test
    void doitLeverExceptionSiEtudiantIntrouvablePourOffresNonPostulees() {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> etudiantService.getOffresNonPostulees())
                .isInstanceOf(EtudiantIntrouvableException.class);

        verify(cvEtudiantRepository, never()).findByEtudiant(any());
    }
    @Test
    void doitRetournerLesOffresApprouveesDepuisDerniereVisite()
            throws Exception {
        inscrireEtudiantSessionActuelle();

        LocalDateTime derniereVisite =
                LocalDateTime.of(2026, 11, 10, 10, 0);

        etudiant.setPreviousLoginAt(derniereVisite);

        OffreDeStage nouvelleOffre =
                creerOffre(1L, "Nouvelle offre");

        nouvelleOffre.setApprovedAt(derniereVisite.plusDays(1));

        OffreDeStage ancienneOffre =
                creerOffre(2L, "Ancienne offre");

        ancienneOffre.setApprovedAt(derniereVisite.minusDays(1));

        OffreDeStage sansDateApprobation =
                creerOffre(3L, "Offre sans date");

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        CvEtudiant cv = new CvEtudiant();
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.ACCEPTEE);

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        when(offreDeStageRepository.findOffresGeneralesDisponibles(
                eq(Statut.ACCEPTEE),
                eq(etudiant.getDepartment()),
                any(LocalDate.class)))
                .thenReturn(List.of(
                        nouvelleOffre,
                        ancienneOffre,
                        sansDateApprobation));

        when(offreDeStageRepository.findOffresCibleesPourEtudiant(
                eq(Statut.ACCEPTEE),
                eq(etudiant.getDepartment()),
                any(LocalDate.class),
                eq(etudiant)))
                .thenReturn(List.of());

        List<OffreDeStageDTO> resultat =
                etudiantService.getNouvellesOffresDepuisDerniereVisite();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).id()).isEqualTo(1L);
        assertThat(resultat.get(0).title()).isEqualTo("Nouvelle offre");
    }

    @Test
    void doitRetournerToutesLesOffresApprouveesSiAucuneVisitePrecedente()
            throws Exception {
        inscrireEtudiantSessionActuelle();

        etudiant.setPreviousLoginAt(null);

        OffreDeStage offre1 = creerOffre(1L, "Offre approuvée");
        offre1.setApprovedAt(LocalDateTime.now());

        OffreDeStage offre2 = creerOffre(2L, "Autre offre approuvée");
        offre2.setApprovedAt(LocalDateTime.now());

        OffreDeStage offre3 = creerOffre(3L, "Offre sans approbation");

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        CvEtudiant cv = new CvEtudiant();
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.ACCEPTEE);

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        when(offreDeStageRepository.findOffresGeneralesDisponibles(
                eq(Statut.ACCEPTEE),
                eq(etudiant.getDepartment()),
                any(LocalDate.class)))
                .thenReturn(List.of(offre1, offre2, offre3));

        when(offreDeStageRepository.findOffresCibleesPourEtudiant(
                eq(Statut.ACCEPTEE),
                eq(etudiant.getDepartment()),
                any(LocalDate.class),
                eq(etudiant)))
                .thenReturn(List.of());

        List<OffreDeStageDTO> resultat =
                etudiantService.getNouvellesOffresDepuisDerniereVisite();

        assertThat(resultat).hasSize(2);
        assertThat(resultat)
                .extracting(OffreDeStageDTO::id)
                .containsExactly(1L, 2L);
    }

    @Test
    void doitRetournerListeVideQuandAucuneNouvelleOffre()
            throws Exception {
        inscrireEtudiantSessionActuelle();

        etudiant.setPreviousLoginAt(
                LocalDateTime.of(2026, 11, 10, 10, 0));

        OffreDeStage offre = creerOffre(1L, "Ancienne offre");
        offre.setApprovedAt(etudiant.getPreviousLoginAt().minusDays(1));

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        CvEtudiant cv = new CvEtudiant();
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.ACCEPTEE);

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        when(offreDeStageRepository.findOffresGeneralesDisponibles(
                eq(Statut.ACCEPTEE),
                eq(etudiant.getDepartment()),
                any(LocalDate.class)))
                .thenReturn(List.of(offre));

        when(offreDeStageRepository.findOffresCibleesPourEtudiant(
                eq(Statut.ACCEPTEE),
                eq(etudiant.getDepartment()),
                any(LocalDate.class),
                eq(etudiant)))
                .thenReturn(List.of());

        List<OffreDeStageDTO> resultat =
                etudiantService.getNouvellesOffresDepuisDerniereVisite();

        assertThat(resultat).isEmpty();
    }

    @Test
    void doitLeverExceptionSiEtudiantIntrouvablePourNouvellesOffres() {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> etudiantService.getNouvellesOffresDepuisDerniereVisite())
                .isInstanceOf(EtudiantIntrouvableException.class);
    }

    private OffreDeStage creerOffre(Long id, String titre) {
        OffreDeStage offre = new OffreDeStage();

        offre.setId(id);
        offre.setTitle(titre);
        offre.setDescription("Description de l'offre");
        offre.setSalary(25.0);
        offre.setDomain(Departement.INFORMATIQUE);
        offre.setStartDate(LocalDate.now().plusMonths(1));
        offre.setEndDate(LocalDate.now().plusMonths(4));
        offre.setDisplayEndDate(LocalDate.now().plusDays(15));
        offre.setStatut(Statut.ACCEPTEE);
        offre.setEmployeur(employeur);
        offre.setCreatedAt(LocalDateTime.now());

        return offre;
    }

}
