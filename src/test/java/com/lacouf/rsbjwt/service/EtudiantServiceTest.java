package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.EmailExistantException;
import com.lacouf.rsbjwt.Exception.MatriculeExistantException;
import com.lacouf.rsbjwt.Exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EtudiantServiceTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private UserAppRepository userAppRepository;

    @InjectMocks
    private EtudiantService etudiantService;

    @Mock
    private PasswordEncoder passwordEncoder;

    InscriptionEtudiantDTO inscriptionEtudiantDTO;
    Etudiant etudiant;

    @BeforeEach
    void init() {
        inscriptionEtudiantDTO = new InscriptionEtudiantDTO(
                "Christelle",
                "Altineus",
                "438-297-8191",
                "christelle@gmail.com",
                "2226252",
                "111111",
                "111111"
        );

        etudiant = Etudiant.builder()
                .firstName("Steve")
                .lastName("Jean")
                .phoneNumber("514-327-9021")
                .matricule("214578")
                .email("steveJean@gmail.com")
                .password("Passewod123")
                .build();
        etudiant.setId(1L);
    }

        @Test
        void doitCreerCompteEtudiant() throws Exception{

            when(passwordEncoder.encode("111111"))
                    .thenReturn("motDePasseEncode");

            when(etudiantRepository.save(any(Etudiant.class)))
                    .thenReturn(etudiant);

            EtudiantDTO result =
                    etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO);

            verify(etudiantRepository, times(1))
                    .save(any(Etudiant.class));

            assertThat(result)
                    .isNotNull()
                    .returns(etudiant.getFirstName(), EtudiantDTO::firstName)
                    .returns(etudiant.getEmail(), EtudiantDTO::email)
                    .returns(etudiant.getMatricule(), EtudiantDTO::matricule)
                    .returns(etudiant.getPhoneNumber(), EtudiantDTO::phoneNumber);



        }

    @Test
    void doitLancerExceptionEmailExistant() {

        when(userAppRepository.findUserAppByEmail(anyString()))
                .thenReturn(Optional.of(etudiant));

        assertThatThrownBy(() ->
                etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO))
                .isInstanceOf(EmailExistantException.class);

        verify(etudiantRepository, never())
                .save(any(Etudiant.class));
    }

    @Test
    void doitLancerExceptionMotDePasseDifferent() {

        inscriptionEtudiantDTO = new InscriptionEtudiantDTO(
                "Christelle",
                "Altineus",
                "438-297-8191",
                "christelle@gmail.com",
                "2226252",
                "111111",
                "222222"
        );

        assertThatThrownBy(() ->
                etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO))
                .isInstanceOf(MotDePasseNonCorrespondantException.class);

        verify(etudiantRepository, never())
                .save(any(Etudiant.class));
    }

    @Test
    void doitLancerExceptionMatriculeExistant() {

        when(userAppRepository.findUserAppByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(etudiantRepository.findByMatricule(anyString()))
                .thenReturn(Optional.of(etudiant));

        assertThatThrownBy(() ->
                etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO))
                .isInstanceOf(MatriculeExistantException.class);

        verify(etudiantRepository, never())
                .save(any(Etudiant.class));
    }



    }






