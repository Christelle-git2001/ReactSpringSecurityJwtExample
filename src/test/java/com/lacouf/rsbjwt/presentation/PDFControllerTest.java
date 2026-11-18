package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.contrat.ContratIntrouvableException;
import com.lacouf.rsbjwt.exception.EvaluationStagiaireIntrouvable;
import com.lacouf.rsbjwt.service.PDFService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
public class PDFControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private PDFService pdfService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void doitRetournerPdfContratAvecSucces() throws Exception {
        byte[] contenuPdf = "Contenu du contrat PDF".getBytes();
        Resource pdfResource = new ByteArrayResource(contenuPdf);

        when(pdfService.createContratPdf(1L, "fr")).thenReturn(pdfResource);

        MvcResult mvcResult = mockMvc.perform(get("/pdf/contrat/1")
                        .param("langue", "fr"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"contrat.pdf\""))
                .andReturn();

        byte[] responseBytes = mvcResult.getResponse().getContentAsByteArray();
        assertThat(responseBytes).isEqualTo(contenuPdf);
    }

    @Test
    void doitRetournerNotFoundQuandContratIntrouvable() throws Exception {
        when(pdfService.createContratPdf(99L, "fr"))
                .thenThrow(new ContratIntrouvableException());

        mockMvc.perform(get("/pdf/contrat/99")
                        .param("langue", "fr"))
                .andExpect(status().isNotFound());
    }

    @Test
    void doitRetournerPdfEvaluationStagiaireAvecSucces() throws Exception {
        byte[] contenuPdf = "Contenu de l'evaluation du stagiaire PDF".getBytes();
        Resource pdfResource = new ByteArrayResource(contenuPdf);
        Long candidatureId = 1L;

        // Mock du service pour la langue par défaut (fr)
        when(pdfService.createEvaluationStagiaire("fr", candidatureId)).thenReturn(pdfResource);

        MvcResult mvcResult = mockMvc.perform(get("/pdf/evaluation/etudiant/" + candidatureId)
                        .param("langue", "fr"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"evaluation_1.pdf\""))
                .andReturn();

        byte[] responseBytes = mvcResult.getResponse().getContentAsByteArray();
        assertThat(responseBytes).isEqualTo(contenuPdf);
    }


    @Test
    void doitRetournerNotFoundQuandEvaluationStagiaireIntrouvable() throws Exception {
        Long candidatureId = 99L;

        when(pdfService.createEvaluationStagiaire("en", candidatureId))
                .thenThrow(new EvaluationStagiaireIntrouvable());

        mockMvc.perform(get("/pdf/evaluation/etudiant/" + candidatureId)
                        .param("langue", "en"))
                .andExpect(status().isNotFound());
    }
}