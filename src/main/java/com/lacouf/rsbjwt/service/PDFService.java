package com.lacouf.rsbjwt.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.lacouf.rsbjwt.Exception.CandidatureIntrouvableException;
import com.lacouf.rsbjwt.Exception.ContratIntrouvableException;
import com.lacouf.rsbjwt.Exception.EvaluationStagiaireIntrouvable;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.ChoixAccueil;
import com.lacouf.rsbjwt.model.Evaluation.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.dto.CreationOffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.EvaluationStagiaireDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PDFService {
    private final String FRANCAIS = "fr";
    private final ContratRepository contratRepository;
    private final int NB_COLUMN_EVALUATION = 5;
    private final CandidatureRepository candidatureRepository;
    private final EvaluationStagiaireRepository evaluationStagiaireRepository;

    public PDFService(ContratRepository contratRepository, CandidatureRepository candidatureRepository, EvaluationStagiaireRepository evaluationStagiaireRepository) {
        this.contratRepository = contratRepository;
        this.candidatureRepository = candidatureRepository;
        this.evaluationStagiaireRepository = evaluationStagiaireRepository;
    }

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

    private void ajouterBlocSignature(
            List<IElement> elements,
            String labelKey,
            String nomComplet,
            LocalDateTime dateSignature,
            ResourceBundle messages) {

        elements.add(new Paragraph(messages.getString(labelKey))
                .setBold()
                .setMarginTop(10));

        if (dateSignature != null) {

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            String dateFormatee = dateSignature.format(formatter);

            elements.add(new Paragraph(
                    MessageFormat.format(
                            messages.getString("contrat.signe"),
                            nomComplet,
                            dateFormatee
                    )
            ));

        } else {
            elements.add(new Paragraph("_________________________________________"));
            elements.add(new Paragraph(nomComplet));
            elements.add(new Paragraph("Date : _________________________"));
        }
    }

    public Resource createContratPdf(Long contratId, String langue) throws ContratIntrouvableException, IOException {

        Contrat contrat = contratRepository.findById(contratId)
                .orElseThrow(ContratIntrouvableException::new);

        Candidature candidature = contrat.getCandidature();
        Etudiant etudiant = candidature.getEtudiant();
        OffreDeStage offre = candidature.getOffreDeStage();
        Employeur employeur = offre.getEmployeur();
        Gestionnaire gestionnaire = contrat.getGestionnaire();

        Locale locale = new Locale(langue != null ? langue : FRANCAIS);
        ResourceBundle messages = ResourceBundle.getBundle("i18n.messages", locale);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            Paragraph et = new Paragraph("et")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setMargin(10);

            Div cadreDescription = new Div()
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                    .setPadding(10)
                    .setMarginTop(5)
                    .setMarginBottom(15);
            cadreDescription.add(new Paragraph(offre.getDescription()));

            long nbSemaines = 0;
            if (offre.getStartDate() != null && offre.getEndDate() != null) {
                nbSemaines = Math.abs(ChronoUnit.WEEKS.between(offre.getStartDate(), offre.getEndDate()));
            }

            List<IElement> elements = new ArrayList<>();

            elements.add(addTitre("contrat.titre", 20, messages));

            elements.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            elements.add(addTitre("contrat.entente", 14, messages));
            elements.add(new Paragraph(messages.getString("contrat.contexte")).setMarginBottom(10));

            elements.add(addCenteredParagraph("intro.gestionnaire", messages, gestionnaire.getFirstName() + " " + gestionnaire.getLastName()));
            elements.add(et);
            elements.add(addCenteredParagraph("intro.employeur", messages, employeur.getFirstName() + " " + employeur.getLastName()));
            elements.add(et);
            elements.add(addCenteredParagraph("intro.etudiant", messages, etudiant.getFirstName() + " " + etudiant.getLastName()));

            elements.add(new Paragraph(messages.getString("contrat.conviennent")).setMarginTop(10).setMarginBottom(10));

            elements.add(addSectionTitle("section.endroit", messages));
            elements.add(new Paragraph(MessageFormat.format(messages.getString("label.adresse"), employeur.getTown())));

            elements.add(addSectionTitle("section.duree", messages));
            elements.add(new Paragraph(MessageFormat.format(messages.getString("label.dateDebut"), offre.getStartDate())));
            elements.add(new Paragraph(MessageFormat.format(messages.getString("label.dateFin"), offre.getEndDate())));
            elements.add(new Paragraph(MessageFormat.format(messages.getString("label.nbSemaines"), nbSemaines)));

            elements.add(addSectionTitle("section.horaire", messages));
            elements.add(new Paragraph(MessageFormat.format(messages.getString("label.horaireTravail"), "9h - 17h")));
            elements.add(new Paragraph(MessageFormat.format(messages.getString("label.nbHeures"), "35")));

            elements.add(addSectionTitle("section.salaire", messages));
            elements.add(new Paragraph(MessageFormat.format(messages.getString("label.salaireHoraire"), offre.getSalary() + " $")));

            elements.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            elements.add(addTitre("section.taches", 14, messages));
            elements.add(cadreDescription);

            elements.add(addSectionTitle("section.responsabilites", messages));
            elements.add(new Paragraph(messages.getString("engagement.college")).setBold());
            elements.add(new Paragraph(messages.getString("texte.college")));

            elements.add(new Paragraph(messages.getString("engagement.entreprise")).setBold());
            elements.add(new Paragraph(messages.getString("texte.entreprise")));

            elements.add(new Paragraph(messages.getString("engagement.etudiant")).setBold());
            elements.add(new Paragraph(messages.getString("texte.etudiant")));

            elements.add(new Paragraph(messages.getString("contrat.signatures")).setBold().setMarginTop(15));
            elements.add(new Paragraph(messages.getString("contrat.respect")));
            elements.add(new Paragraph(messages.getString("contrat.foi")).setMarginBottom(15));

            ajouterBlocSignature(
                    elements,
                    "signature.etudiant",
                    etudiant.getFirstName() + " " + etudiant.getLastName(),
                    contrat.getDateSignatureEtudiant(),
                    messages
            );

            ajouterBlocSignature(
                    elements,
                    "signature.employeur",
                    employeur.getFirstName() + " " + employeur.getLastName(),
                    contrat.getDateSignatureEmployeur(),
                    messages
            );

            ajouterBlocSignature(
                    elements,
                    "signature.gestionnaire",
                    gestionnaire.getFirstName() + " " + gestionnaire.getLastName(),
                    contrat.getDateSignatureGestionnaire(),
                    messages
            );
            for (IElement element : elements) {
                if (element instanceof AreaBreak areaBreak) {
                    document.add(areaBreak);
                } else if (element instanceof IBlockElement blockElement) {
                    document.add(blockElement);
                }
            }
        }

        return new ByteArrayResource(outputStream.toByteArray());
    }

    public Resource createCvPdf(Etudiant etudiant) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph(etudiant.getFirstName() + " " + etudiant.getLastName())
                    .setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Montréal, QC | " + etudiant.getPhoneNumber() + " | " + etudiant.getEmail())
                    .setFontSize(10).setTextAlignment(TextAlignment.CENTER).setMarginBottom(15));

            document.add(new Paragraph("🎯 Profil professionnel").setBold().setFontSize(12).setMarginTop(10));
            document.add(new Paragraph("Étudiant(e) en Techniques de l'informatique au Cégep André-Laurendeau, passionné(e) par le développement logiciel et la résolution de problèmes. Possède des compétences en programmation, développement d'applications et travail d'équipe. À la recherche d'un stage permettant de mettre en pratique mes connaissances et de contribuer à des projets concrets.")
                    .setFontSize(10).setMarginBottom(10));

            document.add(new Paragraph("🎓 Formation").setBold().setFontSize(12).setMarginTop(10));
            document.add(new Paragraph("Cégep André-Laurendeau — Techniques de l'informatique (2024 - Présent)").setBold().setFontSize(10));
            document.add(new Paragraph("Département : " + etudiant.getDepartment() + " | Matricule : " + etudiant.getMatricule()).setFontSize(10).setMarginBottom(10));

            document.add(new Paragraph("💻 Compétences techniques").setBold().setFontSize(12).setMarginTop(10));
            document.add(new Paragraph("• Langages : Java, C#, SQL, JavaScript, HTML/CSS").setFontSize(10));
            document.add(new Paragraph("• Technologies et outils : Git / GitHub, Spring Boot, Visual Studio Code, IntelliJ IDEA").setFontSize(10));
            document.add(new Paragraph("• Méthodologies : Programmation orientée objet, Débogage et tests, Travail collaboratif").setFontSize(10).setMarginBottom(10));

            document.add(new Paragraph("🚀 Projets").setBold().setFontSize(12).setMarginTop(10));
            document.add(new Paragraph("Application de gestion et développement logiciel").setBold().setFontSize(10));
            document.add(new Paragraph("• Conception de la logique de calcul, application des principes POO, tests et validation des résultats.").setFontSize(10).setMarginBottom(10));

            document.add(new Paragraph("🌍 Langues").setBold().setFontSize(12).setMarginTop(10));
            document.add(new Paragraph("• Français : Courant\n• Anglais : Intermédiaire").setFontSize(10));
        }

        return new ByteArrayResource(outputStream.toByteArray());
    }

    public Resource createOffreDeStagePdf(CreationOffreDeStageDTO dto, Employeur employeur) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("Offre de stage – " + dto.title())
                    .setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER).setMarginBottom(15));

            document.add(new Paragraph("À propos de l'entreprise").setBold().setFontSize(12).setMarginTop(10));
            String nomEntreprise = employeur != null ? employeur.getBusinessName() : "Organisation partenaire";
            String secteur = employeur != null && employeur.getBusinessSector() != null ? employeur.getBusinessSector().name() : "technologie";
            String ville = employeur != null ? employeur.getTown() : "Montréal";

            document.add(new Paragraph(nomEntreprise + " (" + ville + ") est une organisation spécialisée dans le secteur de " + secteur + ". Nous recherchons un(e) stagiaire motivé(e) pour participer au développement de nos solutions logicielles.")
                    .setFontSize(10).setMarginBottom(10));

            document.add(new Paragraph("Description du poste").setBold().setFontSize(12).setMarginTop(10));
            document.add(new Paragraph("Sous la supervision de l'équipe de développement, le ou la stagiaire participera à :").setFontSize(10));
            document.add(new Paragraph("• " + dto.description()).setFontSize(10));
            document.add(new Paragraph("• Développer et maintenir des applications logicielles.").setFontSize(10));
            document.add(new Paragraph("• Participer aux revues de code, corriger des bogues et effectuer des tests.").setFontSize(10));
            document.add(new Paragraph("• Rédiger et mettre à jour la documentation technique.").setFontSize(10).setMarginBottom(10));

            document.add(new Paragraph("Exigences").setBold().setFontSize(12).setMarginTop(10));
            document.add(new Paragraph("• Connaissance d'au moins un langage de programmation (Java, C#, etc.).").setFontSize(10));
            document.add(new Paragraph("• Notions de bases de données SQL et utilisation de Git.").setFontSize(10));
            document.add(new Paragraph("• Capacité à travailler en équipe et bon esprit d'analyse.").setFontSize(10).setMarginBottom(10));

            document.add(new Paragraph("Conditions du stage").setBold().setFontSize(12).setMarginTop(10));
            document.add(new Paragraph("• Date de début : " + dto.startDate()).setFontSize(10));
            document.add(new Paragraph("• Date de fin : " + dto.endDate()).setFontSize(10));
            document.add(new Paragraph("• Rémunération : " + dto.salary() + " $ / heure").setFontSize(10).setMarginBottom(10));

            document.add(new Paragraph("Pour postuler").setBold().setFontSize(12).setMarginTop(10));
            document.add(new Paragraph("Veuillez transmettre votre CV et votre profil GitHub à l'adresse de l'entreprise.").setFontSize(10));
        }

        return new ByteArrayResource(outputStream.toByteArray());
    }

    public Resource createEvaluationStagiaire(String langue, Long candidatureId) throws IOException, EvaluationStagiaireIntrouvable {

        EvaluationStagiaire evaluationStagiaire = evaluationStagiaireRepository.findEvaluationStagiaireByCandidature_Id(candidatureId).orElseThrow(EvaluationStagiaireIntrouvable::new);
        Candidature candidature = evaluationStagiaire.getCandidature();
        Etudiant etudiant = candidature.getEtudiant();
        Professeur professeur = etudiant.getProfesseur();
        OffreDeStage offreDeStage = candidature.getOffreDeStage();
        Employeur employeur = offreDeStage.getEmployeur();
        SuperviseurInfo superviseurInfo = evaluationStagiaire.getSuperviseur();

        EvaluationProductivite evaluationProductivite = evaluationStagiaire.getEvaluationProductivite();
        QualiteTravail qualiteTravail = evaluationStagiaire.getQualiteTravail();
        QualiteSocial qualiteSocial = evaluationStagiaire.getQualiteSociale();
        HabiletePersonelle habiletePersonelle = evaluationStagiaire.getHabiletePersonelle();
        AppreciationGlobal appreciationGlobal = evaluationStagiaire.getAppreciationGlobal();



        Locale locale = new Locale(langue != null ? langue : FRANCAIS);
        ResourceBundle messages = ResourceBundle.getBundle("i18n.messages", locale);


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try(
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        ) {
            int sectionNumber = 1;

            document.add(addDoubleTextTable("application.titre", "employeur_evaluation.ATE", messages).setMarginBottom(15));
            document.add(addTitre("employeur_evaluation.titre", 20, messages).setMarginBottom(15));

            LinkedHashMap<String, String> clefDeTraductionTab = buildInformationMap(etudiant, employeur, superviseurInfo);
            document.add(addInformationBox(clefDeTraductionTab, messages));
            document.add(addCenteredParagraph("employeur_evaluation.behaviour_instruction", messages).setBold().setMarginTop(20f).setMarginBottom(20f));

            LinkedHashMap<String, String> criteriaProduction = mapToCriteriaMap(
                    "productivite",
                    evaluationProductivite.getPlanificationScore(),
                    evaluationProductivite.getComprehensionScore(),
                    evaluationProductivite.getRythmeScore(),
                    evaluationProductivite.getPrioriteScore(),
                    evaluationProductivite.getEcheancierScore()
                    );
            String commentaire = evaluationProductivite.getCommentaire();
            document.add(addEvaluationBoxFiveChecks(sectionNumber, "productivite_titre", "productivite_subtitle", criteriaProduction, commentaire, messages).setMarginBottom(15f));
            document.add(new Paragraph(messages.getString("evaluation.N/A_meaning")).setBold());

            sectionNumber += 1;
            LinkedHashMap<String, String> criteriaQualiteTravail = mapToCriteriaMap(
                    "qualite_travail",
                    qualiteTravail.getCompleteTaskScore(),
                    qualiteTravail.getMinutieScore(),
                    qualiteTravail.getCheckBoxScore(),
                    qualiteTravail.getPerfectionnementResearchScore(),
                    qualiteTravail.getAnalystScore()
            );
            commentaire = qualiteTravail.getCommentaire();
            document.add(addEvaluationBoxFiveChecks(sectionNumber, "qualite_travail_titre", "qualite_travail_subtitle", criteriaQualiteTravail, commentaire, messages).setMarginBottom(15f));
            document.add(new Paragraph(messages.getString("evaluation.N/A_meaning")).setBold());

            sectionNumber += 1;
            LinkedHashMap<String, String> criteriaQualiteSocial = mapToCriteriaMap(
                    "qualite_social",
                    qualiteSocial.getSocialScore(),
                    qualiteSocial.getTeamWorkScore(),
                    qualiteSocial.getAdaptationScore(),
                    qualiteSocial.getCritiqueAcceptationScore(),
                    qualiteSocial.getRespectScore(),
                    qualiteSocial.getListenScore()
            );
            commentaire = qualiteSocial.getCommentaire();
            document.add(addEvaluationBoxFiveChecks(sectionNumber, "qualite_social_titre", "qualite_social_subtitle", criteriaQualiteSocial, commentaire, messages).setMarginBottom(15f));
            document.add(new Paragraph(messages.getString("evaluation.N/A_meaning")).setBold());

            sectionNumber += 1;
            LinkedHashMap<String, String> criteriaHabiletePersonelle = mapToCriteriaMap(
                    "habilete_personnelle",
                    habiletePersonelle.getMotivationScore(),
                    habiletePersonelle.getTellIdeaScore(),
                    habiletePersonelle.getInitiativeScore(),
                    habiletePersonelle.getSecurityScore(),
                    habiletePersonelle.getResponsabiliteScore(),
                    habiletePersonelle.getPonctuelScore()
            );
            commentaire = habiletePersonelle.getCommentaire();

            document.add(addEvaluationBoxFiveChecks(sectionNumber, "habilete_personnelle_titre", "habilete_personnelle_subtitle", criteriaHabiletePersonelle, commentaire, messages).setMarginBottom(15f));
            document.add(new Paragraph(messages.getString("evaluation.N/A_meaning")).setBold());
            document.add(addEvaluationBoxGlobale("appreciation_globale.titre", String.valueOf(appreciationGlobal.getAttenteEmployeurScore()), appreciationGlobal.getDescriptionAppreciation(), appreciationGlobal.getDiscussionAvecStagiaire(), messages).setMarginBottom(15f));
            document.add(addHeuresEncadrementBox(String.valueOf(appreciationGlobal.getSupervisionTime()), messages).setMarginBottom(15f));
            document.add(addSectionFinaleBox(appreciationGlobal.getRenewExperience(), appreciationGlobal.getExperienceSuffisante(), superviseurInfo.getFirstName() + " " + superviseurInfo.getLastName(), superviseurInfo.getFonction(), LocalDateTime.now(), messages).setMarginBottom(15f));
            document.add(addReturnFormBox(professeur, messages));
            document.add(addFooterInstitutionnel(messages));
        }
        return new ByteArrayResource(outputStream.toByteArray());
    }

    private LinkedHashMap<String, String> mapToCriteriaMap(String prefix, Long... scores) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        char suffix = 'a';
        for (Long score : scores) {
            String key = prefix + "_" + suffix;
            String value = (score == null || score == 0) ? "N/A" : String.valueOf(score);
            map.put(key, value);
            suffix++;
        }
        return map;
    }

    private LinkedHashMap<String, String> buildInformationMap(Etudiant etudiant, Employeur employeur, SuperviseurInfo superviseur) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("etudiant_nom", etudiant.getFirstName() + " " + etudiant.getLastName());
        map.put("etudiant_programme", etudiant.getDepartment().getLabel());
        map.put("entreprise_nom", employeur.getBusinessName());
        map.put("superviseur_nom", superviseur.getFirstName() + " " + superviseur.getLastName());
        map.put("superviseur_fonction", superviseur.getFonction());
        map.put("superviseur_telephone", superviseur.getPhone());
        return map;
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


        gridTable.addCell(createCommentaryLine(commentaire,"evaluation.commentaire",messages));


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

        table.addCell(createCommentaryLine(commentaire,"appreciation_globale.precisez", messages));

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

        table.addCell(createCommentaryLine(formationSuffisante,"appreciation_globale.formation_technique",messages));

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

    private Cell createCommentaryLine(String commentaire, String questionKey, ResourceBundle messages){
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
}