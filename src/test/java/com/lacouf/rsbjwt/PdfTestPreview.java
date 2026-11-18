package com.lacouf.rsbjwt;
import com.lacouf.rsbjwt.exception.EvaluationStagiaireIntrouvable;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.ChoixAccueil;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Evaluation.*;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.repository.EvaluationStagiaireRepository;
import com.lacouf.rsbjwt.service.PDFService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PdfTestPreview {

    @Mock
    private EvaluationStagiaireRepository evaluationStagiaireRepository;

    @InjectMocks
    private PDFService pdfService;

    @Test
    public void generateTestPdf() throws IOException, EvaluationStagiaireIntrouvable {
        Professeur professeur = new Professeur();
        professeur.setFirstName("Jean");
        professeur.setLastName("Dupont");
        professeur.setEmail("jean.dupont@cegep.qc.ca");

        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName("Alice");
        etudiant.setLastName("Tremblay");
        etudiant.setCredentials(Credentials.builder().email("alice.tremblay@etu.cegep.qc.ca").password("NoNeed").build());
        etudiant.setDepartment(Departement.INFORMATIQUE);
        etudiant.setProfesseur(professeur);

        Employeur employeur = new Employeur();
        employeur.setFirstName("Marc");
        employeur.setLastName("Bergeron");
        employeur.setBusinessName("TechSolutions Inc.");
        employeur.setCredentials(Credentials.builder().email("contact@techsolutions.ca").password("NoNeed").build());
        employeur.setPhoneNumber("514-555-0199");

        OffreDeStage offreDeStage = new OffreDeStage();
        offreDeStage.setTitle("Full-Stack Developer Intern");
        offreDeStage.setEmployeur(employeur);

        Candidature candidature = new Candidature();
        candidature.setId(1L);
        candidature.setEtudiant(etudiant);
        candidature.setOffreDeStage(offreDeStage);

        SuperviseurInfo superviseurInfo = SuperviseurInfo.builder()
                .firstName("Sarah")
                .lastName("Connor")
                .fonction("Senior Software Architect")
                .phone("514-555-8822")
                .build();

        EvaluationProductivite evaluationProductivite = EvaluationProductivite.builder()
                .planificationScore(4L)
                .comprehensionScore(4L)
                .rythmeScore(3L)
                .prioriteScore(4L)
                .echeancierScore(0L)
                .commentaire("Great autonomy and fast understanding of the codebase.")
                .build();

        QualiteTravail qualiteTravail = QualiteTravail.builder()
                .completeTaskScore(4L)
                .minutieScore(3L)
                .checkBoxScore(4L)
                .perfectionnementResearchScore(4L)
                .analystScore(3L)
                .commentaire("Clean code, follows team conventions and writes solid unit tests.")
                .build();

        QualiteSocial qualiteSocial = QualiteSocial.builder()
                .socialScore(4L)
                .teamWorkScore(4L)
                .adaptationScore(4L)
                .critiqueAcceptationScore(3L)
                .respectScore(4L)
                .listenScore(4L)
                .commentaire("Integrates very well into the agile team and participates actively in daily standups.")
                .build();

        HabiletePersonelle habiletePersonelle = HabiletePersonelle.builder()
                .motivationScore(4L)
                .tellIdeaScore(3L)
                .initiativeScore(4L)
                .securityScore(0L)
                .responsabiliteScore(4L)
                .ponctuelScore(4L)
                .commentaire("Always punctual, proactive, and eager to learn new frameworks.")
                .build();

        AppreciationGlobal appreciationGlobal = AppreciationGlobal.builder()
                .attenteEmployeurScore(4L)
                .descriptionAppreciation("Alice exceeded our expectations throughout her internship. She quickly became a key contributor to the backend team.")
                .discussionAvecStagiaire(true)
                .supervisionTime(4)
                .renewExperience(ChoixAccueil.OUI)
                .experienceSuffisante("Nous allons l'engager")
                .build();

        EvaluationStagiaire evaluationStagiaire = EvaluationStagiaire.builder()
                .id(1L)
                .candidature(candidature)
                .superviseur(superviseurInfo)
                .evaluationProductivite(evaluationProductivite)
                .qualiteTravail(qualiteTravail)
                .qualiteSociale(qualiteSocial)
                .habiletePersonelle(habiletePersonelle)
                .appreciationGlobal(appreciationGlobal)
                .dateSignature(LocalDateTime.now())
                .build();

        when(evaluationStagiaireRepository.findEvaluationStagiaireByCandidature_Id(1L))
                .thenReturn(Optional.of(evaluationStagiaire));

        Resource pdfResource = pdfService.createEvaluationStagiaire("fr", 1L);
        assertNotNull(pdfResource);


        Path desktopPath = Paths.get(System.getProperty("user.home"), "Desktop", "EvaluationStagiaire_Test.pdf");
        Files.write(desktopPath, pdfResource.getInputStream().readAllBytes());

        System.out.println("PDF généré avec succès sur le bureau : " + desktopPath.toAbsolutePath());
    }
}