package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.ContratIntrouvableException;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.repository.ContratRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PDFServiceTest {

    @Mock
    private ContratRepository contratRepository;

    @InjectMocks
    private PDFService pdfService;

    private Contrat contrat;

    @BeforeEach
    void setUp() {
        Gestionnaire gestionnaire = Gestionnaire.builder()
                .firstName("Pascal")
                .lastName("Belmont")
                .email("Pascal.Belmont@hotmail.com")
                .password("NoNeedButStillNeed")
                .phoneNumber("565-985-5858")
                .build();
        gestionnaire.setId(1L);

        Employeur employeur = Employeur.builder()
                .firstName("Roger")
                .lastName("Malinois")
                .phone("450-874-6586")
                .email("Roger.Malinois@hotmail.com")
                .town("Laval")
                .businessName("Malinois inc")
                .businessSector(SecteurActivite.INFORMATIQUE)
                .password("NoNeedButStillNeed")
                .build();
        employeur.setId(1L);

        Etudiant etudiant = Etudiant.builder()
                .firstName("Adrien")
                .lastName("Agreste")
                .email("Adrien@gmail.com")
                .phoneNumber("450-716-1216")
                .department(Departement.INFORMATIQUE)
                .matricule("1111111")
                .password("NoNeedButStillNeed")
                .build();
        etudiant.setId(1L);

        OffreDeStage offre = new OffreDeStage();
        offre.setTitle("Développeur Web Junior");
        offre.setDescription("Développement front-end et back-end, revue de code, CI/CD.");
        offre.setSalary(22.50);
        offre.setStartDate(LocalDate.now().plusDays(10));
        offre.setEndDate(LocalDate.now().plusMonths(3));
        offre.setEmployeur(employeur);

        Candidature candidature = Candidature.builder()
                .etudiant(etudiant)
                .offreDeStage(offre)
                .build();

        contrat = Contrat.builder()
                .candidature(candidature)
                .gestionnaire(gestionnaire)
                .build();
        contrat.setId(1L);
    }

    @Test
    void doitGenererPdfContratAvecSucces() throws ContratIntrouvableException {
        when(contratRepository.findById(1L)).thenReturn(Optional.of(contrat));

        Resource result = pdfService.createContratPdf(1L, "fr");

        assertThat(result).isNotNull();
        assertThat(result.exists()).isTrue();
    }

    @Test
    void doitLeverExceptionSiContratIntrouvablePourPdf() {
        when(contratRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pdfService.createContratPdf(99L, "fr"))
                .isInstanceOf(ContratIntrouvableException.class);
    }
}