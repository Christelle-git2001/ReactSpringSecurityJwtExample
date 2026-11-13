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

    private Table addEvaluationBoxFiveChecks(Integer sectionNumber, String titleKey, String subtitleKey, Map<String,String> evaluationCriteriaKey, String commentaire, ResourceBundle messages){
        Table table = new Table(1)
                .setBorder(new SolidBorder(1))
                .setWidth(UnitValue.createPercentValue(100))
                .setKeepTogether(true);

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


        gridTable.addCell(createCommentaryLine(commentaire,"evaluation.commentaire"));


        return gridTable;
    }

    private Table addEvaluationBoxGlobale(String titleKey, String appreciationScore, String commentaire, Boolean discuteAvecStagiaire, ResourceBundle messages) {
        Table table = new Table(1)
                .setBorder(new SolidBorder(1))
                .setWidth(UnitValue.createPercentValue(100))
                .setKeepTogether(true);

        Paragraph paragraphTitle = new Paragraph(messages.getString(titleKey))
                .setBold()
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER);

        Cell cellTitle = createCellSansBordure(paragraphTitle);
        table.addCell(cellTitle);

        float[] appreciationWidths = {85f, 15f};
        Table gridAppreciation = new Table(UnitValue.createPercentArray(appreciationWidths))
                .setWidth(UnitValue.createPercentValue(85))
                .setHorizontalAlignment(HorizontalAlignment.CENTER);

        String[] appreciationKeys = {
                "appreciation_globale.depasse_beaucoup",
                "appreciation_globale.depasse",
                "appreciation_globale.repond_pleinement",
                "appreciation_globale.repond_partiellement",
                "appreciation_globale.ne_repond_pas"
        };

        for (int i = 0; i < appreciationKeys.length; i++) {
            int expectedScore = appreciationKeys.length - i;
            boolean isChecked = appreciationScore != null && appreciationScore.equals(String.valueOf(expectedScore));
            String symbole = isChecked ? "[ X ]" : "[    ]";

            gridAppreciation.addCell(createCellSansBordure(new Paragraph(messages.getString(appreciationKeys[i]))));
            gridAppreciation.addCell(createCellSansBordureBoldCenter(new Paragraph(symbole)));
        }

        Cell cellAppreciationContainer = createCellSansBordure(new Paragraph()).add(gridAppreciation);
        cellAppreciationContainer.setMarginBottom(10f);
        table.addCell(cellAppreciationContainer);

        table.addCell(createCommentaryLine(commentaire,"appreciation_globale.precisez"));

        boolean ouiChecked = (discuteAvecStagiaire != null && discuteAvecStagiaire);
        boolean nonChecked = (discuteAvecStagiaire != null && !discuteAvecStagiaire);

        String symboleOui = ouiChecked ? "[ X ]" : "[    ]";
        String symboleNon = nonChecked ? "[ X ]" : "[    ]";

        Table discussionTable = new Table(new float[]{1, 1, 1})
                .setWidth(UnitValue.createPercentValue(80));

        discussionTable.addCell(createCellSansBordure(new Paragraph(messages.getString("appreciation_globale.discutee"))).setPaddingRight(20f));
        discussionTable.addCell(createCellSansBordure(new Paragraph(messages.getString("oui") + " " + symboleOui)).setPaddingRight(20f));
        discussionTable.addCell(createCellSansBordure(new Paragraph(messages.getString("non") + " " + symboleNon)));

        table.addCell(createCellSansBordure(new Paragraph()).add(discussionTable));

        return table;
    }

    private Table addSectionFinaleBox(ChoixAccueil accueilStagiaireChoix, String formationSuffisante, String nom, String fonction, LocalDateTime date, ResourceBundle messages) {
        Table table = new Table(1)
                .setBorder(new SolidBorder(1))
                .setWidth(UnitValue.createPercentValue(100))
                .setKeepTogether(true);

        Paragraph titleParagraph = new Paragraph(messages.getString("appreciation_globale.accueil_futur"))
                .setFontSize(15)
                .setTextAlignment(TextAlignment.LEFT);
        table.addCell(createCellSansBordure(titleParagraph));

        boolean ouiChecked = ChoixAccueil.OUI.equals(accueilStagiaireChoix);
        boolean nonChecked = ChoixAccueil.NON.equals(accueilStagiaireChoix);
        boolean peutEtreChecked = ChoixAccueil.PEUT_ETRE.equals(accueilStagiaireChoix) || accueilStagiaireChoix == null;

        String symboleOui = ouiChecked ? "[ X ]" : "[    ]";
        String symboleNon = nonChecked ? "[ X ]" : "[    ]";
        String symbolePeutEtre = peutEtreChecked ? "[ X ]" : "[    ]";

        Paragraph optionsParagraph = new Paragraph()
                .setTextAlignment(TextAlignment.CENTER)
                .add(messages.getString("oui") + " " + symboleOui)
                .add(new Text("       "))
                .add(messages.getString("non") + " " + symboleNon)
                .add(new Text("       "))
                .add(messages.getString("peut_etre") + " " + symbolePeutEtre).setBold();

        Cell optionsCell = createCellSansBordure(optionsParagraph);
        optionsCell.setMarginBottom(8f);
        table.addCell(optionsCell);

        table.addCell(createCommentaryLine(formationSuffisante,"appreciation_globale.formation_technique"));

        table.addCell(addSignatureSection(nom, fonction, date, messages));

        return table;
    }

    private Cell addSignatureSection(String nom, String fonction, LocalDateTime date, ResourceBundle messages) {
        Table sigTable = new Table(new float[]{1f, 1f})
                .setWidth(UnitValue.createPercentValue(100));

        String dateFormatted = formatDateString(date);

        Paragraph nomPara = new Paragraph()
                .add(messages.getString("nom") + " : ")
                .add(new Text(nom != null ? nom : "").setUnderline());

        Paragraph fonctionPara = new Paragraph()
                .add(messages.getString("superviseur_fonction") + " : ")
                .add(new Text(fonction != null ? fonction : "").setUnderline());

        sigTable.addCell(createCellSansBordure(nomPara));
        sigTable.addCell(createCellSansBordure(fonctionPara));


        Paragraph signaturePara = new Paragraph()
                .add(messages.getString("contrat.signatures") + " : ____________________");

        Paragraph datePara = new Paragraph()
                .add(messages.getString("label.date") + " : ")
                .add(new Text(dateFormatted).setUnderline());

        sigTable.addCell(createCellSansBordure(signaturePara));
        sigTable.addCell(createCellSansBordure(datePara));

        Cell wrapperCell = new Cell().add(sigTable);
        wrapperCell.setBorder(Border.NO_BORDER);
        wrapperCell.setMarginTop(10f);

        return wrapperCell;
    }

    private Table addHeuresEncadrementBox(String heuresEncadrement, ResourceBundle messages) {
        Table table = new Table(1)
                .setBorder(new SolidBorder(1))
                .setWidth(UnitValue.createPercentValue(100))
                .setKeepTogether(true);

        Table innerTable = new Table(new float[]{1, 1})
                .setHorizontalAlignment(HorizontalAlignment.LEFT);

        Paragraph labelParagraph = new Paragraph(messages.getString("appreciation_globale.heures_encadrement"))
                .setBold();
        innerTable.addCell(createCellSansBordure(labelParagraph).setPaddingRight(8f));

        String valeurAffichee = (heuresEncadrement != null && !heuresEncadrement.trim().isEmpty())
                ? heuresEncadrement
                : "";

        Paragraph valeurParagraph = new Paragraph(valeurAffichee)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);

        Cell cellValeur = createCellSansBordure(valeurParagraph)
                .setWidth(55f);

        innerTable.addCell(cellValeur);

        Cell containerCell = createCellSansBordure(new Paragraph())
                .add(innerTable)
                .setPaddingTop(6f)
                .setPaddingBottom(6f)
                .setPaddingLeft(6f);

        table.addCell(containerCell);

        return table;
    }

    private Table addReturnFormBox(Professeur professeur, ResourceBundle messages) {
        Table table = new Table(1)
                .setBorder(new SolidBorder(1))
                .setWidth(UnitValue.createPercentValue(100))
                .setKeepTogether(true);

        Table innerTable = new Table(2)
                .setWidth(UnitValue.createPercentValue(100));

        Paragraph labelParagraph = new Paragraph(messages.getString("return.message"))
                .setBold();
        innerTable.addCell(createCellSansBordure(labelParagraph));

        String nomComplet = "";
        if (professeur != null) {
            String prenom = professeur.getFirstName() != null ? professeur.getFirstName() : "";
            String nom = professeur.getLastName() != null ? professeur.getLastName() : "";
            nomComplet = (prenom + " " + nom).trim();
        }
        String email = (professeur != null && professeur.getEmail() != null) ? professeur.getEmail() : "";

        Paragraph contactParagraph = new Paragraph()
                .add(new Text(nomComplet + (!nomComplet.isEmpty() && !email.isEmpty() ? " " : "")).setItalic());
        Paragraph emailContact = new Paragraph(new Text(email).setItalic());

        Cell cellContact = createCellSansBordure(contactParagraph);
        cellContact.add(emailContact);
        cellContact.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
        innerTable.addCell(cellContact);

        Cell containerCell = createCellSansBordure(new Paragraph())
                .add(innerTable)
                .setPaddingTop(6f)
                .setPaddingBottom(6f)
                .setPaddingLeft(6f)
                .setPaddingRight(6f);

        table.addCell(containerCell);

        return table;
    }

    private Div addFooterInstitutionnel(ResourceBundle messages) {
        Div footerDiv = new Div().setKeepTogether(true);

        Paragraph merciPara = new Paragraph(messages.getString("footer.merci"))
                .setItalic()
                .setBold()
                .setFontSize(11)
                .setMarginTop(12f)
                .setMarginBottom(6f);
        footerDiv.add(merciPara);

        Paragraph infoCollege = new Paragraph()
                .add(new Text(messages.getString("application.titre") + "\n").setFontSize(9))
                .add(new Text(messages.getString("employeur_evaluation.ATE") + "\n").setFontSize(9))
                .add(new Text(formatDateString(LocalDateTime.now())).setFontSize(8));

        footerDiv.add(infoCollege);

        return footerDiv;
    }

    private String formatDateString(LocalDateTime date){
        String dateFormatted = "";
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", java.util.Locale.FRENCH);
            dateFormatted = date.format(formatter);
        }
        return dateFormatted;
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

    private Cell createCommentaryLine(String commentaire, String questionKey){
        commentaire = commentaire != null && !commentaire.trim().isEmpty() ? commentaire : "";
        Paragraph paragraphCommentaireTitle = new Paragraph(messages.getString(questionKey) + " : ");
        paragraphCommentaireTitle.setMarginBottom(5f);
        Paragraph paragraphCommentaire = new Paragraph(commentaire);
        Paragraph ligneSupplementaire = new Paragraph();

        ligneSupplementaire.setMarginBottom(15f);
        ligneSupplementaire.setPaddingTop(10f);

        paragraphCommentaire.addTabStops(new TabStop(480f, TabAlignment.RIGHT, new SolidLine(1f)));
        paragraphCommentaire.add(new Tab());
        ligneSupplementaire.addTabStops(new TabStop(480f, TabAlignment.RIGHT, new SolidLine(1f)));
        ligneSupplementaire.add(new Tab());

        return new Cell()
                .setBorder(Border.NO_BORDER)
                .add(paragraphCommentaireTitle)
                .add(paragraphCommentaire)
                .add(ligneSupplementaire);
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
        clefDeTraductionTab.put("etudiant_nom",nouvelEtudiant.firstName() + " " + nouvelEtudiant.lastName());
        clefDeTraductionTab.put("etudiant_programme", nouvelEtudiant.department());
        clefDeTraductionTab.put("entreprise_nom",candidature.employeurBusinessName());
        clefDeTraductionTab.put("superviseur_nom",evaluationStagiaireDTO.supervisueurStageDTO().firstName() + " " + evaluationStagiaireDTO.supervisueurStageDTO().lastName());
        clefDeTraductionTab.put("superviseur_fonction",evaluationStagiaireDTO.supervisueurStageDTO().fonction());
        clefDeTraductionTab.put("superviseur_telephone","475-856-9854");
        document.add(addInformationBox(clefDeTraductionTab,messages));
        document.add(addCenteredParagraph("employeur_evaluation.behaviour_instruction",messages).setBold().setMarginTop(20f).setMarginBottom(20f));
        LinkedHashMap<String,String> criteriaProduction = new LinkedHashMap<>();
        criteriaProduction.put("productivite_a", String.valueOf(evaluationProductivite.rythmeScore()));
        criteriaProduction.put("productivite_b", "N/A");
        criteriaProduction.put("productivite_c", String.valueOf(2));
        criteriaProduction.put("productivite_d", String.valueOf(3));
        criteriaProduction.put("productivite_e", String.valueOf(1));
        String commentaire = "sdfsdf sdfsdfs dfsdfsdfs sdfsdfsdf";
        document.add(addEvaluationBoxFiveChecks(sectionNumber,"productivite_titre","productivite_subtitle",criteriaProduction,commentaire,messages).setMarginBottom(15f));
        document.add(new Paragraph(messages.getString("evaluation.N/A_meaning")).setBold());
        sectionNumber += 1;
        LinkedHashMap<String,String> qualiteTravail = new LinkedHashMap<>();
        qualiteTravail.put("qualite_travail_a", String.valueOf(4));
        qualiteTravail.put("qualite_travail_b", "N/A");
        qualiteTravail.put("qualite_travail_c", String.valueOf(2));
        qualiteTravail.put("qualite_travail_d", String.valueOf(3));
        qualiteTravail.put("qualite_travail_e", String.valueOf(1));
        commentaire = "";
        document.add(addEvaluationBoxFiveChecks(sectionNumber,"qualite_travail_titre","qualite_travail_subtitle",qualiteTravail,commentaire,messages).setMarginBottom(15f));
        document.add(new Paragraph(messages.getString("evaluation.N/A_meaning")).setBold());
        sectionNumber += 1;
        LinkedHashMap<String,String> qualiteSocial = new LinkedHashMap<>();
        qualiteSocial.put("qualite_social_a", String.valueOf(4));
        qualiteSocial.put("qualite_social_b", "N/A");
        qualiteSocial.put("qualite_social_c", String.valueOf(2));
        qualiteSocial.put("qualite_social_d", String.valueOf(3));
        qualiteSocial.put("qualite_social_e", String.valueOf(1));
        qualiteSocial.put("qualite_social_f", "N/A");
        document.add(addEvaluationBoxFiveChecks(sectionNumber,"qualite_social_titre","qualite_social_subtitle",qualiteSocial,commentaire,messages).setMarginBottom(15f));
        document.add(new Paragraph(messages.getString("evaluation.N/A_meaning")).setBold());
        sectionNumber += 1;
        LinkedHashMap<String,String> habiletePersonnelle = new LinkedHashMap<>();
        habiletePersonnelle.put("habilete_personnelle_a", String.valueOf(4));
        habiletePersonnelle.put("habilete_personnelle_b", "N/A");
        habiletePersonnelle.put("habilete_personnelle_c", String.valueOf(2));
        habiletePersonnelle.put("habilete_personnelle_d", String.valueOf(3));
        habiletePersonnelle.put("habilete_personnelle_e", String.valueOf(1));
        habiletePersonnelle.put("habilete_personnelle_f", "N/A");
        document.add(addEvaluationBoxFiveChecks(sectionNumber,"habilete_personnelle_titre","habilete_personnelle_subtitle",habiletePersonnelle,commentaire,messages).setMarginBottom(15f));
        document.add(new Paragraph(messages.getString("evaluation.N/A_meaning")).setBold());
        document.add(addEvaluationBoxGlobale("appreciation_globale.titre", String.valueOf(appreciationGlobal.attenteEmployeurScore()), appreciationGlobal.descriptionAppreciation(), appreciationGlobal.discussionAvecStagiaire(), messages).setMarginBottom(15f));
        document.add(addHeuresEncadrementBox(String.valueOf(appreciationGlobal.supervisionTime()),messages).setMarginBottom(15f));
        document.add(addSectionFinaleBox(appreciationGlobal.renewExperience(),appreciationGlobal.experienceSuffisante(),superviseur.firstName() + " " + superviseur.lastName(),superviseur.fonction(),LocalDateTime.now(),messages).setMarginBottom(15f));
        document.add(addReturnFormBox(professeur,messages));
        document.add(addFooterInstitutionnel(messages));



        document.close();
        System.out.println("PDF généré sur le bureau : " + dest);
    }
}