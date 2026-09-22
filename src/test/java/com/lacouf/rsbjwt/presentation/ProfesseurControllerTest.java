package com.lacouf.rsbjwt.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.service.ProfesseurService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                "INFORMATIQUE"
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

}