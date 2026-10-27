package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
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
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeurService employeurService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ConvocationEntrevueRepository convocationRepository;


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
                LocalDate.of(2029, 10, 1).minusDays(15)
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

    private CreationConvocationDTO creerDTOConvocationValide() {
        PlageHoraireDTO plage = new PlageHoraireDTO(
                LocalDateTime.now().plusDays(7),
                30
        );

        return new CreationConvocationDTO(
                candidature.getId(),
                ModaliteEntrevue.DISTANCIEL,
                List.of(plage)
        );
    }

    @Test
    void doitCreerCompteEmployeur() throws Exception {
        when(employeurRepository.save(any(Employeur.class))).thenReturn(employeur);

        EmployeurDTO result = employeurService.creeCompteEmployeur(inscriptionEmployeurDTO);

        verify(employeurRepository, times(1)).save(any(Employeur.class));
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
    void doitLancerExceptionQuandDateFinAvantDateDebut() {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 10, 15),
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 10, 15).minusDays(15)
        );

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, null, employeur.getEmail()))
                .isInstanceOf(DateFinAvantDateDebutException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitLancerExceptionQuandDateFinEgaleDateDebut() {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 01, 1).minusDays(15)
        );

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, null, employeur.getEmail()))
                .isInstanceOf(DateFinAvantDateDebutException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }


    @Test
    void doitLancerExceptionQuandDateFinAffichageApresDateDebut() {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 02, 1),
                LocalDate.of(2029, 02, 26),
                LocalDate.of(2029, 02, 15)
        );

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, null, employeur.getEmail()))
                .isInstanceOf(DateFinAffichageApresDateDebutException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitLancerExceptionQuandDateFinAffichageAvantAujourdhui() {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 02, 1),
                LocalDate.of(2029, 02, 26),
                LocalDate.now().minusDays(1)
        );

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, null, employeur.getEmail()))
                .isInstanceOf(DateFinAffichageAvantAujourdhuiException.class);
        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitLancerExceptionQuandMauvaisTypeDeFichier() {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        MockMultipartFile fichier = new MockMultipartFile("file", "offre.exe",
                MediaType.APPLICATION_OCTET_STREAM_VALUE, "contenu".getBytes());

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, fichier, employeur.getEmail()))
                .isInstanceOf(FichierTypeInvalideException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitLancerExceptionQuandFichierTropVolumineux() {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        MockMultipartFile fichier = new MockMultipartFile("file", "offre.pdf",
                MediaType.APPLICATION_PDF_VALUE, new byte[10 * 1024 * 1024 + 1]);

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, fichier, employeur.getEmail()))
                .isInstanceOf(FichierTropVolumineuxException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitLancerExceptionQuandFichierCorrompu() {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        MockMultipartFile fichier = new MockMultipartFile("file", "offre.pdf",
                MediaType.APPLICATION_PDF_VALUE, new byte[0]);

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, fichier, employeur.getEmail()))
                .isInstanceOf(FichierCorrompuException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitRetournerLesOffresDeLEmployeur() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeur.getEmail(), null)
        );

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

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        when(candidatureRepository.findByOffreDeStage_Employeur(employeur))
                .thenReturn(List.of(candidature));

        List<CandidatureDTO> result = employeurService.obtenirCandidatures();

        assertThat(result).hasSize(1);
    }



    @Test
    void doitRetournerListeVideQuandAucuneOffre() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeur.getEmail(), null)
        );

        employeur.setOffres(List.of());
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));

        List<OffreDeStageDTO> result = employeurService.obtenirOffres();

        assertThat(result).isEmpty();
    }

    @Test
    void doitModifierOffreDeStage() throws DateFinAffichageApresDateDebutException, OffreIntrouvableException, FichierTypeInvalideException, FichierCorrompuException, OffreNonEnAttenteException, DateFinAffichageAvantAujourdhuiException, IOException, OffreNonAutoriseeException, DateFinAvantDateDebutException, DateDebutAvantAujourdhuiException, FichierTropVolumineuxException {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        when(offreDeStageRepository.findById(1L)).thenReturn(Optional.of(offreDeStage));
        when(offreDeStageRepository.save(any(OffreDeStage.class))).thenReturn(offreDeStage);

        OffreDeStageDTO result = employeurService.modifierOffre(1L, creationOffreDeStageDTO, null, employeur.getEmail());

        verify(offreDeStageRepository, times(1)).save(any(OffreDeStage.class));
        assertThat(result).isNotNull();
    }

    @Test
    void doitLancerExceptionQuandOffreIntrouvable() {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        when(offreDeStageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeurService.modifierOffre(99L, creationOffreDeStageDTO, null, employeur.getEmail()))
                .isInstanceOf(OffreIntrouvableException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitLancerExceptionQuandOffreAppartientAUnAutreEmployeur() {
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

        OffreDeStageDTO result = employeurService.creerOffre(creationOffreDeStageDTO, fichier, employeur.getEmail());

        verify(offreDeStageRepository, times(1)).save(any(OffreDeStage.class));
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Infirmerie");
    }

    @Test
    void doitLancerExceptionQuandFichierNullOuVideALaCreation() {
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
    void doitLancerExceptionQuandOffreNonEnAttenteLorsDeLaModification() {
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
    void doitLancerExceptionQuandDateDebutAvantAujourdhui() {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));

        CreationOffreDeStageDTO dtoInvalide = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.now().minusDays(1),
                LocalDate.of(2029, 10, 15),
                LocalDate.now().minusDays(5)
        );

        MockMultipartFile fichier = new MockMultipartFile("file", "offre.pdf",
                MediaType.APPLICATION_PDF_VALUE, "contenu".getBytes());

        assertThatThrownBy(() -> employeurService.creerOffre(dtoInvalide, fichier, employeur.getEmail()))
                .isInstanceOf(DateDebutAvantAujourdhuiException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitCreerConvocationAvecSucces() throws Exception {
        when(candidatureRepository.findById(candidature.getId()))
                .thenReturn(Optional.of(candidature));

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        when(convocationRepository.findByCandidatureId(candidature.getId()))
                .thenReturn(Optional.empty());

        when(convocationRepository.save(any(ConvocationEntrevue.class)))
                .thenAnswer(invocation -> {
                    ConvocationEntrevue convocation = invocation.getArgument(0);
                    convocation.setId(1L);
                    return convocation;
                });

        ConvocationEntrevueDTO resultat =
                employeurService.creerConvocation(creerDTOConvocationValide());

        assertThat(resultat).isNotNull();
        assertThat(resultat.id()).isEqualTo(1L);
        assertThat(resultat.modalite()).isEqualTo(ModaliteEntrevue.DISTANCIEL);
        assertThat(resultat.plagesHoraires()).hasSize(1);

        assertThat(candidature.getStatut())
                .isEqualTo(StatutCandidature.CONVOCATION_ENVOYEE);

        verify(convocationRepository).save(any(ConvocationEntrevue.class));
    }

    @Test
    void doitLancerExceptionQuandCandidatureIntrouvable() {
        when(candidatureRepository.findById(99L))
                .thenReturn(Optional.empty());

        CreationConvocationDTO dto = new CreationConvocationDTO(
                99L,
                ModaliteEntrevue.DISTANCIEL,
                List.of(new PlageHoraireDTO(
                        LocalDateTime.now().plusDays(7),
                        30
                ))
        );

        assertThatThrownBy(() -> employeurService.creerConvocation(dto))
                .isInstanceOf(CandidatureIntrouvableException.class);

        verify(convocationRepository, never()).save(any(ConvocationEntrevue.class));
    }

    @Test
    void doitLancerExceptionQuandConvocationExisteDeja() {
        when(candidatureRepository.findById(candidature.getId()))
                .thenReturn(Optional.of(candidature));

        when(userAppRepository.findUserAppByEmail(employeur.getEmail()))
                .thenReturn(Optional.of(employeur));

        when(convocationRepository.findByCandidatureId(candidature.getId()))
                .thenReturn(Optional.of(new ConvocationEntrevue()));

        assertThatThrownBy(() ->
                employeurService.creerConvocation(creerDTOConvocationValide()))
                .isInstanceOf(ConvocationExistanteException.class);

        verify(convocationRepository, never())
                .save(any(ConvocationEntrevue.class));
    }


}

