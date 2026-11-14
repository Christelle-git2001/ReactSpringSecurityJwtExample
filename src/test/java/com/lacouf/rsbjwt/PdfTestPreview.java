package com.lacouf.rsbjwt;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.lacouf.rsbjwt.Exception.ContratIntrouvableException;
import com.lacouf.rsbjwt.model.Enum.ChoixAccueil;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.model.Professeur;
import com.lacouf.rsbjwt.service.PDFService;
import com.lacouf.rsbjwt.service.dto.*;
import com.lacouf.rsbjwt.service.dto.EvaluationStagiaire.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class PdfTestPreview {

    @InjectMocks
    PDFService pdfService;

    EtudiantDTO nouvelEtudiant = new EtudiantDTO(
            1L,                                    // id
            "Jean",                                // firstName
            "Dupont",                              // lastName
            "jean.dupont@example.com",             // email
            "MAT123456",                           // matricule
            "514-555-1234",                        // phoneNumber
            "Informatique",                        // department
            LocalDateTime.now().minusDays(1),      // previousLoginAt
            10L,                                   // professeurId
            "Marie Curie",                         // professeurNom
            LocalDateTime.now()                    // professorAssignedAt
    );

    SuperviseurStageDTO superviseur = new SuperviseurStageDTO(
            "Marc",
            "Tremblay",
            "Chef d'équipe / Mentor",
            "514-555-9876"
    );

    Professeur professeur = Professeur.builder()
            .firstName("Carole")
                .lastName("Test")
                .email("carole.test@gmail.com")
                .password("motDePasseEncode")
                .matricule("1234567")
                .phoneNumber("123-123-1234")
                .department(Departement.INFORMATIQUE)
                .build();

    EvaluationProductiviteDTO evaluationProductivite = new EvaluationProductiviteDTO(
            4L, // planificationScore
            1L, // comprehensionScore
            4L, // rythmeScore
            3L, // prioriteScore
            4L,  // echeancierScore
            ""
    );

    QualiteTravailDTO qualiteTravail = new QualiteTravailDTO(
            5L, // completeTaskScore
            4L, // minutieScore
            0L, // checkBoxScore
            4L, // perfectionnementResearchScore
            2L,  // analystScore
            ""
    );

    HabiletePersonelleDTO habiletePersonelle = new HabiletePersonelleDTO(
            5L, // motivationScore
            4L, // tellIdeaScore
            1L, // initiativeScore
            4L, // securityScore
            2L, // responsabiliteScore
            0L,  // ponctuelScore
            ""
    );

    QualiteSocialeDTO mockQualiteSociale = new QualiteSocialeDTO(
            1L, // socialScore (ex: Totalement en accord)
            1L, // teamWorkScore
            2L, // adaptationScore (ex: Plutôt en accord)
            1L, // critiqueAcceptationScore
            1L, // respectScore
            2L, // listenScore
            "Le stagiaire s'intègre très facilement au sein de l'équipe et fait preuve d'une écoute active. Il accueille les commentaires constructifs avec ouverture et professionnalisme."
    );

    AppreciationGlobaleDTO appreciationGlobal = new AppreciationGlobaleDTO(
            5L,                                                 // attenteEmployeurScore
            "Stagiaire exceptionnel, très autonome et rigoureux.", // descriptionAppreciation
            true,                                               // discussionAvecStagiaire
            2.5,                                                // supervisionTime (ex: 2.5 heures par semaine)
            ChoixAccueil.PEUT_ETRE,                                               // renewExperience
            "Expérience amplement suffisante et enrichissante."   // experienceSuffisante
    );

    EmployeurDTO employeur = new EmployeurDTO(
            50L,                                 // id
            "Sophie",                            // firstName
            "Martin",                            // lastName
            "514-555-4321",                      // phone
            "sophie.martin@techsolutions.com",   // email
            "Montréal",                          // town
            "Tech Solutions inc.",               // businessName
            SecteurActivite.INFORMATIQUE,         // businessSector
            LocalDateTime.now().minusDays(2)     // previousLoginAt
    );

    CandidatureDTO candidature = new CandidatureDTO(
            101L,                                // id
            nouvelEtudiant.id(),                    // etudiantId
            nouvelEtudiant.firstName(),                              // etudiantFirstName
            nouvelEtudiant.lastName(),                            // etudiantLastName
            nouvelEtudiant.department(),                      // etudiantDepartment
            202L,                                // offreId
            "Développeur Web Java/React",        // offreTitle
            employeur.businessName(),               // employeurBusinessName
            LocalDateTime.of(2026, 3, 15, 10, 30), // dateCandidature
            301L,                                // cvId
            StatutCandidature.ACCEPTEE           // statut
    );

    EvaluationStagiaireDTO evaluationStagiaireDTO = new EvaluationStagiaireDTO(101L,superviseur,evaluationProductivite,qualiteTravail,mockQualiteSociale,habiletePersonelle,appreciationGlobal);

    Locale locale = new Locale("en");
    ResourceBundle messages = ResourceBundle.getBundle("i18n.messages", locale);

    private Paragraph addTitre(String cleDeTraduction, int fontSize, ResourceBundle messages) {
        return new Paragraph(messages.getString(cleDeTraduction))
                .setFontSize(fontSize)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(15);
    }

    private Paragraph addSectionTitle(String cleDeTraduction, ResourceBundle messages) {
        return new Paragraph(messages.getString(cleDeTraduction))
                .setFontSize(12)
                .setBold()
                .setMarginTop(10)
                .setMarginBottom(5);
    }

    private Paragraph addCenteredParagraph(String cleDeTraduction, ResourceBundle messages, Object... args) {
        String text = messages.getString(cleDeTraduction);
        if (args != null && args.length > 0) {
            text = MessageFormat.format(text, args);
        }
        return new Paragraph(text).setTextAlignment(TextAlignment.CENTER);
    }



    @Test
    public void generateTestPdf() throws IOException, ContratIntrouvableException {

    }
}