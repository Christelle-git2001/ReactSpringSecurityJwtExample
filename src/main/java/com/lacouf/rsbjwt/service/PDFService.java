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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
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

    public Resource createContratPdf(Long contratId, String langue) throws ContratIntrouvableException {

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

        PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf);

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
                document.add((IBlockElement) element);
            }

        return new ByteArrayResource(outputStream.toByteArray());
    }
}