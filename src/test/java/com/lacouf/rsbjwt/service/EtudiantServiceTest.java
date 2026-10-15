package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.dto.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.dto.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    InscriptionEtudiantDTO inscriptionEtudiantDTO;
    Etudiant etudiant;
    Employeur employeur;

    @BeforeEach
    void init() {
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
    }

    @AfterEach
    void nettoyerFichiersCvDuDossierUploads() throws Exception {
        Path dossier = Paths.get("uploads", "cvs");
        if (Files.exists(dossier)) {
            try (var fichiers = Files.list(dossier)) {
                fichiers.forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (Exception ignored) {
                    }
                });
            }
        }
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
        Path ancienFichier = Paths.get("uploads", "cvs", "ancien.pdf");
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
                .storagePath("uploads/cvs/cv.pdf")
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
        Path fichier = Paths.get("uploads", "cvs", "cv.pdf");
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

        when(offreDeStageRepository.findAllByStatutAndDomainAndDisplayEndDateGreaterThanEqual(
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

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        CvEtudiant cv = new CvEtudiant();
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.ACCEPTEE);

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        when(offreDeStageRepository.findAllByStatutAndDomainAndDisplayEndDateGreaterThanEqual(
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
                .findAllByStatutAndDomainAndDisplayEndDateGreaterThanEqual(
                        eq(Statut.ACCEPTEE),
                        eq(etudiant.getDepartment()),
                        any(LocalDate.class));
    }

    @Test
    void doitRefuserAccesAuxOffresSiCvNonApprouve() throws Exception {

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
                .findAllByStatutAndDomainAndDisplayEndDateGreaterThanEqual(
                        any(Statut.class),
                        any(Departement.class),
                        any(LocalDate.class));
    }

    @Test
    void doitTelechargerFichierCv() throws Exception {
        Path fichier = Paths.get("uploads", "cvs", "cv.pdf");
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

        Candidature candidature = new Candidature(etudiant, offre);
        candidature.setId(1L);

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));

        when(candidatureRepository.findByEtudiant(etudiant))
                .thenReturn(List.of(candidature));

        List<CandidatureDTO> result =
                etudiantService.getCandidaturesEtudiant(etudiant.getEmail());

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
                etudiantService.getCandidaturesEtudiant(etudiant.getEmail());

        assertThat(result).isEmpty();
    }

    @Test
    void doitLancerExceptionQuandEtudiantIntrouvablePourCandidatures() {
        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                etudiantService.getCandidaturesEtudiant(etudiant.getEmail()))
                .isInstanceOf(EtudiantIntrouvableException.class);

        verify(candidatureRepository, never()).findByEtudiant(any(Etudiant.class));
    }

    @Test
    void doitPostulerAUneOffre() throws Exception {
        OffreDeStage offre = new OffreDeStage();
        offre.setId(10L);
        offre.setStatut(Statut.ACCEPTEE);
        offre.setDisplayEndDate(LocalDate.now().plusDays(10));

        when(etudiantRepository.findByCredentialsEmail(etudiant.getEmail()))
                .thenReturn(Optional.of(etudiant));
        CvEtudiant cv = new CvEtudiant();
        cv.setEtudiant(etudiant);
        cv.setStatut(Statut.ACCEPTEE);

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
    }

}