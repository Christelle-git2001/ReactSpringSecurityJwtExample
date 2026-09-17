package com.lacouf.rsbjwt.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Exceptions.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.model.Exceptions.CourrielExistantException;
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurInscriptionDTO;
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
public class EmployeurControlleurTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private EmployeurService employeurService;

    private ObjectMapper objectMapper;

    EmployeurInscriptionDTO employeurInscriptionDTO;

    private MockMvc mockMvc;


    @BeforeEach
    void init(){
        employeurInscriptionDTO = new EmployeurInscriptionDTO(
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
                1,
              "Gerard",
              "Robert",
              "165-685-4569",
              "Gerard.Robert@hotmail.com",
              "Mercier",
              "Gerard inc",
              SecteurActivite.AEROSPATIAL,
              "Startup"

        );
        when(employeurService.creeCompteEmployeur(any(EmployeurInscriptionDTO.class))).thenReturn(employeurDTO);


        mockMvc.perform(post("/inscription/employeur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeurInscriptionDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void inscriptionEmployeurMotDePasseDiffereException() throws Exception {
        when(employeurService.creeCompteEmployeur(any(EmployeurInscriptionDTO.class)))
                .thenThrow(new MotDePasseNonCorrespondantException("Les mots de passe ne sont pas identiques"));

        mockMvc.perform(post("/inscription/employeur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeurInscriptionDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inscriptionEmployeurExisteDeja() throws Exception{
        when(employeurService.creeCompteEmployeur(any(EmployeurInscriptionDTO.class)))
                .thenThrow(new CourrielExistantException("L'utilisateur existe deja"));

        mockMvc.perform(post("/inscription/employeur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeurInscriptionDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inscriptionChampsObligatoireManquant() throws Exception{
        employeurInscriptionDTO = new EmployeurInscriptionDTO(
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
        mockMvc.perform(post("/inscription/employeur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeurInscriptionDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inscriptionEmailInvalide() throws Exception{
        employeurInscriptionDTO = new EmployeurInscriptionDTO(
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

        mockMvc.perform(post("/inscription/employeur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeurInscriptionDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inscriptionMDPInvalide() throws Exception{
        employeurInscriptionDTO = new EmployeurInscriptionDTO(
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

        mockMvc.perform(post("/inscription/employeur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeurInscriptionDTO)))
                .andExpect(status().isBadRequest());
    }


}
