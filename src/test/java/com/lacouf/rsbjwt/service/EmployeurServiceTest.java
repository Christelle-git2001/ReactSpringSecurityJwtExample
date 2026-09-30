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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class EmployeurServiceTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
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

    private ObjectMapper objectMapper;
    private JsonMapper jsonMapper;
    private MockMvc mockMvc;

    InscriptionEmployeurDTO inscriptionEmployeurDTO;
    CreationOffreDeStageDTO creationOffreDeStageDTO;
    OffreDeStageDTO offreDeStageDTO;
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

        offreDeStageDTO = new OffreDeStageDTO(
                1L,
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15),
                StatutOffre.EN_ATTENTE,
                null
        );

        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                "donner à manger",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15)
        );

        offreDeStage = OffreDeStage.builder()
                .title("Infirmerie")
                .description("donner à manger")
                .salary(19.25)
                .statut(StatutOffre.EN_ATTENTE)
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 10, 15))
                .build();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        objectMapper = new ObjectMapper();
        jsonMapper = JsonMapper.builder().build();
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
    void doitCreerOffreDeStageSansFichier() throws Exception {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        when(offreDeStageRepository.save(any(OffreDeStage.class))).thenReturn(offreDeStage);

        OffreDeStageDTO result = employeurService.creerOffre(creationOffreDeStageDTO, null, employeur.getEmail());

        verify(offreDeStageRepository, times(1)).save(any(OffreDeStage.class));
        assertThat(result).isNotNull();
    }

    @Test
    void doitLancerExceptionQuandDateFinAvantDateDebut() {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                "donner à manger",
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
                "donner à manger",
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
        MockMultipartFile fichier = new MockMultipartFile("fichier", "offre.exe",
                MediaType.APPLICATION_OCTET_STREAM_VALUE, "contenu".getBytes());

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, fichier, employeur.getEmail()))
                .isInstanceOf(FichierTypeInvalideException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitLancerExceptionQuandFichierTropVolumineux() {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        // 5 Mo + 1 octet : juste au-dessus de TAILLE_MAX
        MockMultipartFile fichier = new MockMultipartFile("fichier", "offre.pdf",
                MediaType.APPLICATION_PDF_VALUE, new byte[5 * 1024 * 1024 + 1]);

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, fichier, employeur.getEmail()))
                .isInstanceOf(FichierTropVolumineuxException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

    @Test
    void doitLancerExceptionQuandFichierCorrompu() {
        when(userAppRepository.findUserAppByEmail(employeur.getEmail())).thenReturn(Optional.of(employeur));
        MockMultipartFile fichier = new MockMultipartFile("fichier", "offre.pdf",
                MediaType.APPLICATION_PDF_VALUE, new byte[0]);

        assertThatThrownBy(() -> employeurService.creerOffre(creationOffreDeStageDTO, fichier, employeur.getEmail()))
                .isInstanceOf(FichierCorrompuException.class);

        verify(offreDeStageRepository, never()).save(any(OffreDeStage.class));
    }

}
