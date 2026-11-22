package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.candidature.CandidatureIntrouvableException;
import com.lacouf.rsbjwt.exception.dates.DateAvantAujourdhuiException;
import com.lacouf.rsbjwt.exception.dates.DateFinAffichageApresDateDebutException;
import com.lacouf.rsbjwt.exception.dates.DateFinAffichageAvantAujourdhuiException;
import com.lacouf.rsbjwt.exception.dates.DateFinAvantDateDebutException;
import com.lacouf.rsbjwt.exception.contrat.ContratIntrouvableException;
import com.lacouf.rsbjwt.exception.entrevue.ConvocationExistanteException;
import com.lacouf.rsbjwt.exception.entrevue.ConvocationIntrouvableException;
import com.lacouf.rsbjwt.exception.entrevue.DecisionCandidatureInvalideException;
import com.lacouf.rsbjwt.exception.entrevue.MotifRefusInvalideException;
import com.lacouf.rsbjwt.exception.fichier.FichierCorrompuException;
import com.lacouf.rsbjwt.exception.fichier.FichierIntrouvableException;
import com.lacouf.rsbjwt.exception.fichier.FichierTropVolumineuxException;
import com.lacouf.rsbjwt.exception.fichier.FichierTypeInvalideException;
import com.lacouf.rsbjwt.exception.offre.OffreNonAutoriseeException;
import com.lacouf.rsbjwt.exception.utilisateur.EmailExistantException;
import com.lacouf.rsbjwt.exception.utilisateur.MotDePasseInvalideException;
import com.lacouf.rsbjwt.exception.utilisateur.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.exception.utilisateur.UtilisateurIntrouvableException;
import com.lacouf.rsbjwt.service.dto.candidature.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.candidature.RefusCandidatureDTO;
import com.lacouf.rsbjwt.service.dto.contrat.ContratDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.CreationEntrevueDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.EntrevueDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.PlageHoraireDTO;
import com.lacouf.rsbjwt.service.dto.offre.CreationOffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.offre.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.InscriptionEmployeurDTO;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.ArgumentMatchers.anyLong;

import static org.mockito.Mockito.never;

import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.lacouf.rsbjwt.model.Enum.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
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
                false, null);

        creationOffreDeStageDTO = new CreationOffreDeStageDTO(
                "Infirmerie",
                "préposé",
                19.25,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 10, 1),
                LocalDate.of(2029, 10, 15),
                LocalDate.of(2029, 10, 1).minusDays(15));


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
                "Développeur web",
                null, StatutEntrevue.A_CONFIRMER, null, null, null, null
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
                LocalDate.of(2029, 10, 1).minusDays(15));
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
                LocalDate.of(2029, 10, 1).minusDays(15));
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
                LocalDate.of(2029, 10, 1).minusDays(15));
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
                LocalDate.of(2029, 10, 1).minusDays(15));
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
                LocalDate.of(2029, 10, 1).minusDays(15));
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
                LocalDate.of(2029, 10, 1).minusDays(15));
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
                StatutCandidature.RETENUE_POUR_ENTREVUE,
                null,
                StatutEtudiant.EN_RECHERCHE,
                false, false
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

    @Test
    void doitCreerUneOffreSansChampDeMaximumCandidats() throws Exception, DateFinAffichageApresDateDebutException {
        String json = """
                {"title":"Stage","description":"Description","salary":20,"domain":"INFORMATIQUE",
                "startDate":"2027-01-20","endDate":"2027-05-20","displayEndDate":"2027-01-10"}
                """;
        var offre = new MockMultipartFile("offre", "", "application/json", json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        when(employeurService.creerOffre(any(), any(), anyString())).thenReturn(offreDeStageDTO);

        mockMvc.perform(multipart("/employeur/offres").file(offre).principal(principal))
                .andExpect(status().isCreated());
        var dto = org.mockito.ArgumentCaptor.forClass(CreationOffreDeStageDTO.class);
        verify(employeurService).creerOffre(dto.capture(), any(), anyString());
        org.assertj.core.api.Assertions.assertThat(dto.getValue().title()).isEqualTo("Stage");
    }

    @Test
    void doitRetourner409QuandLOffreEstDeclinee() throws Exception {
        when(employeurService.accepterCandidatApresEntrevue(1L)).thenThrow(
                new com.lacouf.rsbjwt.exception.candidature.ActionsCandidatureDesactiveesException("error.offer_declined"));
        mockMvc.perform(put("/employeur/entrevues/1/acceptation").principal(principal))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("error.offer_declined"));
    }

    @Test
    void doitAccepterLeCandidatApresEntrevue() throws Exception {
        CandidatureDTO decision = new CandidatureDTO(1L, 1L, "Jean", "Test", "INFORMATIQUE", 1L,
                "Stage Java", "Entreprise", LocalDateTime.now(), null,
                StatutCandidature.ACCEPTEE, null, null, false, false);
        when(employeurService.accepterCandidatApresEntrevue(1L)).thenReturn(decision);

        mockMvc.perform(put("/employeur/entrevues/1/acceptation").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("ACCEPTEE"))
                .andExpect(jsonPath("$.statutEtudiant").doesNotExist());
        verify(employeurService).accepterCandidatApresEntrevue(1L);
    }

    @Test
    void doitCloreEntrevue() throws Exception {
        CandidatureDTO candidature = new CandidatureDTO(1L, 1L, "Jean", "Test", "INFORMATIQUE", 1L,
                "Stage Java", "Entreprise", LocalDateTime.now(), null,
                StatutCandidature.ENTREVUE_PLANIFIEE, null, StatutEtudiant.EN_RECHERCHE, false, false);
        when(employeurService.cloreEntrevue(1L)).thenReturn(candidature);

        mockMvc.perform(put("/employeur/entrevues/1/cloture").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.statut").value("ENTREVUE_PLANIFIEE"));
        verify(employeurService).cloreEntrevue(1L);
    }

    @Test
    void doitRefuserClotureEntrevueNonAutorisee() throws Exception {
        when(employeurService.cloreEntrevue(1L)).thenThrow(new OffreNonAutoriseeException());

        mockMvc.perform(put("/employeur/entrevues/1/cloture").principal(principal))
                .andExpect(status().isForbidden());
    }

    @Test
    void doitObtenirConvocationParCandidature() throws Exception {
        when(employeurService.obtenirConvocation(10L)).thenReturn(convocationEntrevueDTO);

        mockMvc.perform(get("/employeur/candidatures/10/entrevue").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(convocationEntrevueDTO.id()))
                .andExpect(jsonPath("$.candidatureId").value(convocationEntrevueDTO.candidatureId()))
                .andExpect(jsonPath("$.statut").value("A_CONFIRMER"));
        verify(employeurService).obtenirConvocation(10L);
    }

    @Test
    void doitRetournerNotFoundQuandConvocationIntrouvable() throws Exception {
        when(employeurService.obtenirConvocation(10L)).thenThrow(new ConvocationIntrouvableException());

        mockMvc.perform(get("/employeur/candidatures/10/entrevue").principal(principal))
                .andExpect(status().isNotFound());
    }

    @Test
    void doitRefuserLeCandidatApresEntrevueAvecUnMotif() throws Exception {
        CandidatureDTO decision = new CandidatureDTO(1L, 1L, "Jean", "Test", "INFORMATIQUE", 1L,
                "Stage Java", "Entreprise", LocalDateTime.now(), null,
                StatutCandidature.REFUSEE, "Autre profil retenu", null, false, false);
        when(employeurService.refuserCandidatApresEntrevue(1L, "Autre profil retenu")).thenReturn(decision);

        mockMvc.perform(put("/employeur/entrevues/1/refus").principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RefusCandidatureDTO("Autre profil retenu"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("REFUSEE"))
                .andExpect(jsonPath("$.motifRefus").value("Autre profil retenu"))
                .andExpect(jsonPath("$.statutEtudiant").doesNotExist());
    }

    @Test
    void doitRetournerBadRequestPourUnMotifVide() throws Exception {
        mockMvc.perform(put("/employeur/entrevues/1/refus").principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RefusCandidatureDTO("   "))))
                .andExpect(status().isBadRequest());
        verify(employeurService, never()).refuserCandidatApresEntrevue(anyLong(), anyString());
    }

    @Test
    void doitSignerContratAvecSucces() throws Exception {
        String motDePasse = "Losange12%";

        ContratDTO contratDTO = new ContratDTO(
                1L,
                1L,
                1L,
                null,
                LocalDateTime.now(),
                null, null);

        when(employeurService.signerContrat(1L, motDePasse))
                .thenReturn(contratDTO);

        mockMvc.perform(put("/employeur/contrats/1/signature")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "motDePasse": "Losange12%"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.dateSignatureEmployeur").exists());
    }

    @Test
    void doitRetournerNotFoundQuandContratIntrouvablePourSignature() throws Exception {
        String motDePasse = "Losange12%";

        when(employeurService.signerContrat(99L, motDePasse))
                .thenThrow(new ContratIntrouvableException());

        mockMvc.perform(put("/employeur/contrats/99/signature")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "motDePasse": "Losange12%"
                            }
                            """))
                .andExpect(status().isNotFound());
    }

    @Test
    void doitRetournerForbiddenQuandOffreNonAutoriseePourSignature() throws Exception {
        String motDePasse = "Losange12%";

        when(employeurService.signerContrat(1L, motDePasse))
                .thenThrow(new OffreNonAutoriseeException());

        mockMvc.perform(put("/employeur/contrats/1/signature")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "motDePasse": "Losange12%"
                            }
                            """))
                .andExpect(status().isForbidden());
    }

    @Test
    void doitRetournerUnauthorizedQuandMotDePasseInvalidePourSignature() throws Exception {
        String motDePasse = "MauvaisMotDePasse";

        when(employeurService.signerContrat(1L, motDePasse))
                .thenThrow(new MotDePasseInvalideException());

        mockMvc.perform(put("/employeur/contrats/1/signature")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "motDePasse": "MauvaisMotDePasse"
                            }
                            """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void doitRetournerBadRequestQuandMotDePasseManquantPourSignature() throws Exception {
        mockMvc.perform(put("/employeur/contrats/1/signature")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "motDePasse": ""
                            }
                            """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doitRetournerLesCandidaturesDeLEmployeur() throws Exception {
        when(employeurService.obtenirCandidatures()).thenReturn(List.of());

        mockMvc.perform(get("/employeur/candidatures"))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
        verify(employeurService).obtenirCandidatures();
    }

    @Test
    void doitRetournerLesCandidaturesPourLOffreDemandee() throws Exception {
        when(employeurService.getCandidaturesParOffre(10L)).thenReturn(List.of());

        mockMvc.perform(get("/employeur/offres/10/candidatures"))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
        verify(employeurService).getCandidaturesParOffre(10L);
    }

    @Test
    void doitRefuserLesCandidaturesDUneOffreNonAutorisee() throws Exception {
        when(employeurService.getCandidaturesParOffre(10L)).thenThrow(new OffreNonAutoriseeException());

        mockMvc.perform(get("/employeur/offres/10/candidatures"))
                .andExpect(status().isForbidden());
    }

    @Test
    void doitRetournerLesEntrevuesDeLEmployeur() throws Exception {
        when(employeurService.obtenirEntrevues()).thenReturn(List.of(convocationEntrevueDTO));

        mockMvc.perform(get("/employeur/entrevues"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        verify(employeurService).obtenirEntrevues();
    }

    @Test
    void doitRetenirLaCandidatureDemandeePourEntrevue() throws Exception {
        CandidatureDTO candidature = new CandidatureDTO(20L, 1L, "Jean", "Test", "INFORMATIQUE", 1L,
                "Stage Java", "Entreprise", LocalDateTime.now(), null,
                StatutCandidature.RETENUE_POUR_ENTREVUE, null, StatutEtudiant.EN_RECHERCHE, false, false);
        when(employeurService.retenirCandidaturePourEntrevue(20L)).thenReturn(candidature);

        mockMvc.perform(put("/employeur/candidatures/20"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("RETENUE_POUR_ENTREVUE"));
        verify(employeurService).retenirCandidaturePourEntrevue(20L);
    }

    @Test
    void doitRetournerNotFoundPourUneCandidatureARetenirIntrouvable() throws Exception {
        when(employeurService.retenirCandidaturePourEntrevue(99L)).thenThrow(new CandidatureIntrouvableException());

        mockMvc.perform(put("/employeur/candidatures/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void doitRetourner400PourLesDatesInvalidesDeLOffre() throws Throwable {
        Throwable[] erreurs = {new DateAvantAujourdhuiException(),
                new DateFinAffichageAvantAujourdhuiException(), new DateFinAffichageApresDateDebutException()};
        String[] messages = {"add_offer.errors.start_date_before_today",
                "add_offer.errors.display_end_date_before_today", "add_offer.errors.display_end_date_after_start_date"};
        MockMultipartFile offre = new MockMultipartFile("offre", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(creationOffreDeStageDTO));
        when(employeurService.creerOffre(any(), any(), anyString())).thenThrow(erreurs);

        for (int i = 0; i < erreurs.length; i++) {
            mockMvc.perform(multipart("/employeur/offres").file(offre).principal(principal))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(messages[i]));
        }
    }

    @Test
    void doitRetourner409PourUneDecisionApresEntrevueInvalide() throws Exception {
        when(employeurService.cloreEntrevue(1L)).thenThrow(new DecisionCandidatureInvalideException());

        mockMvc.perform(put("/employeur/entrevues/1/cloture"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("error.decision_invalid"));
    }

    @Test
    void doitRetourner400PourUnMotifDeRefusInvalide() throws Exception {
        when(employeurService.refuserCandidatApresEntrevue(1L, "Motif"))
                .thenThrow(new MotifRefusInvalideException());

        mockMvc.perform(put("/employeur/entrevues/1/refus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"motifRefus":"Motif"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("error.refusal_reason_invalid"));
    }
}
