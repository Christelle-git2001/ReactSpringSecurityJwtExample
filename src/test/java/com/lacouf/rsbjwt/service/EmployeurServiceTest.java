package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.NumeroTelephoneExistantException;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.Exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.Exception.EmailExistantException;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEmployeurDTO;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class EmployeurServiceTest {

    @Mock
    private EmployeurRepository employeurRepository;

    @Mock
    private UserAppRepository userAppRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeurService employeurService;

    InscriptionEmployeurDTO inscriptionEmployeurDTO;
    Employeur employeur;



    @BeforeEach
    void init(){
        inscriptionEmployeurDTO = new InscriptionEmployeurDTO(
                "Gerard",
                "Robert",
                "165-685-4569",
                "Gerard.Robert@hotmail.com",
                "Mercier",
                "Gerard inc",
                SecteurActivite.AEROSPATIAL,
                "Losange12%",
                "Losange12%"
        );
        employeur = Employeur.builder()
                .firstName("Gerard")
                .lastName("Robert")
                .town("Mercier")
                .phone("165-685-4569")
                .email("Gerard.Robert@hotmail.com")
                .businessName("Gerard inc")
                .businessSector(SecteurActivite.AEROSPATIAL)
                .password("Losange12%")
                .build();

        employeur.setId(1L);
    }

    @Test
    void doitCreerCompteEmployeur() throws Exception {
        when(employeurRepository.save(any(Employeur.class))).thenReturn(employeur);

        EmployeurDTO result = employeurService.creeCompteEmployeur(inscriptionEmployeurDTO);

        verify(employeurRepository, times(1)).save(any(Employeur.class));
        assertThat(result)
                .isNotNull()
                .returns(employeur.getFirstName(), EmployeurDTO::firstName)
                .returns(employeur.getEmail(), EmployeurDTO::email)
                .returns(employeur.getBusinessName(), EmployeurDTO::businessName)
                .returns(employeur.getBusinessSector(), EmployeurDTO::businessSector);
    }

    @Test
    void doitLancerExceptionEmailExistant() {
        when(userAppRepository.findUserAppByEmail(anyString())).thenReturn(Optional.of(employeur));

        assertThatThrownBy(() -> employeurService.creeCompteEmployeur(inscriptionEmployeurDTO))
                .isInstanceOf(EmailExistantException.class);

        verify(employeurRepository, never()).save(any(Employeur.class));
    }

    @Test
    void doitLancerExceptionMotDePasseDifferent() {
        inscriptionEmployeurDTO = new InscriptionEmployeurDTO(
                "Gerard",
                "Robert",
                "165-685-4569",
                "Gerard.Robert@hotmail.com",
                "Mercier",
                "Gerard inc",
                SecteurActivite.AEROSPATIAL,
                "Losange12%",
                "Carre12%"
        );

        assertThatThrownBy(() -> employeurService.creeCompteEmployeur(inscriptionEmployeurDTO))
                .isInstanceOf(MotDePasseNonCorrespondantException.class);

        verify(employeurRepository, never()).save(any(Employeur.class));
    }

    @Test
    void doitLancerExceptionTelephoneExistant() {
        when(userAppRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(employeur));

        assertThatThrownBy(() -> employeurService.creeCompteEmployeur(inscriptionEmployeurDTO))
                .isInstanceOf(NumeroTelephoneExistantException.class);

        verify(employeurRepository, never()).save(any(Employeur.class));
    }


}
