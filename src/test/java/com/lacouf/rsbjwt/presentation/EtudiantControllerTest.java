package com.lacouf.rsbjwt.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacouf.rsbjwt.Exception.EmailExistantException;
import com.lacouf.rsbjwt.Exception.MatriculeExistantException;
import com.lacouf.rsbjwt.Exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.service.EtudiantService;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
public class EtudiantControllerWebMvcTest {
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
                "111111",
                "111111"
        );
    }

    @Test
    void inscriptionEtudiantOk() throws Exception {

        EtudiantDTO etudiantDTO = new EtudiantDTO(
                1L,
                "Christelle",
                "Altineus",
                "christelle@gmail.com",
                "2226252",
                "438-297-8191"
        );

        when(etudiantService.inscrireEtudiant(any(InscriptionEtudiantDTO.class)))
                .thenReturn(etudiantDTO);

        mockMvc.perform(post("/etudiant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEtudiantDTO)))
                .andExpect(status().isOk());

    }
    @Test
    void inscriptionEtudiantMotDePasseDifferent() throws Exception {

        inscriptionEtudiantDTO = new InscriptionEtudiantDTO(
                "Christelle",
                "Altineus",
                "438-297-8191",
                "christelle@gmail.com",
                "2226252",
                "111111",
                "222222"
        );

        when(etudiantService.inscrireEtudiant(any(InscriptionEtudiantDTO.class)))
                .thenThrow(new MotDePasseNonCorrespondantException());

        mockMvc.perform(post("/etudiant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEtudiantDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inscriptionEtudiantEmailExisteDeja() throws Exception {

        when(etudiantService.inscrireEtudiant(any(InscriptionEtudiantDTO.class)))
                .thenThrow(new EmailExistantException());

        mockMvc.perform(post("/etudiant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEtudiantDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void inscriptionEtudiantMatriculeExisteDeja() throws Exception {

        when(etudiantService.inscrireEtudiant(any(InscriptionEtudiantDTO.class)))
                .thenThrow(new MatriculeExistantException());

        mockMvc.perform(post("/etudiant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscriptionEtudiantDTO)))
                .andExpect(status().isConflict());
    }
}