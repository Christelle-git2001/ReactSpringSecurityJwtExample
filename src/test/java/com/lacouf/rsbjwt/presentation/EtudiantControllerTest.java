package com.lacouf.rsbjwt.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.service.EtudiantService;
import com.lacouf.rsbjwt.service.dto.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.lacouf.rsbjwt.service.dto.CandidatureDTO;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


@SpringBootTest
@ActiveProfiles("test")
public class EtudiantControllerTest {
    @Test
    void contexteCharge() {
        System.out.println("LE TEST ETUDIANT EST LANCE");
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private EtudiantService etudiantService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private InscriptionEtudiantDTO inscriptionEtudiantDTO;
    private EtudiantDTO etudiantDTO;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        objectMapper = new ObjectMapper();



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

        etudiantDTO = new EtudiantDTO(
                1L,
                "Christelle",
                "Altineus",
                "christelle@gmail.com",
                "2226252",
                "438-297-8191",
                "INFORMATIQUE",
                null
        );
    }

    @Test
    void doitCreerCompteEtudiant() throws Exception {

        EtudiantDTO etudiantDTO = new EtudiantDTO(
                1L,
                "Christelle",
                "Altineus",
                "christelle@gmail.com",
                "2226252",
                "438-297-8191",
                "INFORMATIQUE",
                null
        );

        when(etudiantService.creerCompteEtudiant(any(InscriptionEtudiantDTO.class)))
                .thenReturn(etudiantDTO);

        mockMvc.perform(post("/etudiant/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEtudiantDTO)))
                .andExpect(status().isCreated());

    }
    @Test
    void doitRetournerBadRequestMotDePasseNonCorrespondant() throws Exception {

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

        when(etudiantService.creerCompteEtudiant(any(InscriptionEtudiantDTO.class)))
                .thenThrow(new MotDePasseNonCorrespondantException());

        mockMvc.perform(post("/etudiant/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEtudiantDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doitRetournerConflictEmailExistant() throws Exception {

        when(etudiantService.creerCompteEtudiant(any(InscriptionEtudiantDTO.class)))
                .thenThrow(new EmailExistantException());

        mockMvc.perform(post("/etudiant/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEtudiantDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void doitRetournerConflictMatriculeExistant() throws Exception {

        when(etudiantService.creerCompteEtudiant(any(InscriptionEtudiantDTO.class)))
                .thenThrow(new MatriculeExistantException());

        mockMvc.perform(post("/etudiant/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEtudiantDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void doitRetournerConflictNumeroTelephoneExistant() throws Exception {

        when(etudiantService.creerCompteEtudiant(any(InscriptionEtudiantDTO.class)))
                .thenThrow(new NumeroTelephoneExistantException());

        mockMvc.perform(post("/etudiant/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEtudiantDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void doitRetournerMatriculeNonConforme() throws Exception {
        when(etudiantService.creerCompteEtudiant(any(InscriptionEtudiantDTO.class)))
                .thenThrow(new MatriculeNonConforme());

        mockMvc.perform(post("/etudiant/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEtudiantDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doitTeleverserCv() throws Exception {
        CvEtudiantDTO cvDTO = new CvEtudiantDTO(
                1L,
                etudiantDTO,
                "cv.pdf",
                "application/pdf",
                13L,
                LocalDateTime.now(),
                Statut.EN_ATTENTE,
                ""
        );

        when(etudiantService.televerserCv(any(), anyString()))
                .thenReturn(cvDTO);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cv.pdf",
                "application/pdf",
                "contenu pdf".getBytes()
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "christelle@gmail.com",
                null);

        mockMvc.perform(multipart("/etudiant/cv")
                .file(file)
                .principal(authentication))
            .andExpect(status().isCreated());
    }

    @Test
    void doitObtenirCv() throws Exception {
        CvEtudiantDTO cvDTO = new CvEtudiantDTO(
                1L,
                etudiantDTO,
                "cv.pdf",
                "application/pdf",
                13L,
                LocalDateTime.now(),
                Statut.EN_ATTENTE,
                ""
        );

        when(etudiantService.getCv("christelle@gmail.com"))
                .thenReturn(cvDTO);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "christelle@gmail.com",
                null);

        mockMvc.perform(get("/etudiant/cv")
                        .principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    void doitTelechargerCv() throws Exception {
        Resource resource = new ByteArrayResource("contenu pdf".getBytes());

        when(etudiantService.telechargerCv("christelle@gmail.com"))
                .thenReturn(resource);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "christelle@gmail.com",
                null);

        mockMvc.perform(get("/etudiant/cv/download")
                        .principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    void doitRetournerNotFoundQuandCvIntrouvable() throws Exception {
        when(etudiantService.getCv("christelle@gmail.com"))
                .thenThrow(new FichierIntrouvableException());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "christelle@gmail.com",
                null);

        mockMvc.perform(get("/etudiant/cv")
                        .principal(authentication))
                .andExpect(status().isNotFound());
    }

    @Test
    void doitSupprimerCv() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "christelle@gmail.com",
                null);

        mockMvc.perform(delete("/etudiant/cv")
                        .principal(authentication))
                .andExpect(status().isNoContent());
    }

    @Test
    void doitRetournerLesOffresDisponibles() throws Exception {
        OffreDeStageDTO offre = new OffreDeStageDTO(
                1L,
                "Développeur Java",
                "Développement d'applications",
                25.0,
                Departement.INFORMATIQUE,
                LocalDate.of(2029, 1, 1),
                LocalDate.of(2029, 4, 30),
                LocalDate.of(2029, 1, 1).minusDays(15),
                Statut.ACCEPTEE,
                null,
                null,
                null,
                null
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                inscriptionEtudiantDTO.email(),
                null);

        when(etudiantService.getOffresDisponibles(
                Departement.valueOf(inscriptionEtudiantDTO.department()),
                inscriptionEtudiantDTO.email()))
                .thenReturn(List.of(offre));

        mockMvc.perform(
                        get("/etudiant/offres/" + inscriptionEtudiantDTO.department())
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Développeur Java"));
    }

    @Test
    void doitRetournerListeVideQuandAucuneOffreDisponible() throws Exception {

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                inscriptionEtudiantDTO.email(),
                null);

        when(etudiantService.getOffresDisponibles(
                Departement.valueOf(inscriptionEtudiantDTO.department()),
                inscriptionEtudiantDTO.email()))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/etudiant/offres/" + inscriptionEtudiantDTO.department())
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void doitRefuserAccesAuxOffresSiCvNonApprouve() throws Exception {

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                inscriptionEtudiantDTO.email(),
                null);

        when(etudiantService.getOffresDisponibles(
                Departement.valueOf(inscriptionEtudiantDTO.department()),
                inscriptionEtudiantDTO.email()))
                .thenThrow(new CurriculumVitaeNonApprouveException());

        mockMvc.perform(
                        get("/etudiant/offres/" + inscriptionEtudiantDTO.department())
                                .principal(authentication)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void doitRetournerLesCandidaturesEtudiant() throws Exception {

        CandidatureDTO candidature = new CandidatureDTO(
                1L,
                1L,
                "Christelle",
                "Test",
                "INFORMATIQUE",
                10L,
                "Développeur Web",
                "TechCorp",
                LocalDateTime.now()
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "christelle@gmail.com",
                        null
                );

        when(etudiantService.getCandidaturesEtudiant("christelle@gmail.com"))
                .thenReturn(List.of(candidature));

        mockMvc.perform(get("/etudiant/mesCandidatures")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].offreId").value(10))
                .andExpect(jsonPath("$[0].etudiantFirstName").value("Christelle"))
                .andExpect(jsonPath("$[0].etudiantLastName").value("Test"))
                .andExpect(jsonPath("$[0].etudiantDepartment").value("INFORMATIQUE"))
                .andExpect(jsonPath("$[0].offreTitle").value("Développeur Web"))
                .andExpect(jsonPath("$[0].employeurBusinessName").value("TechCorp"));

        verify(etudiantService, times(1))
                .getCandidaturesEtudiant("christelle@gmail.com");
    }


    @Test
    void doitRetournerListeVideQuandAucuneCandidature() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "christelle@gmail.com",
                        null
                );

        when(etudiantService.getCandidaturesEtudiant(
                "christelle@gmail.com"))
                .thenReturn(List.of());

        mockMvc.perform(get("/etudiant/mesCandidatures")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void doitPostulerAUneOffre() throws Exception {

        CandidatureDTO candidature = new CandidatureDTO(
                1L,
                1L,
                "Christelle",
                "Test",
                "INFORMATIQUE",
                10L,
                "Développeur Web",
                "TechCorp",
                LocalDateTime.now()
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "christelle@gmail.com",
                        null
                );

        when(etudiantService.postuler(10L, "christelle@gmail.com"))
                .thenReturn(candidature);

        mockMvc.perform(post("/etudiant/offres/10/postuler")
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.offreId").value(10))
                .andExpect(jsonPath("$.etudiantFirstName").value("Christelle"))
                .andExpect(jsonPath("$.offreTitle").value("Développeur Web"))
                .andExpect(jsonPath("$.employeurBusinessName").value("TechCorp"));
    }


    @Test
    void doitRetournerConflictQuandCandidatureDejaExistante() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "christelle@gmail.com",
                        null
                );

        when(etudiantService.postuler(10L, "christelle@gmail.com"))
                .thenThrow(new CandidatureDejaExistanteException());

        mockMvc.perform(post("/etudiant/offres/10/postuler")
                        .principal(authentication))
                .andExpect(status().isConflict());
    }

    @Test
    void doitRetournerConflictQuandOffreIndisponible() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "christelle@gmail.com",
                        null
                );

        when(etudiantService.postuler(10L, "christelle@gmail.com"))
                .thenThrow(new OffreDeStageIndisponibleException());

        mockMvc.perform(post("/etudiant/offres/10/postuler")
                        .principal(authentication))
                .andExpect(status().isConflict());
    }
}