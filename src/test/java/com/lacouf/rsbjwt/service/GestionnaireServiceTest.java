package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Notification.ControllerPath;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.model.event.CvStatutModifieEvent;
import com.lacouf.rsbjwt.model.event.CvSupprimerSoNotificationGestionnaireEvent;
import com.lacouf.rsbjwt.model.event.NouvelleOffreTeleverseeEvent;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.service.dto.*;
import com.lacouf.rsbjwt.utils.StockageFichierUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.MockedStatic;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.mockStatic;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionnaireServiceTest {

    @Mock
    private OffreDeStageRepository offreDeStageRepository;

    @Mock
    private CvEtudiantRepository cvEtudiantRepository;

    @Mock
    private GestionnaireRepository gestionnaireRepository;

    @Mock
    private UserAppRepository userAppRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmployeurRepository employeurRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private ProfesseurRepository professeurRepository;

    @Mock
    private CandidatureRepository candidatureRepository;

    @Mock
    private ContratRepository contratRepository;


    @InjectMocks
    private GestionnaireService gestionnaireService;
    private GestionnaireDTO gestionnaireDto;
    private Gestionnaire gestionnaire;
    private CvEtudiant cvEtudiant;
    private Etudiant etudiant;
    private String cvCommentError;

    @BeforeEach
    void setUp(){
        gestionnaireDto = new GestionnaireDTO(-6L,"Laurent","losange","Laurent.Losange@hotmail.com","458-458-4584","5858", null);
        gestionnaire = new Gestionnaire(1L,"Laurent","losange","Laurent.Losange@hotmail.com","5858","458-458-4584");
        gestionnaire.setId(1L);
        etudiant = new Etudiant("pascal","losange","pascal.losange@hotmail.com","585-585-5858", Departement.INFORMATIQUE,"5858585","5858");
        etudiant.setId(1L);
        cvEtudiant = new CvEtudiant("jeronimo","application/pdf",18L,"", LocalDateTime.now(),etudiant);
        cvEtudiant.setId(1L);
        cvCommentError = "Cv non conforme";
    }


    private Employeur creerEmployeurTest() {
        Employeur employeur = Employeur.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .phone("5141234567")
                .email("jean.dupont@test.com")
                .town("Montréal")
                .businessName("Entreprise Test")
                .businessSector(SecteurActivite.INFORMATIQUE)
                .password("password")
                .build();

        employeur.setId(1L);

        return employeur;
    }

    @Test
    void doitCreerCompteGestionnaire() throws EmailExistantException, NumeroTelephoneExistantException {
        when(gestionnaireRepository.save(any(Gestionnaire.class))).thenReturn(gestionnaire);

        GestionnaireDTO gestionnaireDtoBD = gestionnaireService.creerCompteGestionnaire(gestionnaireDto);

        assertThat(gestionnaireDtoBD).isNotNull().satisfies(dto -> {
                assertThat(dto.email()).isEqualTo(gestionnaire.getEmail());
                assertThat(dto.firstName()).isEqualTo(gestionnaire.getFirstName());
                assertThat(dto.id()).isEqualTo(gestionnaire.getId());

        });
    }

    @Test
    void doitLeverExceptionSiEmailExisteDeja()  {
        when(userAppRepository.findUserAppByEmail(any(String.class))).thenReturn(Optional.of(gestionnaire));

        assertThatThrownBy(() -> gestionnaireService.creerCompteGestionnaire(gestionnaireDto)).isInstanceOf(EmailExistantException.class);
    }

    @Test
    void doitLeverExceptionSiTelephoneExisteDeja()  {
        when(userAppRepository.findByPhoneNumber(any(String.class))).thenReturn(Optional.of(gestionnaire));

        assertThatThrownBy(() -> gestionnaireService.creerCompteGestionnaire(gestionnaireDto)).isInstanceOf(NumeroTelephoneExistantException.class);
    }

    @Test
    void doitMapperEtRetournerTousLesSecteurs() {
        List<SecteurEmployeurDTO> result =
                gestionnaireService.getAllSecteurs();

        assertThat(result)
                .hasSize(SecteurActivite.values().length)
                .extracting(SecteurEmployeurDTO::label)
                .contains(
                        SecteurActivite.INFORMATIQUE.getLabel(),
                        SecteurActivite.PHARMACEUTIQUE.getLabel(),
                        SecteurActivite.FINANCE.getLabel()
                );
    }

    @Test
    void doitMapperEtRetournerTousLesDepartements() {
        List<DepartementDTO> result =
                gestionnaireService.getAllDepartements();

        assertThat(result)
                .hasSize(Departement.values().length)
                .extracting(DepartementDTO::label)
                .contains(
                        Departement.INFORMATIQUE.getLabel(),
                        Departement.GENIE_PHYSIQUE.getLabel(),
                        Departement.EDUCATION_ENFANCE.getLabel()
                );
    }

    @Test
    void doitRetournerLesOffresEnAttente() {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(Statut.EN_ATTENTE);
        offre.setEmployeur(creerEmployeurTest());

        when(offreDeStageRepository.findByStatut(Statut.EN_ATTENTE))
                .thenReturn(List.of(offre));

        List<OffreDeStageDTO> result =
                gestionnaireService.getOffresEnAttente();

        assertThat(result)
                .hasSize(1)
                .extracting(OffreDeStageDTO::id)
                .contains(1L);

        verify(offreDeStageRepository)
                .findByStatut(Statut.EN_ATTENTE);
    }

    @Test
    void doitApprouverUneOffreEnAttente()
            throws OffreIntrouvableException, OffreNonEnAttenteException {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(Statut.EN_ATTENTE);
        offre.setEmployeur(creerEmployeurTest());

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.of(offre));

        when(offreDeStageRepository.save(offre))
                .thenReturn(offre);

        OffreDeStageDTO result =
                gestionnaireService.approuverOffre(1L);

        assertThat(result).isNotNull();

        assertThat(offre.getStatut())
                .isEqualTo(Statut.ACCEPTEE);

        assertThat(offre.getRejectionComment())
                .isNull();

        verify(offreDeStageRepository)
                .findById(1L);

        verify(offreDeStageRepository)
                .save(offre);
    }

    @Test
    void doitLeverExceptionSiOffreIntrouvable()
            throws OffreNonEnAttenteException {

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                gestionnaireService.approuverOffre(1L)
        )
                .isInstanceOf(OffreIntrouvableException.class);

        verify(offreDeStageRepository, never())
                .save(any());
    }

    @Test
    void doitLeverExceptionSiOffreNonEnAttente()
            throws OffreIntrouvableException {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(Statut.ACCEPTEE);

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.of(offre));

        assertThatThrownBy(() ->
                gestionnaireService.approuverOffre(1L)
        )
                .isInstanceOf(OffreNonEnAttenteException.class);

        verify(offreDeStageRepository, never())
                .save(any());
    }

    @Test
    void doitRefuserUneOffreEnAttente()
            throws OffreIntrouvableException,
            OffreNonEnAttenteException,
            CommentaireRefusObligatoireException {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(Statut.EN_ATTENTE);
        offre.setEmployeur(creerEmployeurTest());

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.of(offre));

        when(offreDeStageRepository.save(offre))
                .thenReturn(offre);

        String commentaire =
                "L'offre ne respecte pas les exigences.";

        OffreDeStageDTO result =
                gestionnaireService.refuserOffre(1L, commentaire);

        assertThat(result).isNotNull();

        assertThat(offre.getStatut())
                .isEqualTo(Statut.REFUSEE);

        assertThat(offre.getRejectionComment())
                .isEqualTo(commentaire);

        verify(offreDeStageRepository)
                .findById(1L);

        verify(offreDeStageRepository)
                .save(offre);
    }

    @Test
    void doitLeverExceptionSiCommentaireNull()
            throws OffreIntrouvableException,
            OffreNonEnAttenteException {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(Statut.EN_ATTENTE);

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.of(offre));

        assertThatThrownBy(() ->
                gestionnaireService.refuserOffre(1L, null)
        )
                .isInstanceOf(CommentaireRefusObligatoireException.class);

        verify(offreDeStageRepository, never())
                .save(any());
    }

    @Test
    void doitLeverExceptionSiCommentaireVide()
            throws OffreIntrouvableException,
            OffreNonEnAttenteException {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(Statut.EN_ATTENTE);

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.of(offre));

        assertThatThrownBy(() ->
                gestionnaireService.refuserOffre(1L, "")
        )
                .isInstanceOf(CommentaireRefusObligatoireException.class);

        verify(offreDeStageRepository, never())
                .save(any());
    }

    @Test
    void doitLeverExceptionSiCommentaireContientSeulementDesEspaces()
            throws OffreIntrouvableException,
            OffreNonEnAttenteException {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(Statut.EN_ATTENTE);

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.of(offre));

        assertThatThrownBy(() ->
                gestionnaireService.refuserOffre(1L, "   ")
        )
                .isInstanceOf(CommentaireRefusObligatoireException.class);

        verify(offreDeStageRepository, never())
                .save(any());
    }

    @Test
    void doitLeverExceptionSiOffreRefuseeNestPasEnAttente()
            throws OffreIntrouvableException,
            CommentaireRefusObligatoireException {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(Statut.ACCEPTEE);

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.of(offre));

        assertThatThrownBy(() ->
                gestionnaireService.refuserOffre(
                        1L,
                        "L'offre ne respecte pas les exigences."
                )
        )
                .isInstanceOf(OffreNonEnAttenteException.class);

        verify(offreDeStageRepository, never())
                .save(any());
    }

    @Test
    void doitRetournerLesCurriculumVitaeEnAttente(){
        List<CvEtudiant> listCv = List.of(cvEtudiant);

        when(cvEtudiantRepository.findByStatut(Statut.EN_ATTENTE)).thenReturn(listCv);

        List<CvEtudiantDTO> cvEtudiantDTOs = gestionnaireService.getCurriculumVitaeEnAttente();

        assertThat(cvEtudiantDTOs)
                .hasSize(1)
                .first()
                .extracting(CvEtudiantDTO::fileName)
                .isEqualTo(cvEtudiant.getFileName());

        verify(cvEtudiantRepository).findByStatut(Statut.EN_ATTENTE);
    }

    @Test
    void doitRetournerLesCurriculumVitae(){
        List<CvEtudiant> listCv = new ArrayList<>();
        listCv.add(cvEtudiant);

        CvEtudiant cvRefuse = CvEtudiant.builder()
                .fileName(cvEtudiant.getFileName())
                .contentType(cvEtudiant.getContentType())
                .fileSize(cvEtudiant.getFileSize())
                .storagePath(cvEtudiant.getStoragePath())
                .uploadDate(cvEtudiant.getUploadDate())
                .etudiant(cvEtudiant.getEtudiant()).build();
        cvRefuse.setId(2L);
        cvRefuse.setStatut(Statut.REFUSEE);
        cvRefuse.setRejectionComment("Why not");
        listCv.add(cvRefuse);

        when(cvEtudiantRepository.findAll()).thenReturn(listCv);

        List<CvEtudiantDTO> cvEtudiantDTOs = gestionnaireService.getTousLesCvs();

        assertThat(cvEtudiantDTOs)
                .hasSize(2)
                .first()
                .extracting(CvEtudiantDTO::fileName, CvEtudiantDTO::statut)
                .containsExactly(cvEtudiant.getFileName(), Statut.EN_ATTENTE);
        assertThat(cvEtudiantDTOs)
                .hasSize(2)
                .last()
                .extracting(CvEtudiantDTO::fileName,CvEtudiantDTO::statut)
                .containsExactly(cvEtudiant.getFileName(),Statut.REFUSEE);

        verify(cvEtudiantRepository).findAll();
    }

    @Test
    void doitApprouverCurriculumVitaeEnAttente() throws CurriculumVitaeNonEnAttente, CurriculumVitaeIntrouvable {
        when(cvEtudiantRepository.findById(1L)).thenReturn(Optional.of(cvEtudiant));
        when(cvEtudiantRepository.save(any(CvEtudiant.class))).thenReturn(cvEtudiant);

        CvEtudiantDTO result = gestionnaireService.approuverCurriculumVitae(1L);

        assertThat(result)
                .isNotNull()
                .satisfies(dto -> {
                    assertThat(dto.fileName()).isEqualTo(cvEtudiant.getFileName());
                    assertThat(dto.statut()).isEqualTo(Statut.ACCEPTEE);
                    assertThat(dto.rejectionComment()).isBlank();
                });

        verify(cvEtudiantRepository).findById(1L);
        verify(cvEtudiantRepository).save(cvEtudiant);
        verify(applicationEventPublisher).publishEvent(new CvStatutModifieEvent(etudiant, Statut.ACCEPTEE));
        verify(applicationEventPublisher).publishEvent(new CvSupprimerSoNotificationGestionnaireEvent(
                ControllerPath.GESTIONNAIRE_CV_ID_DETAIL.build("1")));
    }

    @Test
    void doitLeverExceptionSiCurriculumVitaeNonEnAttenteApprouver(){
        cvEtudiant.setStatut(Statut.REFUSEE);
        when(cvEtudiantRepository.findById(1L)).thenReturn(Optional.of(cvEtudiant));

        assertThatThrownBy(() -> gestionnaireService.approuverCurriculumVitae(1L)).isInstanceOf(CurriculumVitaeNonEnAttente.class);

        verify(cvEtudiantRepository).findById(1L);
        verify(cvEtudiantRepository, never()).save(any(CvEtudiant.class));
    }

    @Test
    void doitLeverExceptionSiCurriculumVitaeIntrouvableApprouver(){
        when(cvEtudiantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gestionnaireService.approuverCurriculumVitae(1L)).isInstanceOf(CurriculumVitaeIntrouvable.class);

        verify(cvEtudiantRepository).findById(1L);
        verify(cvEtudiantRepository, never()).save(any(CvEtudiant.class));
    }

    @Test
    void doitRefuserUnCurriculumVitaeEnAttente() throws CurriculumVitaeNonEnAttente, CurriculumVitaeIntrouvable, CommentaireRefusObligatoireException {
        when(cvEtudiantRepository.findById(1L)).thenReturn(Optional.of(cvEtudiant));
        when(cvEtudiantRepository.save(any(CvEtudiant.class))).thenReturn(cvEtudiant);

        CvEtudiantDTO result = gestionnaireService.refuserCurriculumVitae(1L,cvCommentError);

        assertThat(result)
                .isNotNull()
                .satisfies(dto -> {
                    assertThat(dto.fileName()).isEqualTo(cvEtudiant.getFileName());
                    assertThat(dto.statut()).isEqualTo(Statut.REFUSEE);
                    assertThat(dto.rejectionComment()).isEqualTo(cvCommentError);
                });

        verify(cvEtudiantRepository).findById(1L);
        verify(cvEtudiantRepository).save(cvEtudiant);
        verify(applicationEventPublisher).publishEvent(new CvStatutModifieEvent(etudiant, Statut.REFUSEE));
        verify(applicationEventPublisher).publishEvent(new CvSupprimerSoNotificationGestionnaireEvent(
                ControllerPath.GESTIONNAIRE_CV_ID_DETAIL.build("1")));
    }

    @Test
    void doitRetournerUnCvParID() throws CurriculumVitaeIntrouvable {
        when(cvEtudiantRepository.findById(cvEtudiant.getId())).thenReturn(Optional.of(cvEtudiant));

        CvEtudiantDTO cvEtudiantDTO = gestionnaireService.getCvParId(cvEtudiant.getId());

        assertThat(cvEtudiantDTO.id()).isEqualTo(cvEtudiant.getId());
        assertThat(cvEtudiantDTO.statut()).isEqualTo(cvEtudiant.getStatut());
        assertThat(cvEtudiantDTO.fileName()).isEqualTo(cvEtudiant.getFileName());
        verify(cvEtudiantRepository).findById(cvEtudiantDTO.id());
    }

    @Test
    void doitRetournerCvNotFound() {
        assertThatThrownBy(() -> gestionnaireService.getCvParId(-5L)).isInstanceOf(CurriculumVitaeIntrouvable.class);

        verify(cvEtudiantRepository,never()).findById(cvEtudiant.getId());
    }

    @Test
    void doitLeverExceptionSiCurriculumVitaeNonEnAttenteRefuser(){
        cvEtudiant.setStatut(Statut.REFUSEE);
        when(cvEtudiantRepository.findById(1L)).thenReturn(Optional.of(cvEtudiant));

        assertThatThrownBy(() -> gestionnaireService.refuserCurriculumVitae(1L,cvCommentError)).isInstanceOf(CurriculumVitaeNonEnAttente.class);

        verify(cvEtudiantRepository).findById(1L);
        verify(cvEtudiantRepository, never()).save(any(CvEtudiant.class));
    }

    @Test
    void doitLeverExceptionSiCurriculumVitaeIntrouvableRefuser(){
        when(cvEtudiantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gestionnaireService.refuserCurriculumVitae(1L,cvCommentError)).isInstanceOf(CurriculumVitaeIntrouvable.class);

        verify(cvEtudiantRepository).findById(1L);
        verify(cvEtudiantRepository, never()).save(any(CvEtudiant.class));
    }

    @Test
    void doitLeverExceptionSiCommentaireContientQueDesEspace(){
        cvCommentError = " ";
        when(cvEtudiantRepository.findById(1L)).thenReturn(Optional.of(cvEtudiant));

        assertThatThrownBy(() -> gestionnaireService.refuserCurriculumVitae(1L,cvCommentError)).isInstanceOf(CommentaireRefusObligatoireException.class);

        verify(cvEtudiantRepository).findById(1L);
        verify(cvEtudiantRepository, never()).save(any(CvEtudiant.class));
    }

    @Test
    void doitLeverExceptionSiCommentaireContientNull(){
        cvCommentError = null;
        when(cvEtudiantRepository.findById(1L)).thenReturn(Optional.of(cvEtudiant));

        assertThatThrownBy(() -> gestionnaireService.refuserCurriculumVitae(1L,cvCommentError)).isInstanceOf(CommentaireRefusObligatoireException.class);

        verify(cvEtudiantRepository).findById(1L);
        verify(cvEtudiantRepository, never()).save(any(CvEtudiant.class));
    }

    @Test
    void doitLeverExceptionSiCommentaireContientVide(){
        cvCommentError = "";
        when(cvEtudiantRepository.findById(1L)).thenReturn(Optional.of(cvEtudiant));

        assertThatThrownBy(() -> gestionnaireService.refuserCurriculumVitae(1L,cvCommentError)).isInstanceOf(CommentaireRefusObligatoireException.class);

        verify(cvEtudiantRepository).findById(1L);
        verify(cvEtudiantRepository, never()).save(any(CvEtudiant.class));
    }

    @Test
    void doitRetournePDF() throws Exception {
        when(cvEtudiantRepository.findById(1L)).thenReturn(Optional.of(cvEtudiant));
        Path pathAttendu = Path.of(cvEtudiant.getStoragePath());
        Resource mockResource = new ByteArrayResource(cvEtudiant.getFileName().getBytes());

        try (MockedStatic<StockageFichierUtils> stockageUtilsMock = mockStatic(StockageFichierUtils.class)) {
            stockageUtilsMock.when(() -> StockageFichierUtils.chargerFichier(pathAttendu))
                    .thenReturn(mockResource);

            Resource resource = gestionnaireService.telechargerCvParId(1L);


            assertThat(resource).isNotNull();
            assertThat(resource.getInputStream().readAllBytes())
                    .isEqualTo(mockResource.getContentAsByteArray());
            assertThat(resource.exists()).isTrue();
            assertThat(resource.isReadable()).isTrue();
            assertThat(resource.contentLength())
                    .isPositive()
                    .isEqualTo(mockResource.getContentAsByteArray().length);

        }

    }

    @Test
    void doitRetournerNotFoundQuandCvIntrouvable() {
        when(cvEtudiantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gestionnaireService.telechargerCvParId(99L))
                .isInstanceOf(CurriculumVitaeIntrouvable.class);

    }

    @Test
    void doitRetournerNotFoundQuandFichierPhysiqueIntrouvable() throws Exception {
        when(cvEtudiantRepository.findById(1L)).thenReturn(Optional.of(cvEtudiant));
        Path pathAttendu = Path.of(cvEtudiant.getStoragePath());

        try (MockedStatic<StockageFichierUtils> stockageUtilsMock = mockStatic(StockageFichierUtils.class)) {
            stockageUtilsMock.when(() -> StockageFichierUtils.chargerFichier(pathAttendu))
                    .thenThrow(FichierIntrouvableException.class);

            assertThatThrownBy(() -> gestionnaireService.telechargerCvParId(1L)).isInstanceOf(FichierIntrouvableException.class);
        }

    }

    @Test
    void doitGererFichierVide() throws Exception {
        when(cvEtudiantRepository.findById(1L)).thenReturn(Optional.of(cvEtudiant));
        Path pathAttendu = Path.of(cvEtudiant.getStoragePath());
        Resource pdfVide = new ByteArrayResource(new byte[0]);

        try (MockedStatic<StockageFichierUtils> stockageUtilsMock = mockStatic(StockageFichierUtils.class)) {
            stockageUtilsMock.when(() -> StockageFichierUtils.chargerFichier(pathAttendu))
                    .thenReturn(pdfVide);

            Resource resource = gestionnaireService.telechargerCvParId(1L);


            assertThat(resource).isNotNull();
            assertThat(resource.getInputStream().readAllBytes())
                    .isEqualTo(pdfVide.getContentAsByteArray());
            assertThat(resource.exists()).isTrue();
            assertThat(resource.isReadable()).isTrue();
            assertThat(resource.contentLength())
                    .isEqualTo(pdfVide.getContentAsByteArray().length);

        }
    }

    @Test
    void doitRetournerToutesLesOffres() {
        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setTitle("Développeur Java");
        offre.setStatut(Statut.EN_ATTENTE);
        offre.setEmployeur(creerEmployeurTest());

        when(offreDeStageRepository.findAll())
                .thenReturn(List.of(offre));

        List<OffreDeStageDTO> result = gestionnaireService.getToutesLesOffres();

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(OffreDeStageDTO::id, OffreDeStageDTO::title)
                .containsExactly(1L, "Développeur Java");

        verify(offreDeStageRepository).findAll();
    }

    @Test
    void doitCreerOffreCiblee() throws Exception, DateFinAffichageApresDateDebutException {
        CreationOffreCibleeDTO dto = new CreationOffreCibleeDTO(
                "Stage Cible Java",
                "Description du stage ciblé",
                30.0,
                Departement.INFORMATIQUE,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(30),
                LocalDate.now().plusDays(1),
                1L,
                1L
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "offre.pdf",
                "application/pdf",
                "contenu pdf".getBytes()
        );

        Employeur employeur = creerEmployeurTest();
        employeur.setId(1L);

        Etudiant etudiant = new Etudiant("Pascal", "Losange", "pascal@test.com", "585-585-5858", Departement.INFORMATIQUE, "5858585", "5858");
        etudiant.setId(1L);

        when(employeurRepository.findById(1L)).thenReturn(Optional.of(employeur));
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));

        when(offreDeStageRepository.save(any(OffreCiblee.class)))
                .thenAnswer(invocation -> {
                    OffreCiblee offre = invocation.getArgument(0);
                    offre.setId(10L);
                    return offre;
                });

        try (MockedStatic<StockageFichierUtils> mockedUtils = mockStatic(StockageFichierUtils.class)) {
            mockedUtils.when(() -> StockageFichierUtils.sauvegarderPdf(any(MultipartFile.class), any(Path.class)))
                    .thenReturn(Paths.get("uploads/offres/offre.pdf"));

            OffreDeStageDTO result = gestionnaireService.creerOffreCiblee(dto, file);

            assertThat(result).isNotNull();
            assertThat(result.title()).isEqualTo("Stage Cible Java");
            assertThat(result.statut()).isEqualTo(Statut.EN_ATTENTE);

            verify(employeurRepository).findById(1L);
            verify(etudiantRepository).findById(1L);
            verify(offreDeStageRepository).save(any(OffreCiblee.class));
            verify(applicationEventPublisher).publishEvent(any(NouvelleOffreTeleverseeEvent.class));
        }
    }

    @Test
    void doitRetournerTousLesProfesseurs() {
        Professeur professeur1 = Professeur.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .email("jean@test.com")
                .password("password")
                .matricule("P001")
                .phoneNumber("5141111111")
                .department(Departement.INFORMATIQUE)
                .build();
        professeur1.setId(1L);

        Professeur professeur2 = Professeur.builder()
                .firstName("Marie")
                .lastName("Tremblay")
                .email("marie@test.com")
                .password("password")
                .matricule("P002")
                .phoneNumber("5142222222")
                .department(Departement.GENIE_PHYSIQUE)
                .build();
        professeur2.setId(2L);

        when(professeurRepository.findAll())
                .thenReturn(List.of(professeur1, professeur2));

        List<ProfesseurDTO> result = gestionnaireService.getAllProfesseurs();

        assertThat(result)
                .hasSize(2)
                .extracting(ProfesseurDTO::id)
                .containsExactly(1L, 2L);

        assertThat(result)
                .extracting(ProfesseurDTO::firstName)
                .containsExactly("Jean", "Marie");

        verify(professeurRepository).findAll();
    }

    @Test
    void doitAssignerUnProfesseurAPlusieursEtudiants()
            throws UtilisateurIntrouvableException {

        Professeur professeur = Professeur.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .email("jean@test.com")
                .password("password")
                .matricule("P001")
                .phoneNumber("5141111111")
                .department(Departement.INFORMATIQUE)
                .build();
        professeur.setId(5L);

        Etudiant etudiant2 = new Etudiant(
                "Marie", "Tremblay", "marie@test.com",
                "5142222222", Departement.INFORMATIQUE, "E002", "password"
        );
        etudiant2.setId(2L);

        when(professeurRepository.findById(5L))
                .thenReturn(Optional.of(professeur));

        when(etudiantRepository.findAllById(List.of(1L, 2L)))
                .thenReturn(List.of(etudiant, etudiant2));

        when(etudiantRepository.saveAll(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AssignationProfesseurDTO dto =
                new AssignationProfesseurDTO(List.of(1L, 2L));

        List<EtudiantDTO> result =
                gestionnaireService.assignerProfesseur(5L, dto);

        assertThat(result).hasSize(2);

        assertThat(etudiant.getProfesseur()).isEqualTo(professeur);
        assertThat(etudiant2.getProfesseur()).isEqualTo(professeur);

        verify(professeurRepository).findById(5L);
        verify(etudiantRepository).findAllById(List.of(1L, 2L));
        verify(etudiantRepository).saveAll(any());
    }

    @Test
    void doitRemplacerAncienProfesseurLorsDeReassignation()
            throws UtilisateurIntrouvableException {

        Professeur ancienProfesseur = Professeur.builder()
                .firstName("Paul")
                .lastName("Martin")
                .email("paul@test.com")
                .password("password")
                .matricule("P001")
                .phoneNumber("5141111111")
                .department(Departement.INFORMATIQUE)
                .build();

        Professeur nouveauProfesseur = Professeur.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .email("jean@test.com")
                .password("password")
                .matricule("P002")
                .phoneNumber("5142222222")
                .department(Departement.INFORMATIQUE)
                .build();

        etudiant.setProfesseur(ancienProfesseur);

        when(professeurRepository.findById(5L))
                .thenReturn(Optional.of(nouveauProfesseur));

        when(etudiantRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(etudiant));

        when(etudiantRepository.saveAll(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AssignationProfesseurDTO dto =
                new AssignationProfesseurDTO(List.of(1L));

        gestionnaireService.assignerProfesseur(5L, dto);

        assertThat(etudiant.getProfesseur())
                .isEqualTo(nouveauProfesseur);

        verify(etudiantRepository).saveAll(any());
    }

    @Test
    void doitLeverExceptionSiProfesseurIntrouvable() {
        when(professeurRepository.findById(99L))
                .thenReturn(Optional.empty());

        AssignationProfesseurDTO dto =
                new AssignationProfesseurDTO(List.of(1L, 2L));

        assertThatThrownBy(() ->
                gestionnaireService.assignerProfesseur(99L, dto)
        ).isInstanceOf(UtilisateurIntrouvableException.class);

        verify(professeurRepository).findById(99L);

        verify(etudiantRepository, never())
                .findAllById(any());

        verify(etudiantRepository, never())
                .saveAll(any());
    }

    @Test
    void doitLeverExceptionSiUnEtudiantEstIntrouvable() {
        Professeur professeur = Professeur.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .email("jean@test.com")
                .password("password")
                .matricule("P001")
                .phoneNumber("5141111111")
                .department(Departement.INFORMATIQUE)
                .build();

        professeur.setId(5L);

        when(professeurRepository.findById(5L))
                .thenReturn(Optional.of(professeur));

        when(etudiantRepository.findAllById(List.of(1L, 2L)))
                .thenReturn(List.of(etudiant));

        AssignationProfesseurDTO dto =
                new AssignationProfesseurDTO(List.of(1L, 2L));

        assertThatThrownBy(() ->
                gestionnaireService.assignerProfesseur(5L, dto)
        ).isInstanceOf(UtilisateurIntrouvableException.class);

        verify(professeurRepository).findById(5L);

        verify(etudiantRepository)
                .findAllById(List.of(1L, 2L));

        verify(etudiantRepository, never())
                .saveAll(any());
    }

    @Test
    void doitCreerContratAvecSucces() throws Exception {
        Long candidatureId = 1L;
        String emailGestionnaire = "Laurent.Losange@hotmail.com";

        Candidature candidature = new Candidature();
        candidature.setId(candidatureId);
        candidature.setStatut(StatutCandidature.STAGE_TROUVE);

        Contrat contratSauvegarde = Contrat.builder()
                .candidature(candidature)
                .gestionnaire(gestionnaire)
                .build();

        contratSauvegarde.setId(10L);


        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(emailGestionnaire);

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(userAppRepository.findUserAppByEmail(emailGestionnaire))
                    .thenReturn(Optional.of(gestionnaire));
            when(candidatureRepository.findById(candidatureId))
                    .thenReturn(Optional.of(candidature));
            when(contratRepository.save(any(Contrat.class)))
                    .thenReturn(contratSauvegarde);

            ContratDTO result = gestionnaireService.creerContrat(candidatureId);

            assertThat(result).isNotNull();

            verify(userAppRepository).findUserAppByEmail(emailGestionnaire);
            verify(candidatureRepository).findById(candidatureId);
            verify(contratRepository).save(any(Contrat.class));
        }
    }

    @Test
    void doitLeverExceptionSiUtilisateurIntrouvablePourCreationContrat() {

        Long candidatureId = 1L;
        String emailGestionnaire = "inconnu@hotmail.com";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(emailGestionnaire);

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(userAppRepository.findUserAppByEmail(emailGestionnaire))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> gestionnaireService.creerContrat(candidatureId))
                    .isInstanceOf(UtilisateurIntrouvableException.class);

            verify(userAppRepository).findUserAppByEmail(emailGestionnaire);
            verify(candidatureRepository, never()).findById(any());
            verify(contratRepository, never()).save(any());
        }
    }

    @Test
    void doitLeverExceptionSiCandidatureIntrouvablePourCreationContrat() {
        Long candidatureId = 99L;
        String emailGestionnaire = "Laurent.Losange@hotmail.com";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(emailGestionnaire);

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(userAppRepository.findUserAppByEmail(emailGestionnaire))
                    .thenReturn(Optional.of(gestionnaire));
            when(candidatureRepository.findById(candidatureId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> gestionnaireService.creerContrat(candidatureId))
                    .isInstanceOf(CandidatureIntrouvableException.class);

            verify(candidatureRepository).findById(candidatureId);
            verify(contratRepository, never()).save(any());
        }
    }

    @Test
    void doitLeverExceptionSiStatutCandidatureInvalidePourCreationContrat() {
        Long candidatureId = 1L;
        String emailGestionnaire = "Laurent.Losange@hotmail.com";

        Candidature candidature = new Candidature();
        candidature.setId(candidatureId);

        candidature.setStatut(StatutCandidature.RETENUE_POUR_ENTREVUE);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(emailGestionnaire);

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(userAppRepository.findUserAppByEmail(emailGestionnaire))
                    .thenReturn(Optional.of(gestionnaire));
            when(candidatureRepository.findById(candidatureId))
                    .thenReturn(Optional.of(candidature));

            assertThatThrownBy(() -> gestionnaireService.creerContrat(candidatureId))
                    .isInstanceOf(StatutCandidatureInvalideException.class);

            verify(candidatureRepository).findById(candidatureId);
            verify(contratRepository, never()).save(any());
        }
    }

}