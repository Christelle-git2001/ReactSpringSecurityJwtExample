package com.lacouf.rsbjwt.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.lacouf.rsbjwt.Exception.ContratIntrouvableException;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.dto.CreationOffreDeStageDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Service
@Transactional(readOnly = true)
public class PDFService {
    private final String FRANCAIS = "fr";
    private final ContratRepository contratRepository;

    public PDFService(ContratRepository contratRepository) {
        this.contratRepository = contratRepository;
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

    private void ajouterBlocSignature(List<IElement> elements, String labelKey, String nomComplet, LocalDate dateSignature, ResourceBundle messages) {
        elements.add(new Paragraph(messages.getString(labelKey)).setBold().setMarginTop(10));

        if (dateSignature != null) {
            elements.add(new Paragraph(MessageFormat.format(messages.getString("contrat.signe"), nomComplet, dateSignature)));
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

            ajouterBlocSignature(elements, "signature.etudiant", etudiant.getFirstName() + " " + etudiant.getLastName(), contrat.getDateSignatureEtudiant(), messages);
            ajouterBlocSignature(elements, "signature.employeur", employeur.getFirstName() + " " + employeur.getLastName(), contrat.getDateSignatureEmployeur(), messages);
            ajouterBlocSignature(elements, "signature.gestionnaire", gestionnaire.getFirstName() + " " + gestionnaire.getLastName(), contrat.getDateSignatureGestionnaire(), messages);

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
}