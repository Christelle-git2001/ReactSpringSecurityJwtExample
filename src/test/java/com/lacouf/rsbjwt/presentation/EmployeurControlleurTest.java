package com.lacouf.rsbjwt.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Exceptions.ConfirmationMotDePasseEchouer;
import com.lacouf.rsbjwt.model.Exceptions.EmployeurExistant;
import com.lacouf.rsbjwt.service.EmployeurService;
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

    EmployeurInscriptionDTO employeurDTO;

    private MockMvc mockMvc;


    @BeforeEach
    void init(){
        employeurDTO = EmployeurInscriptionDTO.builder()
                .prenom("Gerard")
                .nom("Robert")
                .ville("Mercier")
                .telephone("165-685-4569")
                .email("Gerard.Robert@hotmail.com")
                .nomEntreprise("Gerard inc")
                .typeEntreprise("Startup")
                .secteurActivite(SecteurActivite.AEROSPATIAL)
                .password("Losange12%")
                .passwordConfirmation("Losange12%")
                .build();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void inscriptionEmployeurCreated() throws Exception {
        when(employeurService.inscription(any(EmployeurInscriptionDTO.class))).thenReturn(employeurDTO);


        mockMvc.perform(post("/inscription/employeur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeurDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void inscriptionEmployeurMotDePasseDiffereException() throws Exception {
        when(employeurService.inscription(any(EmployeurInscriptionDTO.class)))
                .thenThrow(new ConfirmationMotDePasseEchouer("Les mots de passe ne sont pas identiques"));

        mockMvc.perform(post("/inscription/employeur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeurDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inscriptionEmployeurExisteDeja() throws Exception{
        when(employeurService.inscription(any(EmployeurInscriptionDTO.class)))
                .thenThrow(new EmployeurExistant("L'utilisateur existe deja"));

        mockMvc.perform(post("/inscription/employeur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeurDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inscriptionChampsObligatoireManquant() throws Exception{
        employeurDTO = EmployeurInscriptionDTO.builder()
                .nom("Robert")
                .telephone("165-685-4569")
                .email("Gerard.Robert@hotmail.com")
                .nomEntreprise("Gerard inc")
                .typeEntreprise("Startup")
                .secteurActivite(SecteurActivite.AEROSPATIAL)
                .password("Losange12")
                .passwordConfirmation("Losange12")
                .build();
        mockMvc.perform(post("/inscription/employeur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeurDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inscriptionEmailInvalide() throws Exception{
        employeurDTO.setEmail("HAHA");

        mockMvc.perform(post("/inscription/employeur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeurDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inscriptionMDPInvalide() throws Exception{
        employeurDTO.setPassword("HAHA");
        employeurDTO.setPasswordConfirmation("HAHA");

        mockMvc.perform(post("/inscription/employeur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeurDTO)))
                .andExpect(status().isBadRequest());
    }


}
