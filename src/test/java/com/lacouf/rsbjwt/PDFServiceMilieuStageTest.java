package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Evaluation.*;
import com.lacouf.rsbjwt.repository.CandidatureRepository;
import com.lacouf.rsbjwt.repository.ContratRepository;
import com.lacouf.rsbjwt.repository.EvaluationMillieuStageRepository;
import com.lacouf.rsbjwt.repository.EvaluationStagiaireRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PDFServiceMilieuStageTest {

    @Mock
    private ContratRepository contratRepository;

    @Mock
    private CandidatureRepository candidatureRepository;

    @Mock
    private EvaluationStagiaireRepository evaluationStagiaireRepository;

    @Mock
    private EvaluationMillieuStageRepository evaluationMillieuStageRepository;

    private PDFService pdfService;

    @BeforeEach
    void setUp() {
        pdfService = new PDFService(
                contratRepository,
                evaluationStagiaireRepository,
                evaluationMillieuStageRepository
        );
    }

    @Test
    void genererPdfEvaluationMilieuStageSurBureau() throws Exception {
        // 1. Préparation des données fictives
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName("Jean");
        etudiant.setLastName("Tremblay");

        Employeur employeur = new Employeur();
        employeur.setBusinessName("Technologies Acme Inc.");
        employeur.setTown("Montréal");

        OffreDeStage offre = new OffreDeStage();
        offre.setEmployeur(employeur);
        offre.setStartDate(LocalDate.of(2026, 5, 11));
        offre.setEndDate(LocalDate.of(2026, 8, 14));
        offre.setSalary(24.50);

        Candidature candidature = new Candidature();
        candidature.setId(1L);
        candidature.setEtudiant(etudiant);
        candidature.setOffreDeStage(offre);

        SuperviseurInfo superviseur = new SuperviseurInfo(
                "Marie",
                "Lavoie",
                "Cheffe d'équipe développement",
                "514-555-0199"
        );

        MilieuStageScores scores = MilieuStageScores.builder()
                .tacheConformeScore(4L)
                .integrationScore(4L)
                .tempsSupervisionSuffisantScore(3L)
                .tempsSupervision(List.of(6L, 4L, 3L))
                .environmentScore(4L)
                .climatScore(4L)
                .salaireSuffisentScore(3L)
                .communicationScore(4L)
                .equipementScore(4L)
                .volumeTravailScore(3L)
                .commentaire("Très bon milieu d'apprentissage avec un encadrement structuré dès la première semaine.")
                .build();

        ObservationGeneral observationGeneral = new ObservationGeneral(
                1L,
                2L,
                true,
                List.of(
                        new QuartTravail("08:30", "16:30"),
                        new QuartTravail("09:00", "17:00")
                )
        );

        EvaluationMillieuStage evaluation = EvaluationMillieuStage.builder()
                .id(1L)
                .candidature(candidature)
                .superviseurInfo(superviseur)
                .millieuStageScores(scores)
                .observationGeneral(observationGeneral)
                .build();

        when(evaluationMillieuStageRepository.findEvaluationMillieuStageByCandidature_Id(1L))
                .thenReturn(Optional.of(evaluation));

        // 2. Appel de la méthode PDFService
        Resource pdfResource = pdfService.createEvaluationMilieuStage("fr", 1L);
        assertNotNull(pdfResource);

        byte[] pdfBytes = pdfResource.getInputStream().readAllBytes();
        assertTrue(pdfBytes.length > 0);

        // 3. Sauvegarde directe sur le bureau (Windows / macOS / Linux)
        Path desktopPath = Paths.get(System.getProperty("user.home"), "Desktop", "Evaluation_Milieu_Stage_Test.pdf");
        Files.write(desktopPath, pdfBytes);

        System.out.println("PDF généré avec succès sur le bureau : " + desktopPath.toAbsolutePath());
    }
}