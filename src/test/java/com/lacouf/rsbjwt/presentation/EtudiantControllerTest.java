package com.lacouf.rsbjwt.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacouf.rsbjwt.Exception.EmailExistantException;
import com.lacouf.rsbjwt.Exception.MatriculeExistantException;
import com.lacouf.rsbjwt.Exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.Exception.NumeroTelephoneExistantException;
import com.lacouf.rsbjwt.service.EtudiantService;
import com.lacouf.rsbjwt.service.dto.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
                "INFORMATIQUE"
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
    void doitTeleverserCv() throws Exception {
        CvEtudiantDTO cvDTO = new CvEtudiantDTO(
                1L,
                "cv.pdf",
                "application/pdf",
                13L,
                LocalDateTime.now()
        );

        when(etudiantService.uploadCv(any(), anyString()))
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
                "cv.pdf",
                "application/pdf",
                13L,
                LocalDateTime.now()
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
}