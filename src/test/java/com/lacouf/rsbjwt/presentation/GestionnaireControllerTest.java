package com.lacouf.rsbjwt.presentation;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;



@SpringBootTest
@ActiveProfiles("test")
public class GestionnaireControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private GestionnaireService gestionnaireService;

    private MockMvc mockMvc;

    private JsonMapper jsonMapper;

    private EtudiantDTO etudiantDTO;

    @BeforeEach
    void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        jsonMapper = JsonMapper.builder().build();

        etudiantDTO = new EtudiantDTO(
                1L,
                "Christelle",
                "Altineus",
                "christelle@gmail.com",
                "2226252",
                "438-297-8191",
                "INFORMATIQUE"
        );
    }


    @Test
    void doitRetournerListeSecteurs() throws Exception {

        List<SecteurEmployeurDTO> secteurs = List.of(
                new SecteurEmployeurDTO("INFORMATIQUE", "Informatique"),
                new SecteurEmployeurDTO("PHARMACEUTIQUE", "Pharmaceutique"),
                new SecteurEmployeurDTO("FINANCE", "Finance")
        );

        when(gestionnaireService.getAllSecteurs()).thenReturn(secteurs);

        mockMvc.perform(get("/gestionnaire/secteurEmployeur")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("INFORMATIQUE"))
                .andExpect(jsonPath("$[0].label").value("Informatique"))
                .andExpect(jsonPath("$[1].name").value("PHARMACEUTIQUE"))
                .andExpect(jsonPath("$[1].label").value("Pharmaceutique"))
                .andExpect(jsonPath("$[2].name").value("FINANCE"))
                .andExpect(jsonPath("$[2].label").value("Finance"));
    }

    @Test
    void doitRetournerListeDepartement() throws Exception {

        List<DepartementDTO> departement = List.of(
                new DepartementDTO("INFORMATIQUE", "Informatique"),
                new DepartementDTO("PHARMACEUTIQUE", "Pharmaceutique"),
                new DepartementDTO("FINANCE", "Finance")
        );

        when(gestionnaireService.getAllDepartements()).thenReturn(departement);

        mockMvc.perform(get("/gestionnaire/departement")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("INFORMATIQUE"))
                .andExpect(jsonPath("$[0].label").value("Informatique"))
                .andExpect(jsonPath("$[1].name").value("PHARMACEUTIQUE"))
                .andExpect(jsonPath("$[1].label").value("Pharmaceutique"))
                .andExpect(jsonPath("$[2].name").value("FINANCE"))
                .andExpect(jsonPath("$[2].label").value("Finance"));
    }

    @Test
    void doitRetournerListeOffresEnAttente() throws Exception {

        OffreDeStageDTO offre = new OffreDeStageDTO(
                1L,
                "Développeur Java",
                "Développement d'une application web",
                25.0,
                null,
                null,
                null,
                Statut.EN_ATTENTE,
                "offre.pdf",
                null,
                null
        );

        when(gestionnaireService.getOffresEnAttente())
                .thenReturn(List.of(offre));

        mockMvc.perform(get("/gestionnaire/offres")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Développeur Java"))
                .andExpect(jsonPath("$[0].statut").value("EN_ATTENTE"))
                .andExpect(jsonPath("$[0].fileName").value("offre.pdf"));
    }

    @Test
    void doitApprouverUneOffre() throws Exception {

        OffreDeStageDTO offre = new OffreDeStageDTO(
                1L,
                "Développeur Java",
                "Développement d'une application web",
                25.0,
                null,
                null,
                null,
                Statut.ACCEPTEE,
                "offre.pdf",
                null,
                null
        );

        when(gestionnaireService.approuverOffre(1L))
                .thenReturn(offre);

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .put("/gestionnaire/offres/1/approuver")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statut").value("ACCEPTEE"))
                .andExpect(jsonPath("$.rejectionComment").isEmpty());
    }

    @Test
    void doitRefuserUneOffre() throws Exception {

        String commentaire = "L'offre ne respecte pas les exigences.";

        OffreDeStageDTO offre = new OffreDeStageDTO(
                1L,
                "Développeur Java",
                "Développement d'une application web",
                25.0,
                null,
                null,
                null,
                Statut.REFUSEE,
                "offre.pdf",
                null,
                commentaire

        );

        when(gestionnaireService.refuserOffre(1L, commentaire))
                .thenReturn(offre);

        String requestBody = new ObjectMapper().writeValueAsString(
                new RejectionCommentDTO(commentaire)
        );

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .put("/gestionnaire/offres/1/refuser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statut").value("REFUSEE"))
                .andExpect(jsonPath("$.rejectionComment").value(commentaire));
    }

    @Test
    void doitRetournerCurriculumVitaeEnAttente() throws Exception {
        CvEtudiantDTO cvEtudiantDTO = new CvEtudiantDTO(
                -1,
                etudiantDTO,
                "LeRagout",
                "application/pdf",
                18L,
                LocalDateTime.now(),
                Statut.EN_ATTENTE,
                ""
        );

        when(gestionnaireService.getCurriculumVitaeEnAttente()).thenReturn(List.of(cvEtudiantDTO));

       MvcResult mvcResult =  mockMvc.perform(get("/gestionnaire/cvs/attente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(cvEtudiantDTO)))
                .andExpect(status().isOk()).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<CvEtudiantDTO> cvEtudiantDTOResult = jsonMapper.readValue(
                jsonResponse,
                new TypeReference<List<CvEtudiantDTO>>() {}
        );

        assertThat(cvEtudiantDTOResult).hasSize(1);
        assertThat(cvEtudiantDTOResult.getFirst()).satisfies(dto -> {
            assertThat(dto.rejectionComment()).isEqualTo(cvEtudiantDTO.rejectionComment());
            assertThat(dto.fileName()).isEqualTo(cvEtudiantDTO.fileName());
            assertThat(dto.statut()).isEqualTo(cvEtudiantDTO.statut());

        });
    }

    @Test
    void doitApprouverUnCurriculumVitae() throws Exception {
        CvEtudiantDTO cvEtudiantDTO = new CvEtudiantDTO(
                1,
                etudiantDTO,
                "LeRagout",
                "application/pdf",
                18L,
                LocalDateTime.now(),
                Statut.ACCEPTEE,
                ""
        );

        when(gestionnaireService.approuverCurriculumVitae(1L)).thenReturn(cvEtudiantDTO);

        MvcResult mvcResult = mockMvc.perform(put("/gestionnaire/cv/" + 1 + "/approuver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(cvEtudiantDTO)))
                .andExpect(status().isOk()).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        CvEtudiantDTO cvEtudiantDTOResult = jsonMapper.readValue(jsonResponse, CvEtudiantDTO.class);

        assertThat(cvEtudiantDTOResult).isNotNull().satisfies(dto -> {
            assertThat(dto.rejectionComment()).isEqualTo(cvEtudiantDTO.rejectionComment());
            assertThat(dto.fileName()).isEqualTo(cvEtudiantDTO.fileName());
            assertThat(dto.statut()).isEqualTo(cvEtudiantDTO.statut());
        });
    }

    @Test
    void doitRefuserUnCurriculumVitae() throws Exception {
        CvEtudiantDTO cvEtudiantDTO = new CvEtudiantDTO(
                1,
                etudiantDTO,
                "LeRagout",
                "application/pdf",
                18L,
                LocalDateTime.now(),
                Statut.REFUSEE,
                "Illegal Curriculum Vitae"
        );

        when(gestionnaireService.refuserCurriculumVitae(cvEtudiantDTO.id(), cvEtudiantDTO.rejectionComment())).thenReturn(cvEtudiantDTO);

        MvcResult mvcResult = mockMvc.perform(put("/gestionnaire/cv/" + 1 + "/refuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(cvEtudiantDTO)))
                .andExpect(status().isOk()).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        CvEtudiantDTO cvEtudiantDTOResult = jsonMapper.readValue(jsonResponse, CvEtudiantDTO.class);

        assertThat(cvEtudiantDTOResult).isNotNull().satisfies(dto -> {
            assertThat(dto.rejectionComment()).isEqualTo(cvEtudiantDTO.rejectionComment());
            assertThat(dto.fileName()).isEqualTo(cvEtudiantDTO.fileName());
            assertThat(dto.statut()).isEqualTo(cvEtudiantDTO.statut());
        });
    }
}
