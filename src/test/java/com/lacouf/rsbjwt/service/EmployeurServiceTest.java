package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Exceptions.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.model.Exceptions.EmailExistantException;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurInscriptionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class EmployeurServiceTest {

    @MockitoBean
    private EmployeurRepository employeurRepository;

    @MockitoBean
    private UserAppRepository userAppRepository;

    @InjectMocks
    private EmployeurService employeurService;

    EmployeurInscriptionDTO employeurInscriptionDTO;
    Employeur employeur;



    @BeforeEach
    void init(){
        employeurInscriptionDTO = new EmployeurInscriptionDTO(
                "Gerard",
                "Robert",
                "165-685-4569",
                "Gerard.Robert@hotmail.com",
                "Mercier",
                "Gerard inc",
                SecteurActivite.AEROSPATIAL,
                "Startup",
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
                .businessType("Startup")
                .businessSector(SecteurActivite.AEROSPATIAL)
                .password("Losange12%")
                .build();
    }

    @Test
    void employeurInscritCorrectement() throws Exception {
        when(employeurRepository.save(any(Employeur.class))).thenReturn(employeur);

        EmployeurDTO result = employeurService.creeCompteEmployeur(employeurInscriptionDTO);

        verify(employeurRepository, times(1)).save(any(Employeur.class));
        assertThat(result)
                .isNotNull()
                .returns(employeur.getFirstName(), EmployeurDTO::firstName)
                .returns(employeur.getEmail(), EmployeurDTO::email)
                .returns(employeur.getBusinessName(), EmployeurDTO::businessName)
                .returns(employeur.getBusinessSector(), EmployeurDTO::businessSector);
    }

    @Test
    void employeurInscriptionEmailExistant() {
        when(userAppRepository.findUserAppByEmail(anyString())).thenReturn(Optional.of(employeur));

        assertThatThrownBy(() -> employeurService.creeCompteEmployeur(employeurInscriptionDTO))
                .isInstanceOf(EmailExistantException.class);

        verify(employeurRepository, never()).save(any(Employeur.class));
    }

    @Test
    void employeurInscriptionMotDePasseDifferent() {
        employeurInscriptionDTO = new EmployeurInscriptionDTO(
                "Gerard",
                "Robert",
                "165-685-4569",
                "Gerard.Robert@hotmail.com",
                "Mercier",
                "Gerard inc",
                SecteurActivite.AEROSPATIAL,
                "Startup",
                "Losange12%",
                "Carre12%"
        );

        assertThatThrownBy(() -> employeurService.creeCompteEmployeur(employeurInscriptionDTO))
                .isInstanceOf(MotDePasseNonCorrespondantException.class);

        verify(employeurRepository, never()).save(any(Employeur.class));
    }


}
