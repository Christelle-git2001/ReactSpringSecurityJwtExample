package com.lacouf.rsbjwt.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.security.exception.AuthenticationException;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.LoginDTO;
import com.lacouf.rsbjwt.service.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
public class UtilisateurAppControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private UserAppService userAppService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LoginDTO loginDTO;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        objectMapper = new ObjectMapper();

        loginDTO = new LoginDTO(
                "christelle@gmail.com",
                "111111"
        );
    }

    @Test
    void connexionOk() throws Exception {
        when(userAppService.authenticateUser(any(LoginDTO.class)))
                .thenReturn("fake-jwt-token");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk()); // Si votre contrôleur renvoie 200 (sinon isAccepted())
    }

    @Test
    void connexionMotDePasseIncorrect() throws Exception {
        when(userAppService.authenticateUser(any(LoginDTO.class)))
                .thenThrow(new AuthenticationException(
                        HttpStatus.UNAUTHORIZED,
                        "login.credentialsWrong"));

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized()) // 401 au lieu de 403
                .andExpect(jsonPath("$.message").value("login.credentialsWrong"));
    }

    @Test
    void connexionUtilisateurInexistant() throws Exception {
        when(userAppService.authenticateUser(any(LoginDTO.class)))
                .thenThrow(new UserNotFoundException());

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized()) // 401 au lieu de 404 selon votre ExceptionHandler
                .andExpect(jsonPath("$.message").value("error.user_not_found"));
    }

    @Test
    void doitObtenirUtilisateurConnecte() throws Exception {
        EtudiantDTO userDTO = new EtudiantDTO(
                1L,
                "Christelle",
                "Altineus",
                "christelle@gmail.com",
                "5555555",
                "554-858-6958",
                Departement.INFORMATIQUE.name(),
                LocalDateTime.now(),
                1l,
                "Jean"
        );

        when(userAppService.getMe("Bearer fake-jwt-token"))
                .thenReturn(userDTO);

        mockMvc.perform(get("/user/me")
                        .header("Authorization", "Bearer fake-jwt-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("christelle@gmail.com"));
    }

    @Test
    void doitRetournerErreurQuandTokenInvalideOuManquantPourGetMe() throws Exception {
        when(userAppService.getMe(null))
                .thenThrow(new AuthenticationException(HttpStatus.UNAUTHORIZED, "token.invalid"));

        mockMvc.perform(get("/user/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}