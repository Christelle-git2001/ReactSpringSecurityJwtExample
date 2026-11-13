package com.lacouf.rsbjwt;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.lacouf.rsbjwt.Exception.ContratIntrouvableException;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.service.PDFService;
import com.lacouf.rsbjwt.service.dto.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.EvaluationStagiaire.AppreciationGlobaleDTO;
import com.lacouf.rsbjwt.service.dto.EvaluationStagiaire.EvaluationProductiviteDTO;
import com.lacouf.rsbjwt.service.dto.EvaluationStagiaire.HabiletePersonelleDTO;
import com.lacouf.rsbjwt.service.dto.EvaluationStagiaire.QualiteTravailDTO;
import com.lacouf.rsbjwt.service.dto.SuperviseurStageDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
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

    EvaluationProductiviteDTO evaluationProductivite = new EvaluationProductiviteDTO(
            4L, // planificationScore
            1L, // comprehensionScore
            4L, // rythmeScore
            3L, // prioriteScore
            4L  // echeancierScore
    );

    QualiteTravailDTO qualiteTravail = new QualiteTravailDTO(
            5L, // completeTaskScore
            4L, // minutieScore
            0L, // checkBoxScore
            4L, // perfectionnementResearchScore
            2L  // analystScore
    );

    HabiletePersonelleDTO habiletePersonelle = new HabiletePersonelleDTO(
            5L, // motivationScore
            4L, // tellIdeaScore
            1L, // initiativeScore
            4L, // securityScore
            2L, // responsabiliteScore
            0L  // ponctuelScore
    );

    AppreciationGlobaleDTO appreciationGlobal = new AppreciationGlobaleDTO(
            5L,                                                 // attenteEmployeurScore
            "Stagiaire exceptionnel, très autonome et rigoureux.", // descriptionAppreciation
            true,                                               // discussionAvecStagiaire
            2.5,                                                // supervisionTime (ex: 2.5 heures par semaine)
            null,                                               // renewExperience
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

    Locale locale = new Locale("en");
    ResourceBundle messages = ResourceBundle.getBundle("i18n.messages", locale);
    private final int NB_COLUMN_EVALUATION = 5;

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

    private Table addDoubleTextTable(String cleDeTraductionText1, String cleDeTraductionText2, ResourceBundle messages){
        Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);

        Cell cellLeft = new Cell().add(new Paragraph(messages.getString(cleDeTraductionText1)));
        cellLeft.setTextAlignment(TextAlignment.LEFT);
        cellLeft.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
        cellLeft.setBold();
        cellLeft.setFontSize(15);
        table.addCell(cellLeft);

        Cell cellRight = new Cell().add(new Paragraph(messages.getString(cleDeTraductionText2)));
        cellRight.setTextAlignment(TextAlignment.RIGHT);
        cellRight.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
        cellRight.setBold();
        cellRight.setFontSize(15);
        table.addCell(cellRight);

        return table;
    }

    private Table addInformationBox(Map<String,String> clefDeTraductionTab, ResourceBundle messages){
        Table table = new Table(1).setBorder(new SolidBorder(1)).setWidth(UnitValue.createPercentValue(100));
        clefDeTraductionTab.forEach((key,value) -> {
            Text textKey = new Text(messages.getString(key) + " : ");
            textKey.setBold();
            Text textValue = new Text(value + " ");
            Paragraph paragraph = new Paragraph(textKey).add(textValue);
            paragraph.addTabStops(new TabStop(480f, TabAlignment.RIGHT, new SolidLine(1f)));
            paragraph.add(new Tab());
            Cell cell = new Cell().add(paragraph);
            cell.setBorder(Border.NO_BORDER);
            cell.setPaddingTop(5);
            cell.setPaddingBottom(5);
            cell.setPaddingLeft(5);

            table.addCell(cell);
        });
        return table;
    }

    private Table addEvaluationBox(Integer sectionNumber, String titleKey, String subtitleKey, Map<String,String> evaluationCriteriaKey, String commentaire, ResourceBundle messages){
        Table table = new Table(1)
                .setBorder(new SolidBorder(1))
                .setWidth(UnitValue.createPercentValue(100));

        Paragraph paragraphTitle = new Paragraph(sectionNumber + ". " + messages.getString(titleKey))
                .setBold()
                .setFontSize(17)
                .setTextAlignment(TextAlignment.CENTER);


        boolean hasSubtitle = (subtitleKey != null && !subtitleKey.isEmpty());

        Cell cellTitle = createCellSansBordure(paragraphTitle);
        if (!hasSubtitle) {
            cellTitle.setBorderBottom(new SolidBorder(1));
        }
        table.addCell(cellTitle);

        if (hasSubtitle) {
            Paragraph paragraphSubTitle = new Paragraph(messages.getString(subtitleKey))
                    .setTextAlignment(TextAlignment.CENTER);

            Cell cellSubTitle = createCellSansBordure(paragraphSubTitle);
            cellSubTitle.setBorderBottom(new SolidBorder(1));
            table.addCell(cellSubTitle);
        }

        table.addCell(createCellSansBordure(new Paragraph()).add(addSousTableEvaluationBox(evaluationCriteriaKey, commentaire, messages)));

        return table;
    }

    private Table addSousTableEvaluationBox(Map<String, String> evaluationCriteriaKey, String commentaire, ResourceBundle messages) {
        float[] columnWidths = {40f, 12f, 12f, 12f, 12f, 12f};
        Table gridTable = new Table(UnitValue.createPercentArray(columnWidths))
                .setWidth(UnitValue.createPercentValue(100));

        String[] headerKeys = {
                "evaluation.stagiaire_capacite",
                "evaluation.totalement_en_accord",
                "evaluation.plutot_en_accord",
                "evaluation.plutot_en_désaccord",
                "evaluation.totalement_en_désaccord",
                "evaluation.na"
        };

        gridTable.addCell(createCellSansBordure(new Paragraph(messages.getString(headerKeys[0]))).setBold());
        for (int i = 1; i < headerKeys.length; i++) {
            gridTable.addCell(createCellSansBordureBoldCenter(new Paragraph(messages.getString(headerKeys[i]))));
        }

        char letter = 'a';
        for (Map.Entry<String, String> entry : evaluationCriteriaKey.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            gridTable.addCell(createCellSansBordure(new Paragraph(letter + ") " + messages.getString(key))));
            letter++;

            for (int i = 0; i < NB_COLUMN_EVALUATION; i++) {
                boolean isChecked = value.equals(String.valueOf(i + 1)) || ("N/A".equals(value) && i == NB_COLUMN_EVALUATION - 1);
                String symbole = isChecked ? "[ X ]" : "[    ]";
                gridTable.addCell(createCellSansBordureBoldCenter(new Paragraph(symbole)));
            }
        }

        commentaire = commentaire != null && !commentaire.trim().isEmpty() ? "" : commentaire;
        Paragraph paragraphCommentaireTitle = new Paragraph(messages.getString("evaluation.commentaire") + " : ");
        paragraphCommentaireTitle.setMarginBottom(10f).setBold();
        Paragraph paragraphCommentaire = new Paragraph(commentaire);
        Paragraph ligneSupplementaire = new Paragraph();

        ligneSupplementaire.setMarginBottom(15f);
        ligneSupplementaire.setPaddingTop(10f);

        paragraphCommentaire.addTabStops(new TabStop(480f, TabAlignment.RIGHT, new SolidLine(1f)));
        paragraphCommentaire.add(new Tab());
        ligneSupplementaire.addTabStops(new TabStop(480f, TabAlignment.RIGHT, new SolidLine(1f)));
        ligneSupplementaire.add(new Tab());

        Cell commentCell = new Cell(1, columnWidths.length)
                .setBorder(Border.NO_BORDER)
                .add(paragraphCommentaireTitle)
                .add(paragraphCommentaire)
                .add(ligneSupplementaire);
        gridTable.addCell(commentCell);


        return gridTable;
    }

    private Cell createCellSansBordure(Paragraph paragraph){
        Cell cell = new Cell();
        cell.setBorder(Border.NO_BORDER);
        return cell.add(paragraph);
    }

    private Cell createCellSansBordureBoldCenter(Paragraph paragraph){
        Cell cell = new Cell();
        cell.setBorder(Border.NO_BORDER).setBold().setTextAlignment(TextAlignment.CENTER);
        return cell.add(paragraph);
    }
    @Test
    public void generateTestPdf() throws IOException, ContratIntrouvableException {
        String dest = System.getProperty("user.home") + "/Desktop/test-offre.pdf";

        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        int sectionNumber = 1;

        document.add(addDoubleTextTable("application.titre","employeur_evaluation.ATE",messages).setMarginBottom(15));
        document.add(addTitre("employeur_evaluation.titre",20,messages).setMarginBottom(15));

        LinkedHashMap<String,String> clefDeTraductionTab = new LinkedHashMap<>();
        clefDeTraductionTab.put("etudiant_nom","Jean Dupont");
        clefDeTraductionTab.put("etudiant_programme","Informatique");
        clefDeTraductionTab.put("entreprise_nom","Malinois inc");
        clefDeTraductionTab.put("superviseur_nom","Nicolas Bergeron");
        clefDeTraductionTab.put("superviseur_fonction","Ingenieur Logiciel");
        clefDeTraductionTab.put("superviseur_telephone","475-856-9854");
        document.add(addInformationBox(clefDeTraductionTab,messages));
        document.add(addCenteredParagraph("employeur_evaluation.behaviour_instruction",messages).setBold().setMarginTop(20f).setMarginBottom(20f));
        LinkedHashMap<String,String> criteriaProduction = new LinkedHashMap<>();
        criteriaProduction.put("productivite_a", String.valueOf(4));
        criteriaProduction.put("productivite_b", "N/A");
        criteriaProduction.put("productivite_c", String.valueOf(2));
        criteriaProduction.put("productivite_d", String.valueOf(3));
        criteriaProduction.put("productivite_e", String.valueOf(1));
        String commentaire = "";
        document.add(addEvaluationBox(sectionNumber,"productivite_titre","productivite_subtitle",criteriaProduction,commentaire,messages));
        sectionNumber += 1;
        document.close();
        System.out.println("PDF généré sur le bureau : " + dest);
    }
}