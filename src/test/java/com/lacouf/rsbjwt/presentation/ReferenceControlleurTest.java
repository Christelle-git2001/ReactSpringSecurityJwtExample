package com.lacouf.rsbjwt.presentation;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.service.dto.SecteurEmployeurDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
public class ReferenceControlleurTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper;

    private SecteurActivite[] secteurActivite;

    private MockMvc mockMvc;
    @BeforeEach
    void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        objectMapper = new ObjectMapper();
        secteurActivite = SecteurActivite.values();
    }

    @Test
    public void recoisTousSecteursActivites() throws Exception {
         MvcResult mvcResult =  mockMvc.perform(get("/employeur/secteur")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(mvcResult.getResponse());
        String jsonContent = mvcResult.getResponse().getContentAsString();
        List<SecteurEmployeurDTO> resultList = objectMapper.readValue(
                jsonContent,
                new TypeReference<>() {
                }
        );
        assertThat(secteurActivite.length).isEqualTo(resultList.size());
    }
}
