package com.lacouf.rsbjwt.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.service.ProfesseurService;
import com.lacouf.rsbjwt.service.dto.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionProfesseurDTO;
import com.lacouf.rsbjwt.service.dto.ProfesseurDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest
@ActiveProfiles("test")
public class ProfesseurControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ProfesseurService professeurService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private InscriptionProfesseurDTO inscriptionProfesseurDTO;
    private ProfesseurDTO professeurDTO;

    @BeforeEach
    void init() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        objectMapper = new ObjectMapper();

        inscriptionProfesseurDTO = new InscriptionProfesseurDTO(
                "Carole",
                "Test",
                "carole.test@gmail.com",
                "111111",
                "111111",
                "1234567",
                "123-123-1234",
                "INFORMATIQUE"
        );

        professeurDTO = new ProfesseurDTO(
                1L,
                "Carole",
                "Test",
                "carole.test@gmail.com",
                "1234567",
                "123-123-1234",
                "INFORMATIQUE",
                null
        );
    }

    @Test
    void doitCreerCompteProfesseur() throws Exception {
        when(professeurService.creerCompteProfesseur(any(InscriptionProfesseurDTO.class)))
                .thenReturn(professeurDTO);

        mockMvc.perform(post("/professeur/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionProfesseurDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void doitRetournerConflictEmailExistant() throws Exception {
        when(professeurService.creerCompteProfesseur(any()))
                .thenThrow(new EmailExistantException());

        mockMvc.perform(post("/professeur/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionProfesseurDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void doitRetournerConflictNumeroTelephoneExistant() throws Exception {
        when(professeurService.creerCompteProfesseur(any()))
                .thenThrow(new NumeroTelephoneExistantException());

        mockMvc.perform(post("/professeur/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionProfesseurDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void doitRetournerConflictMatriculeExistant() throws Exception {
        when(professeurService.creerCompteProfesseur(any()))
                .thenThrow(new MatriculeExistantException());

        mockMvc.perform(post("/professeur/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionProfesseurDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void doitRetournerBadRequestMotDePasseNonCorrespondant() throws Exception {
        when(professeurService.creerCompteProfesseur(any()))
                .thenThrow(new MotDePasseNonCorrespondantException());

        mockMvc.perform(post("/professeur/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionProfesseurDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doitRetournerBadRequestDepartementInvalide() throws Exception {
        when(professeurService.creerCompteProfesseur(any()))
                .thenThrow(new DepartementInvalideException(inscriptionProfesseurDTO.department()));

        mockMvc.perform(post("/professeur/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionProfesseurDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doitRetournerLesEtudiantsAssignes() throws Exception {

        EtudiantDTO etudiant1 = new EtudiantDTO(
                1L,
                "Jean",
                "Test",
                "jean.test@gmail.com",
                "1234567",
                "123-123-1234",
                "INFORMATIQUE",
                null,
                1L,
                "Carole"
        );

        EtudiantDTO etudiant2 = new EtudiantDTO(
                2L,
                "Marie",
                "Test",
                "marie.test@gmail.com",
                "7654321",
                "123-123-1235",
                "INFORMATIQUE",
                null,
                1L,
                "Carole"
        );

        when(professeurService.getEtudiantsAssignes())
                .thenReturn(List.of(etudiant1, etudiant2));

        mockMvc.perform(get("/professeur/etudiants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Jean"))
                .andExpect(jsonPath("$[1].firstName").value("Marie"));

        verify(professeurService).getEtudiantsAssignes();
    }

    @Test
    void doitRetournerListeVideSiAucunEtudiantAssigne() throws Exception {

        when(professeurService.getEtudiantsAssignes())
                .thenReturn(List.of());

        mockMvc.perform(get("/professeur/etudiants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(professeurService).getEtudiantsAssignes();
    }

    @Test
    void doitRetournerLesCandidaturesDUnEtudiant() throws Exception {
        Long etudiantId = 1L;

        CandidatureDTO candidature = new CandidatureDTO(
                10L,
                etudiantId,
                "Marie",
                "Tremblay",
                "INFORMATIQUE",
                5L,
                "Développeuse web",
                "Entreprise ABC",
                LocalDateTime.now(),
                2L,
                StatutCandidature.SOUMISE
        );

        when(professeurService.getCandidaturesEtudiant(etudiantId))
                .thenReturn(List.of(candidature));

        mockMvc.perform(get("/professeur/etudiants/{etudiantId}/candidatures", etudiantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].etudiantId").value(1))
                .andExpect(jsonPath("$[0].statut").value("SOUMISE"));

        verify(professeurService).getCandidaturesEtudiant(etudiantId);
    }

    @Test
    void doitRetournerListeVideSiEtudiantNAucuneCandidature() throws Exception {
        Long etudiantId = 1L;

        when(professeurService.getCandidaturesEtudiant(etudiantId))
                .thenReturn(List.of());

        mockMvc.perform(get("/professeur/etudiants/{etudiantId}/candidatures", etudiantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(professeurService).getCandidaturesEtudiant(etudiantId);
    }

}