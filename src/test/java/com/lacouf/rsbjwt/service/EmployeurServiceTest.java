package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Exceptions.ConfirmationMotDePasseEchouer;
import com.lacouf.rsbjwt.model.Exceptions.EmployeurExistant;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
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

    @InjectMocks
    private EmployeurService employeurService;

    EmployeurInscriptionDTO employeurInscriptionDTO;
    Employeur employeur;



    @BeforeEach
    void init(){
        employeurInscriptionDTO = EmployeurInscriptionDTO.builder()
                .prenom("Gerard")
                .nom("Robert")
                .ville("Mercier")
                .telephone("165-685-4569")
                .email("Gerard.Robert@hotmail.com")
                .nomEntreprise("Gerard inc")
                .typeEntreprise("Startup")
                .secteurActivite(SecteurActivite.AEROSPATIAL)
                .password("Losange12%")
                .passwordConfirmation("Losange12%")
                .build();
        employeur = Employeur.builder()
                .prenom("Gerard")
                .nom("Robert")
                .ville("Mercier")
                .telephone("165-685-4569")
                .email("Gerard.Robert@hotmail.com")
                .nomEntreprise("Gerard inc")
                .typeEntreprise("Startup")
                .secteurActivite(SecteurActivite.AEROSPATIAL)
                .password("Losange12%")
                .build();
    }

    @Test
    void employeurInscritCorrectement() throws Exception {
        when(employeurRepository.save(any(Employeur.class))).thenReturn(employeur);

        EmployeurDTO result = employeurService.inscription(employeurInscriptionDTO);

        verify(employeurRepository, times(1)).save(any(Employeur.class));
        assertThat(result)
                .isNotNull()
                .returns(employeur.getFirstName(), EmployeurDTO::getPrenom)
                .returns(employeur.getEmail(), EmployeurDTO::getEmail)
                .returns(employeur.getNomEntreprise(), EmployeurDTO::getNomEntreprise)
                .returns(employeur.getSecteurActivite(), EmployeurDTO::getSecteurActivite);
    }

    @Test
    void employeurInscriptionEmailExistant() {
        when(employeurRepository.existeParEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> employeurService.inscription(employeurInscriptionDTO))
                .isInstanceOf(EmployeurExistant.class);

        verify(employeurRepository, never()).save(any(Employeur.class));
    }

    @Test
    void employeurInscriptionMotDePasseDifferent() {
        employeurInscriptionDTO.setPasswordConfirmation("carre12&");

        assertThatThrownBy(() -> employeurService.inscription(employeurInscriptionDTO))
                .isInstanceOf(ConfirmationMotDePasseEchouer.class);

        verify(employeurRepository, never()).save(any(Employeur.class));
    }


}
