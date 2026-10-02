package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Enum.StatutOffre;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.CreationOffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEmployeurDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
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
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeurService employeurService;


    InscriptionEmployeurDTO inscriptionEmployeurDTO;
    CreationOffreDeStageDTO creationOffreDeStageDTO;
    Employeur employeur;
    OffreDeStage offreDeStage;



    @BeforeEach
    void init(){
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

        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15)
        );

        offreDeStage = OffreDeStage.builder()
                .title("Infirmerie")
                .description("donner à manger")
                .salary(19.25)
                .domain(Departement.INFORMATIQUE)
                .statut(StatutOffre.EN_ATTENTE)
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 10, 15))
                .employeur(employeur)
                .build();
        ReflectionTestUtils.setField(offreDeStage, "id", 1L);
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

    // Offre de Stage

    @Test
    void doitLancerExceptionQuandDateFinAvantDateDebut() {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 1)
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
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 1)
        );

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, null, employeur.getEmail()))
                .isInstanceOf(DateFinAvantDateDebutException.class);

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
        employeur.setOffres(List.of(offreDeStage));
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));

        List<OffreDeStageDTO> result = employeurService.obtenirOffres(employeur.getEmail());

        assertThat(result).hasSize(1);
    }

    @Test
    void doitRetournerListeVideQuandAucuneOffre() {
        employeur.setOffres(List.of());
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));

        List<OffreDeStageDTO> result = employeurService.obtenirOffres(employeur.getEmail());

        assertThat(result).isEmpty();
    }

    @Test
    void doitModifierOffreDeStage() throws Exception {
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

        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        when(offreDeStageRepository.findById(1L)).thenReturn(Optional.of(offreDeStage));

        assertThatThrownBy(() -> employeurService.modifierOffre(1L, creationOffreDeStageDTO, null, employeur.getEmail()))
                .isInstanceOf(RuntimeException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

}
