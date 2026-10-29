package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.model.Enum.*;
import com.lacouf.rsbjwt.service.dto.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.service.EmployeurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lacouf.rsbjwt.service.dto.CandidatureDTO;

@SpringBootTest
@ActiveProfiles("test")
public class EmployeurControllerTest {
    CreationOffreDeStageDTO creationOffreDeStageDTO;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private EmployeurService employeurService;

    private ObjectMapper objectMapper;
    private CreationEntrevueDTO creationConvocationDTO;
    private EntrevueDTO convocationEntrevueDTO;

    InscriptionEmployeurDTO inscriptionEmployeurDTO;
    OffreDeStageDTO offreDeStageDTO;
    Principal principal = () -> "employeur@test.com";

    private MockMvc mockMvc;

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

        offreDeStageDTO = new OffreDeStageDTO(
                1L,
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 10, 15),
                LocalDate.of(2029, 10, 1).minusDays(15),
                Statut.EN_ATTENTE,
                null,
                null,
                null,
                null,
                null,
                false
        );

        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 10, 15),
                LocalDate.of(2029, 10, 1).minusDays(15)
        );


        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        objectMapper = JsonMapper.builder().build();

        PlageHoraireDTO plageHoraireDTO = new PlageHoraireDTO(
                LocalDateTime.now().plusDays(7),
                30
        );

        creationConvocationDTO = new CreationEntrevueDTO(
                1L,
                FormatEntrevue.EN_LIGNE,
                LocalDateTime.now().plusDays(7),
                30
        );

        convocationEntrevueDTO = new EntrevueDTO(
                1L,
                LocalDateTime.now(),
                FormatEntrevue.EN_LIGNE,
                LocalDateTime.now().plusDays(7),
                30,
                1L,
                1L,
                "Jean",
                "Tremblay",
                "Développeur web"
        );
    }

    @Test
    void doitCreerCompteEmployeur() throws Exception {
      EmployeurDTO employeurDTO = new EmployeurDTO(
              1L,
              "Gerard",
              "Robert",
              "165-685-4569",
              "Gerard.Robert@hotmail.com",
              "Mercier",
              "Gerard inc",
              SecteurActivite.AEROSPATIAL,
              null
        );
        when(employeurService.creeCompteEmployeur(any(InscriptionEmployeurDTO.class))).thenReturn(employeurDTO);

        mockMvc.perform(post("/employeur/inscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inscriptionEmployeurDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void doitRetournerBadRequestMotDePasseNonCorrespondant() throws Exception {
        when(employeurService.creeCompteEmployeur(any(InscriptionEmployeurDTO.class)))
                .thenThrow(new MotDePasseNonCorrespondantException());

        mockMvc.perform(post("/employeur/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEmployeurDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doitRetournerConflictEmailExistant() throws Exception{
        when(employeurService.creeCompteEmployeur(any(InscriptionEmployeurDTO.class)))
                .thenThrow(new EmailExistantException());

        mockMvc.perform(post("/employeur/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEmployeurDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void doitRetournerBadRequestQuandChampObligatoireManquant() throws Exception{
        inscriptionEmployeurDTO = new InscriptionEmployeurDTO(
                "Gerard",
                "Robert",
                "165-685-4569",
                "Gerard.Robert@hotmail.com",
                "",
                "Gerard inc",
                SecteurActivite.AEROSPATIAL,
                "Losange12%",
                "Losange12%"
        );
        mockMvc.perform(post("/employeur/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEmployeurDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doitRetournerBadRequestQuandEmailInvalide() throws Exception{
        inscriptionEmployeurDTO = new InscriptionEmployeurDTO(
                "Gerard",
                "Robert",
                "165-685-4569",
                "HAHA",
                "Mercier",
                "Gerard inc",
                SecteurActivite.AEROSPATIAL,
                "Losange12%",
                "Losange12%"
        );

        mockMvc.perform(post("/employeur/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEmployeurDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doitCreerOffreDeStageAvecFichierPdf() throws Exception, DateFinAffichageApresDateDebutException {
        CreationOffreDeStageDTO creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 10, 15),
                LocalDate.of(2029, 10, 1).minusDays(15)
        );
        MockMultipartFile offre = new MockMultipartFile(
                "offre",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(creationOffreDeStageDTO)
        );
        MockMultipartFile fichier = new MockMultipartFile(
                "file",
                "offre.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "contenu du pdf".getBytes()
        );

        when(employeurService.creerOffre(any(), any(), any())).thenReturn(offreDeStageDTO);

        mockMvc.perform(multipart("/employeur/offres")
                        .file(offre)
                        .file(fichier)
                        .principal(principal))
                .andExpect(status().isCreated());
    }

    @Test
    void doitRetournerBadRequestQuandTitreVide() throws Exception {
        CreationOffreDeStageDTO creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 10, 15),
                LocalDate.of(2029, 10, 1).minusDays(15)
        );
        MockMultipartFile offre = new MockMultipartFile(
                "offre",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(creationOffreDeStageDTO)
        );

        mockMvc.perform(multipart("/employeur/offres")
                        .file(offre)
                        .principal(principal))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doitRetournerUnsupportedMediaTypeQuandMauvaisTypeDeFichier() throws Exception, DateFinAffichageApresDateDebutException {
        CreationOffreDeStageDTO creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 10, 15),
                LocalDate.of(2029, 10, 1).minusDays(15)
        );
        MockMultipartFile offre = new MockMultipartFile(
                "offre",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(creationOffreDeStageDTO)
        );
        MockMultipartFile fichier = new MockMultipartFile(
                "fichier",
                "offre.exe",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "contenu".getBytes()
        );

        when(employeurService.creerOffre(any(), any(), any()))
                .thenThrow(new FichierTypeInvalideException());

        mockMvc.perform(multipart("/employeur/offres")
                        .file(offre)
                        .file(fichier)
                        .principal(principal))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void doitRetournerPayloadTooLargeQuandFichierTropVolumineux() throws Exception, DateFinAffichageApresDateDebutException {
        CreationOffreDeStageDTO creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 10, 15),
                LocalDate.of(2029, 10, 1).minusDays(15)
        );
        MockMultipartFile offre = new MockMultipartFile(
                "offre",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(creationOffreDeStageDTO)
        );
        MockMultipartFile fichier = new MockMultipartFile(
                "fichier",
                "offre.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "contenu du pdf".getBytes()
        );

        when(employeurService.creerOffre(any(), any(), any()))
                .thenThrow(new FichierTropVolumineuxException());

        mockMvc.perform(multipart("/employeur/offres")
                        .file(offre)
                        .file(fichier)
                        .principal(principal))
                .andExpect(status().is(413));
    }

    @Test
    void doitRetournerUnprocessableEntityQuandFichierCorrompu() throws Exception, DateFinAffichageApresDateDebutException {
        CreationOffreDeStageDTO creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 10, 15),
                LocalDate.of(2029, 10, 1).minusDays(15)
        );
        MockMultipartFile offre = new MockMultipartFile(
                "offre",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(creationOffreDeStageDTO)
        );
        MockMultipartFile fichier = new MockMultipartFile(
                "fichier",
                "offre.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                new byte[0]
        );

        when(employeurService.creerOffre(any(), any(), any()))
                .thenThrow(new FichierCorrompuException());

        mockMvc.perform(multipart("/employeur/offres")
                        .file(offre)
                        .file(fichier)
                        .principal(principal))
                .andExpect(status().is(422));
    }

    @Test
    void doitRetournerBadRequestQuandDateFinAvantDateDebut() throws Exception, DateFinAffichageApresDateDebutException {
        CreationOffreDeStageDTO creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 10, 15),
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 10, 1).minusDays(15)
        );
        MockMultipartFile offre = new MockMultipartFile(
                "offre",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(creationOffreDeStageDTO)
        );

        when(employeurService.creerOffre(any(), any(), any()))
                .thenThrow(new DateFinAvantDateDebutException());

        mockMvc.perform(multipart("/employeur/offres")
                        .file(offre)
                        .principal(principal))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doitRetournerLesOffresDeLEmployeur() throws Exception {
        when(employeurService.obtenirOffres()).thenReturn(List.of(offreDeStageDTO));

        mockMvc.perform(get("/employeur/offres"))
                .andExpect(status().isOk());
    }

    @Test
    void doitModifierOffreDeStage() throws Exception, DateFinAffichageApresDateDebutException {
        when(employeurService.modifierOffre(any(), any(), any(), any())).thenReturn(offreDeStageDTO);

        mockMvc.perform(multipart(HttpMethod.PUT, "/employeur/offres/1")
                        .file(new MockMultipartFile("offre", "", MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsBytes(creationOffreDeStageDTO)))
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void doitTelechargerDocumentOffre() throws Exception {
        Resource resource = new ByteArrayResource("contenu du pdf".getBytes());
        Authentication authentication = new UsernamePasswordAuthenticationToken("employeur@test.com", null);

        when(employeurService.telechargerOffre(1L, "employeur@test.com"))
                .thenReturn(resource);

        mockMvc.perform(get("/employeur/offres/1/download")
                        .principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    void doitRetournerNotFoundQuandOffreOuFichierIntrouvablePourTelechargement() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken("employeur@test.com", null);

        when(employeurService.telechargerOffre(1L, "employeur@test.com"))
                .thenThrow(new FichierIntrouvableException());

        mockMvc.perform(get("/employeur/offres/1/download")
                        .principal(authentication))
                .andExpect(status().isNotFound());
    }

    @Test
    void doitCreerConvocationAvecSucces() throws Exception {
        when(employeurService.creerConvocation(
                any(CreationEntrevueDTO.class)
        )).thenReturn(convocationEntrevueDTO);

        mockMvc.perform(post("/employeur/entrevues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                creationConvocationDTO
                        ))
                        .principal(principal))
                .andExpect(status().isCreated());
    }

    @Test
    void doitRetournerBadRequestQuandCandidatureIdManquant() throws Exception {
        String jsonInvalide = """
                {
                    "format": "EN_LIGNE",
                    "dateHeureEntrevue": "2027-11-15T10:00:00"
                }
                """;

        mockMvc.perform(post("/employeur/entrevues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalide)
                        .principal(principal))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doitRetournerForbiddenQuandOffreNonAutorisee() throws Exception {
        when(employeurService.creerConvocation(
                any(CreationEntrevueDTO.class)
        )).thenThrow(new OffreNonAutoriseeException());

        mockMvc.perform(post("/employeur/entrevues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                creationConvocationDTO
                        ))
                        .principal(principal))
                .andExpect(status().isForbidden());
    }

    @Test
    void doitRetournerNotFoundQuandCandidatureIntrouvable() throws Exception {
        when(employeurService.creerConvocation(
                any(CreationEntrevueDTO.class)
        )).thenThrow(new CandidatureIntrouvableException());

        mockMvc.perform(post("/employeur/entrevues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                creationConvocationDTO
                        ))
                        .principal(principal))
                .andExpect(status().isNotFound());
    }

    @Test
    void doitRetournerConflictQuandConvocationExisteDeja() throws Exception {
        when(employeurService.creerConvocation(
                any(CreationEntrevueDTO.class)
        )).thenThrow(new ConvocationExistanteException());

        mockMvc.perform(post("/employeur/entrevues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                creationConvocationDTO
                        ))
                        .principal(principal))
                .andExpect(status().isConflict());
    }


    @Test
    void doitRetournerCandidaturesConvoquees() throws Exception {
        CandidatureDTO candidatureDTO = new CandidatureDTO(
                1L,
                10L,
                "Jean",
                "Tremblay",
                "INFORMATIQUE",
                2L,
                "Développeur web",
                "Entreprise ABC",
                LocalDateTime.now(),
                null,
                StatutCandidature.RETENUE_POUR_ENTREVUE
        );

        when(employeurService.obtenirCandidaturesConvoquees())
                .thenReturn(List.of(candidatureDTO));

        mockMvc.perform(get("/employeur/candidatures/convoquees")
                        .principal(principal))
                .andExpect(status().isOk());
    }


    @Test
    void doitRetournerNotFoundQuandEmployeurIntrouvablePourCandidaturesConvoquees()
            throws Exception {

        when(employeurService.obtenirCandidaturesConvoquees())
                .thenThrow(new UtilisateurIntrouvableException());

        mockMvc.perform(get("/employeur/candidatures/convoquees")
                        .principal(principal))
                .andExpect(status().isNotFound());
    }

}