package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.Saison;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
import com.lacouf.rsbjwt.service.SessionService;
import com.lacouf.rsbjwt.service.dto.session.SessionDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonneesEmployeurServiceTest {

    @Mock
    private EmployeurRepository employeurRepository;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private DonneesEmployeurService service;

    @Test
    void compterEmployeurSessionActuelleRetourneLeNombreDEmployeursFiltres() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        Employeur e1 = new Employeur();
        Employeur e2 = new Employeur();
        List<Employeur> tous = List.of(e1, e2);

        when(employeurRepository.findAll()).thenReturn(tous);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerUtilisateursParSession(tous, Saison.AUTOMNE, 2026))
                .thenReturn(List.of(e1));

        Long resultat = service.compterEmployeurSessionActuelle();

        assertThat(resultat).isEqualTo(1L);
        verify(employeurRepository).findAll();
        verify(sessionService).filtrerUtilisateursParSession(tous, Saison.AUTOMNE, 2026);
    }

    @Test
    void compterEmployeurSessionActuelleRetourneZeroQuandAucunEmployeur() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        List<Employeur> vide = List.of();

        when(employeurRepository.findAll()).thenReturn(vide);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerUtilisateursParSession(vide, Saison.AUTOMNE, 2026))
                .thenReturn(vide);

        Long resultat = service.compterEmployeurSessionActuelle();

        assertThat(resultat).isEqualTo(0L);
        verify(employeurRepository).findAll();
        verify(sessionService).filtrerUtilisateursParSession(vide, Saison.AUTOMNE, 2026);
    }
}