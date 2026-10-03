package com.lacouf.rsbjwt.presentation;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacouf.rsbjwt.model.Enum.StatutOffre;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.RejectionCommentDTO;
import com.lacouf.rsbjwt.service.dto.SecteurEmployeurDTO;
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

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@ActiveProfiles("test")
public class GestionnaireControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private GestionnaireService gestionnaireService;

    private MockMvc mockMvc;
    @BeforeEach
    void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
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
    void doitRetournerListeOffresEnAttente() throws Exception {

        OffreDeStageDTO offre = new OffreDeStageDTO(
                1L,
                "Développeur Java",
                "Développement d'une application web",
                25.0,
                null,
                null,
                null,
                StatutOffre.EN_ATTENTE,
                "offre.pdf",
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
                StatutOffre.ACCEPTEE,
                "offre.pdf",
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
                StatutOffre.REFUSEE,
                "offre.pdf",
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
}
