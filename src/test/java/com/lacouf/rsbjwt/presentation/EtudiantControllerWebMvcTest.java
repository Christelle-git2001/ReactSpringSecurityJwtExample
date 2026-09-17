package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.EtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import com.lacouf.rsbjwt.service.EtudiantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EtudiantControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EtudiantService etudiantService;

    @Test
    void inscriptionEtudiantValide() throws Exception {

        mockMvc.perform(post("/etudiant/register")
                        .contentType("application/json")
                        .content("""
                        {
                            "firstName": "Christelle",
                            "lastName": "Altineus",
                            "phone": "438-297-8191",
                            "email": "christelle@gmail.com",
                            "matricule": "2226252",
                            "password": "111111",
                            "passwordConfirmation": "111111"
                        }
                    """))
                .andExpect(status().isOk());
    }
}
