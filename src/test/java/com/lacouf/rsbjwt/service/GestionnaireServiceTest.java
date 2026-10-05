package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.repository.CvEtudiantRepository;
import com.lacouf.rsbjwt.repository.GestionnaireRepository;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
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

    @InjectMocks
    private GestionnaireService gestionnaireService;
    private GestionnaireDto gestionnaireDto;
    private Gestionnaire gestionnaire;
    private CvEtudiant cvEtudiant;
    private Etudiant etudiant;
    private String cvCommentError;

    @BeforeEach
    void setUp(){
        gestionnaireDto = new GestionnaireDto(-6L,"Laurent","losange","Laurent.Losange@hotmail.com","458-458-4584","5858");
        gestionnaire = new Gestionnaire(1L,"Laurent","losange","Laurent.Losange@hotmail.com","5858","458-458-4584");
        gestionnaire.setId(1L);
        etudiant = new Etudiant("pascal","losange","pascal.losange@hotmail.com","585-585-5858", Departement.INFORMATIQUE,"5858585","5858");
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

        GestionnaireDto gestionnaireDtoBD = gestionnaireService.creerCompteGestionnaire(gestionnaireDto);

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







}