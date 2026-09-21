package com.lacouf.rsbjwt.presentation;


import com.lacouf.rsbjwt.service.GestionnaireService;
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
}
