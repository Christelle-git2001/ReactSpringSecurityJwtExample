package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Professeur;
import com.lacouf.rsbjwt.model.UserApp;
import com.lacouf.rsbjwt.repository.CandidatureRepository;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.ProfesseurRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionProfesseurDTO;
import com.lacouf.rsbjwt.service.dto.ProfesseurDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;


import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfesseurServiceTest {

    @Mock
    private ProfesseurRepository professeurRepository ;

    @Mock
    private UserAppRepository userAppRepository;

    @Mock
    private PasswordEncoder passwordEncoder ;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private CandidatureRepository candidatureRepository;

    @InjectMocks
    private ProfesseurService professeurService ;

    InscriptionProfesseurDTO inscriptionProfesseurDTO;
    Professeur professeur;

    @BeforeEach
    void init() {
        inscriptionProfesseurDTO = new InscriptionProfesseurDTO(
                "Carole",
                "Test",
                "carole.test@gmail.com",
                "111111",
                "111111",
                "1234567",
                "123-123-1234",
                "INFORMATIQUE"
        );

        professeur = Professeur.builder()
                .firstName("Carole")
                .lastName("Test")
                .email("carole.test@gmail.com")
                .password("motDePasseEncode")
                .matricule("1234567")
                .phoneNumber("123-123-1234")
                .department(Departement.INFORMATIQUE)
                .build();
        professeur.setId(1L);
    }
    @BeforeEach
    void initialiserUtilisateurConnecte() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "carole.test@gmail.com",
                        null
                )
        );
    }

    @AfterEach
    void nettoyerSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doitCreerCompteProfesseur() throws Exception {

        when(passwordEncoder.encode("111111")).thenReturn("motDePasseEncode");
        when(professeurRepository.save(any(Professeur.class))).thenReturn(professeur);

        ProfesseurDTO result = professeurService.creerCompteProfesseur(inscriptionProfesseurDTO);

        assertThat(result.firstName()).isEqualTo(inscriptionProfesseurDTO.firstName());
        assertThat(result.email()).isEqualTo(inscriptionProfesseurDTO.email());
        assertThat(result.matricule()).isEqualTo(inscriptionProfesseurDTO.matricule());

        verify(passwordEncoder).encode("111111");

    }

    @Test
    void doitLancerExceptionEmailExistant() {

        when(userAppRepository.findUserAppByEmail(anyString()))
                .thenReturn(Optional.of(professeur));

        assertThatThrownBy(() ->
                professeurService.creerCompteProfesseur(inscriptionProfesseurDTO))
                .isInstanceOf(EmailExistantException.class);

        verify(professeurRepository, never())
                .save(any(Professeur.class));
    }

    @Test
    void doitLancerExceptionMotDePasseNonCorrespondant() {

        InscriptionProfesseurDTO dtoMdpIncorrect = new InscriptionProfesseurDTO(
                "Carole",
                "Test",
                "carole.test@gmail.com",
                "111111",
                "222222",
                "1234567",
                "123-123-1234",
                "INFORMATIQUE"
        );
        assertThatThrownBy(() -> professeurService.creerCompteProfesseur(dtoMdpIncorrect))
                .isInstanceOf(MotDePasseNonCorrespondantException.class);

        verify(professeurRepository, never()).save(any(Professeur.class));
    }

    @Test
    void doitLancerExceptionMatriculeExistant() {

        when(userAppRepository.findUserAppByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(professeurRepository.findByMatricule(anyString()))
                .thenReturn(Optional.of(professeur));

        assertThatThrownBy(() ->
                professeurService.creerCompteProfesseur(inscriptionProfesseurDTO))
                .isInstanceOf(MatriculeExistantException.class);

        verify(professeurRepository, never())
                .save(any(Professeur.class));
    }

    @Test
    void doitLancerExceptionDepartementInvalide() {

        InscriptionProfesseurDTO dtoDepartementInvalide = new InscriptionProfesseurDTO(
                "Carole",
                "Test",
                "carole.test@gmail.com",
                "111111",
                "111111",
                "1234567",
                "123-123-1234",
                "DEPARTEMENT_INEXISTANT"
        );

        assertThatThrownBy(() -> professeurService.creerCompteProfesseur(dtoDepartementInvalide))
                .isInstanceOf(DepartementInvalideException.class);

        verify(professeurRepository, never()).save(any(Professeur.class));
    }

    @Test
    void doitLancerExceptionNumeroTelephoneExistant() {
        when(userAppRepository.findByPhoneNumber(anyString()))
                .thenReturn(Optional.of(professeur));

        assertThatThrownBy(() ->
                professeurService.creerCompteProfesseur(inscriptionProfesseurDTO))
                .isInstanceOf(NumeroTelephoneExistantException.class);

        verify(professeurRepository, never())
                .save(any(Professeur.class));
    }

    @Test
    void doitRetournerLesEtudiantsAssignes() throws Exception {

        when(userAppRepository.findUserAppByEmail("carole.test@gmail.com"))
                .thenReturn(Optional.of(professeur));

        when(etudiantRepository.findAllByProfesseurId(1L))
                .thenReturn(List.of());

        var resultat = professeurService.getEtudiantsAssignes();

        assertThat(resultat).isEmpty();

        verify(userAppRepository)
                .findUserAppByEmail("carole.test@gmail.com");

        verify(etudiantRepository)
                .findAllByProfesseurId(1L);
    }

    @Test
    void doitRetournerListeVideSiAucunEtudiantAssigne()
            throws Exception {

        when(userAppRepository.findUserAppByEmail("carole.test@gmail.com"))
                .thenReturn(Optional.of(professeur));

        when(etudiantRepository.findAllByProfesseurId(1L))
                .thenReturn(List.of());

        var resultat = professeurService.getEtudiantsAssignes();

        assertThat(resultat).isNotNull().isEmpty();

        verify(etudiantRepository)
                .findAllByProfesseurId(1L);
    }

    @Test
    void doitLeverExceptionSiUtilisateurIntrouvable() {

        when(userAppRepository.findUserAppByEmail("carole.test@gmail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                professeurService.getEtudiantsAssignes()
        ).isInstanceOf(UtilisateurIntrouvableException.class);

        verify(etudiantRepository, never())
                .findAllByProfesseurId(anyLong());
    }

    @Test
    void doitLeverExceptionSiUtilisateurNestPasProfesseur() {

        UserApp utilisateur = mock(UserApp.class);

        when(userAppRepository.findUserAppByEmail("carole.test@gmail.com"))
                .thenReturn(Optional.of(utilisateur));

        assertThatThrownBy(() ->
                professeurService.getEtudiantsAssignes()
        ).isInstanceOf(UtilisateurIntrouvableException.class);

        verify(etudiantRepository, never())
                .findAllByProfesseurId(anyLong());
    }

    @Test
    void doitRetournerLesCandidaturesDUnEtudiant() throws Exception {
        Long etudiantId = 1L;

        Candidature candidature = Candidature.builder()
                .build();
        candidature.setId(1L);

        when(etudiantRepository.findById(etudiantId))
                .thenReturn(Optional.of(mock(
                        com.lacouf.rsbjwt.model.Etudiant.class
                )));

        when(candidatureRepository.findAllByEtudiantId(etudiantId))
                .thenReturn(List.of(candidature));

        List<CandidatureDTO> resultat =
                professeurService.getCandidaturesEtudiant(etudiantId);

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).id()).isEqualTo(1L);

        verify(etudiantRepository).findById(etudiantId);
        verify(candidatureRepository).findAllByEtudiantId(etudiantId);
    }

    @Test
    void doitRetournerListeVideSiEtudiantNAucuneCandidature() throws Exception {
        Long etudiantId = 1L;

        when(etudiantRepository.findById(etudiantId))
                .thenReturn(Optional.of(
                        mock(com.lacouf.rsbjwt.model.Etudiant.class)
                ));

        when(candidatureRepository.findAllByEtudiantId(etudiantId))
                .thenReturn(List.of());

        List<CandidatureDTO> resultat =
                professeurService.getCandidaturesEtudiant(etudiantId);

        assertThat(resultat).isNotNull().isEmpty();

        verify(etudiantRepository).findById(etudiantId);
        verify(candidatureRepository).findAllByEtudiantId(etudiantId);
    }

    @Test
    void doitLeverExceptionSiEtudiantIntrouvablePourCandidatures() {
        Long etudiantId = 99L;

        when(etudiantRepository.findById(etudiantId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                professeurService.getCandidaturesEtudiant(etudiantId)
        ).isInstanceOf(UtilisateurIntrouvableException.class);

        verify(etudiantRepository).findById(etudiantId);

        verify(candidatureRepository, never())
                .findAllByEtudiantId(anyLong());
    }



}


