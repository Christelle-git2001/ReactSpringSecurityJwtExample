package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Gestionnaire;
import com.lacouf.rsbjwt.model.Professeur;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.security.JwtTokenProvider;
import com.lacouf.rsbjwt.service.dto.LoginDTO;
import com.lacouf.rsbjwt.service.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAppServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    private UserAppService userAppService;

    @Mock
    private UserAppRepository userAppRepository;

    @Mock
    private GestionnaireRepository gestionnaireRepository;
    @Mock
    private EtudiantRepository etudiantRepository;
    @Mock
    private EmployeurRepository employeurRepository;
    @Mock
    private ProfesseurRepository professeurRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userAppService = new UserAppService(
                authenticationManager,
                jwtTokenProvider,
                userAppRepository,
                gestionnaireRepository,
                employeurRepository,
                etudiantRepository,
                professeurRepository
        );
    }

    @Test
    void authentificationRetourneToken() {
        LoginDTO loginDTO = new LoginDTO("christelle@gmail.com", "111111");

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(userAppRepository.findUserAppByEmail(loginDTO.email()))
                .thenReturn(java.util.Optional.of(new Etudiant()));

        when(jwtTokenProvider.generateToken(authentication))
                .thenReturn("fake-jwt-token");

        String token = userAppService.authenticateUser(loginDTO);

        assertEquals("fake-jwt-token", token);
    }

    @Test
    void doitLeverExceptionQuandUtilisateurInexistantLorsDeAuthentification() {
        LoginDTO loginDTO = new LoginDTO("inconnu@gmail.com", "111111");

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(userAppRepository.findUserAppByEmail(loginDTO.email()))
                .thenReturn(java.util.Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> userAppService.authenticateUser(loginDTO))
                .isInstanceOf(com.lacouf.rsbjwt.security.exception.UserNotFoundException.class);
    }

    @Test
    void doitRetournerUserDtoPourEtudiantLorsDeGetMe() {
        String token = "Bearer fake-token";
        String email = "etudiant@test.com";

        com.lacouf.rsbjwt.model.Etudiant etudiant = new com.lacouf.rsbjwt.model.Etudiant();
        etudiant.setId(1L);

        when(jwtTokenProvider.getEmailFromJWT("fake-token")).thenReturn(email);
        when(userAppRepository.findUserAppByEmail(email)).thenReturn(java.util.Optional.of(etudiant));

    }
    @Test
    void doitRetournerGestionnaireDtoLorsDeGetMe() {
        String token = "Bearer fake-token";
        String email = "gestionnaire@test.com";

        Gestionnaire gestionnaire = new Gestionnaire(1L,"Laurent","losange","Laurent.Losange@hotmail.com","5858","458-458-4584");
        gestionnaire.setId(1L);

        when(jwtTokenProvider.getEmailFromJWT("fake-token")).thenReturn(email);
        when(userAppRepository.findUserAppByEmail(email)).thenReturn(Optional.of(gestionnaire));
        when(gestionnaireRepository.findById(1L)).thenReturn(Optional.of(gestionnaire));

        UserDTO result = userAppService.getMe(token);

        org.assertj.core.api.Assertions.assertThat(result).isNotNull();
        verify(gestionnaireRepository).findById(1L);
    }

    @Test
    void doitRetournerEtudiantDtoLorsDeGetMe() {
        String token = "fake-token"; // Test sans le préfixe Bearer
        String email = "etudiant@test.com";

        Etudiant etudiant = Etudiant.builder()
                .firstName("Steve")
                .lastName("Jean")
                .phoneNumber("514-327-9021")
                .matricule("214578")
                .department(Departement.INFORMATIQUE)
                .email("steveJean@gmail.com")
                .password("Passewod123")
                .build();        etudiant.setId(2L);

        when(jwtTokenProvider.getEmailFromJWT("fake-token")).thenReturn(email);
        when(userAppRepository.findUserAppByEmail(email)).thenReturn(Optional.of(etudiant));
        when(etudiantRepository.findById(2L)).thenReturn(Optional.of(etudiant));

        UserDTO result = userAppService.getMe(token);

        org.assertj.core.api.Assertions.assertThat(result).isNotNull();
        verify(etudiantRepository).findById(2L);
    }

    @Test
    void doitRetournerEmployeurDtoLorsDeGetMe() {
        String token = "Bearer fake-token";
        String email = "employeur@test.com";

        Employeur employeur = Employeur.builder()
                .firstName("Gerard")
                .lastName("Robert")
                .town("Mercier")
                .phone("165-685-4569")
                .email("Gerard.Robert@hotmail.com")
                .businessName("Gerard inc")
                .businessSector(SecteurActivite.AEROSPATIAL)
                .password("Losange12%")
                .build();        employeur.setId(3L);

        when(jwtTokenProvider.getEmailFromJWT("fake-token")).thenReturn(email);
        when(userAppRepository.findUserAppByEmail(email)).thenReturn(Optional.of(employeur));
        when(employeurRepository.findById(3L)).thenReturn(Optional.of(employeur));

        UserDTO result = userAppService.getMe(token);

        org.assertj.core.api.Assertions.assertThat(result).isNotNull();
        verify(employeurRepository).findById(3L);
    }

    @Test
    void doitRetournerProfesseurDtoLorsDeGetMe() {
        String token = "Bearer fake-token";
        String email = "professeur@test.com";

        Professeur professeur = Professeur.builder()
                .firstName("Gerard")
                .lastName("Robert")
                .email("Gerard.Robert@hotmail.com")
                .password("Losange12%")
                .department(Departement.INFORMATIQUE)
                .matricule("8555885")
                .build();
        professeur.setId(4L);

        when(jwtTokenProvider.getEmailFromJWT("fake-token")).thenReturn(email);
        when(userAppRepository.findUserAppByEmail(email)).thenReturn(Optional.of(professeur));
        when(professeurRepository.findById(4L)).thenReturn(Optional.of(professeur));

        UserDTO result = userAppService.getMe(token);

        org.assertj.core.api.Assertions.assertThat(result).isNotNull();
        verify(professeurRepository).findById(4L);
    }

    @Test
    void doitLeverExceptionQuandUtilisateurInexistantLorsDeGetMe() {
        String token = "Bearer fake-token";
        String email = "inconnu@test.com";

        when(jwtTokenProvider.getEmailFromJWT("fake-token")).thenReturn(email);
        when(userAppRepository.findUserAppByEmail(email)).thenReturn(Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> userAppService.getMe(token))
                .isInstanceOf(com.lacouf.rsbjwt.security.exception.UserNotFoundException.class);
    }

}