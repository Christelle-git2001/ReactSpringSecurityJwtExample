package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.model.Enum.Departement;
import org.springframework.http.HttpMethod;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.dto.CreationOffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEmployeurDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class EmployeurControllerTest {
    CreationOffreDeStageDTO creationOffreDeStageDTO;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private EmployeurService employeurService;

    private ObjectMapper objectMapper;

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
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 1).minusDays(15),
                Statut.EN_ATTENTE,
                null,
                null,
                null
        );

        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 1).minusDays(15)
        );


        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        objectMapper = JsonMapper.builder().build();
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
              SecteurActivite.AEROSPATIAL
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

    // Test OffreDeStage

    @Test
    void doitCreerOffreDeStageAvecFichierPdf() throws Exception, DateFinAffichageApresDateDebutException {
        CreationOffreDeStageDTO creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 1).minusDays(15)
        );
        MockMultipartFile offre = new MockMultipartFile(
                "offre",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(creationOffreDeStageDTO)
        );
        // Le nom "fichier" doit être identique à @RequestPart("fichier") dans le contrôleur
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
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 1).minusDays(15)
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
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 1).minusDays(15)
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
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 1).minusDays(15)
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
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 1).minusDays(15)
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
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 1).minusDays(15)
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
        when(employeurService.obtenirOffres(any())).thenReturn(List.of(offreDeStageDTO));

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

}
