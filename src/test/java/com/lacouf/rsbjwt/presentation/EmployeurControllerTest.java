package com.lacouf.rsbjwt.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.Exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.Exception.EmailExistantException;
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEmployeurDTO;
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
public class EmployeurControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private EmployeurService employeurService;

    private ObjectMapper objectMapper;

    InscriptionEmployeurDTO inscriptionEmployeurDTO;

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
                "Startup",
                "Losange12%",
                "Losange12%"
        );
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void inscriptionEmployeurCreated() throws Exception {
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
    void inscriptionEmployeurMotDePasseDiffereException() throws Exception {
        when(employeurService.creeCompteEmployeur(any(InscriptionEmployeurDTO.class)))
                .thenThrow(new MotDePasseNonCorrespondantException());

        mockMvc.perform(post("/employeur/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEmployeurDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inscriptionEmployeurExisteDeja() throws Exception{
        when(employeurService.creeCompteEmployeur(any(InscriptionEmployeurDTO.class)))
                .thenThrow(new EmailExistantException());

        mockMvc.perform(post("/employeur/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEmployeurDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void inscriptionChampsObligatoireManquant() throws Exception{
        inscriptionEmployeurDTO = new InscriptionEmployeurDTO(
                "Gerard",
                "Robert",
                "165-685-4569",
                "Gerard.Robert@hotmail.com",
                "",
                "Gerard inc",
                SecteurActivite.AEROSPATIAL,
                "Startup",
                "Losange12%",
                "Losange12%"
        );
        mockMvc.perform(post("/employeur/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEmployeurDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inscriptionEmailInvalide() throws Exception{
        inscriptionEmployeurDTO = new InscriptionEmployeurDTO(
                "Gerard",
                "Robert",
                "165-685-4569",
                "HAHA",
                "Mercier",
                "Gerard inc",
                SecteurActivite.AEROSPATIAL,
                "Startup",
                "Losange12%",
                "Losange12%"
        );

        mockMvc.perform(post("/employeur/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEmployeurDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inscriptionMDPInvalide() throws Exception{
        inscriptionEmployeurDTO = new InscriptionEmployeurDTO(
                "Gerard",
                "Robert",
                "165-685-4569",
                "Gerard.Robert@hotmail.com",
                "Mercier",
                "Gerard inc",
                SecteurActivite.AEROSPATIAL,
                "Startup",
                "HAHA",
                "HAHA"
        );

        mockMvc.perform(post("/employeur/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEmployeurDTO)))
                .andExpect(status().isBadRequest());
    }
}
