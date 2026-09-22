package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.service.dto.SecteurEmployeurDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GestionnaireServiceTest {

    private GestionnaireService gestionnaireService;

    @BeforeEach
    void setUp() {
        gestionnaireService = new GestionnaireService();
    }

    @Test
    void doitMapperEtRetournerTousLesSecteurs() {
        List<SecteurEmployeurDTO> result = gestionnaireService.getAllSecteurs();

        assertThat(result)
                .hasSize(SecteurActivite.values().length)
                .extracting(SecteurEmployeurDTO::label)
                .contains(
                        SecteurActivite.INFORMATIQUE.getLabel(),
                        SecteurActivite.PHARMACEUTIQUE.getLabel(),
                        SecteurActivite.FINANCE.getLabel()
                );
    }
}