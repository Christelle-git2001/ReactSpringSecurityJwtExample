package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
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

    @InjectMocks
    private DonneesEmployeurService service;

    @Test
    void compterEmployeurRetourneLeNombreTotalDEmployeurs() {
        when(employeurRepository.findAll()).thenReturn(List.of(new Employeur(), new Employeur(), new Employeur()));

        Long resultat = service.compterEmployeur();

        assertThat(resultat).isEqualTo(3L);
        verify(employeurRepository).findAll();
    }

    @Test
    void compterEmployeurRetourneZeroQuandAucunEmployeur() {
        when(employeurRepository.findAll()).thenReturn(List.of());

        Long resultat = service.compterEmployeur();

        assertThat(resultat).isEqualTo(0L);
        verify(employeurRepository).findAll();
    }
}