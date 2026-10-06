package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.AucunGestionnaireTrouver;
import com.lacouf.rsbjwt.model.Gestionnaire;
import com.lacouf.rsbjwt.repository.GestionnaireRepository;
import com.lacouf.rsbjwt.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("dev")
class DevAuthControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private GestionnaireRepository gestionnaireRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    void doitRetournerTokenQuandGestionnaireExiste() throws Exception {
        Gestionnaire mockGestionnaire = Gestionnaire.builder()
                .email("gestionnaire@lacouf.com")
                .password("encoded_pass")
                .build();

        String expectedToken = "fake.jwt.token.value";

        when(gestionnaireRepository.findAll()).thenReturn(List.of(mockGestionnaire));
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(expectedToken);

        MvcResult mvcResult = mockMvc.perform(post("/api/auth/login/gestionnaire"))
                .andExpect(status().isOk())
                .andReturn();

        String actualToken = mvcResult.getResponse().getContentAsString();
        assertThat(actualToken).isEqualTo(expectedToken);

        verify(gestionnaireRepository).findAll();
        verify(jwtTokenProvider).generateToken(any(Authentication.class));
    }

    @Test
    void doitLancerExceptionQuandAucunGestionnaire()  {
        when(gestionnaireRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> mockMvc.perform(post("/api/auth/login/gestionnaire")))
                .hasCauseInstanceOf(AucunGestionnaireTrouver.class);
    }
}