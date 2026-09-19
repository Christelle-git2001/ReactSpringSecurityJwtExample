package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.DepartementInvalideException;
import com.lacouf.rsbjwt.Exception.EmailExistantException;
import com.lacouf.rsbjwt.Exception.MatriculeExistantException;
import com.lacouf.rsbjwt.Exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Professeur;
import com.lacouf.rsbjwt.repository.ProfesseurRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.InscriptionProfesseurDTO;
import com.lacouf.rsbjwt.service.dto.ProfesseurDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

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

    @InjectMocks
    private ProfesseurService professeurService ;

    InscriptionProfesseurDTO inscriptionProfesseurDTO;
    Professeur professeur;

    @BeforeEach
    void init() {
        inscriptionProfesseurDTO = new InscriptionProfesseurDTO(
                "Carole",
                "Test",
                "test@gmail.com",
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

        professeur.setId(1L);
    }

    @Test
    void doitInscrireProfesseur() throws Exception {

        when(passwordEncoder.encode("111111"))
                .thenReturn("motDePasseEncode");

        when(professeurRepository.save(any(Professeur.class)))
                .thenReturn(professeur);

        ProfesseurDTO result =
                professeurService.inscrireProfesseur(inscriptionProfesseurDTO);

        verify(professeurRepository, times(1))
                .save(any(Professeur.class));

        assertThat(result)
                .isNotNull()
                .returns(professeur.getFirstName(), ProfesseurDTO::firstName)
                .returns(professeur.getEmail(), ProfesseurDTO::email)
                .returns(professeur.getMatricule(), ProfesseurDTO::matricule)
                .returns(professeur.getDepartment().getLabel(), ProfesseurDTO::department);
    }

    @Test
    void doitLancerExceptionEmailExistant() {

        when(userAppRepository.findUserAppByEmail(anyString()))
                .thenReturn(Optional.of(professeur));

        assertThatThrownBy(() ->
                professeurService.inscrireProfesseur(inscriptionProfesseurDTO))
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
                "222222", // Mot de passe de confirmation différent
                "1234567",
                "123-123-1234",
                "INFORMATIQUE"
        );
        assertThatThrownBy(() -> professeurService.inscrireProfesseur(dtoMdpIncorrect))
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
                professeurService.inscrireProfesseur(inscriptionProfesseurDTO))
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

        assertThatThrownBy(() -> professeurService.inscrireProfesseur(dtoDepartementInvalide))
                .isInstanceOf(DepartementInvalideException.class);

        verify(professeurRepository, never()).save(any(Professeur.class));
    }
        

}


