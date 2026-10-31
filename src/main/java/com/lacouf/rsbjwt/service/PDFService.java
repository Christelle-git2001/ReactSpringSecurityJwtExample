package com.lacouf.rsbjwt.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.lacouf.rsbjwt.Exception.OffreIntrouvableException;
import com.lacouf.rsbjwt.Exception.UtilisateurIntrouvableException;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.dto.CandidatureDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.ResourceBundle;

@Service
@Transactional(readOnly = true)
public class PDFService {
    private static final String STOCKAGE_CONTRAT = "Contrat.pdf";
    private final String FRANCAIS = "fr";

    private final EmployeurRepository employeurRepository;
    private final EtudiantRepository etudiantRepository;
    private final GestionnaireRepository gestionnaireRepository;
    private final OffreDeStageRepository offreDeStageRepository;

    public PDFService(EmployeurRepository employeurRepository,
                      EtudiantRepository etudiantRepository,
                      GestionnaireRepository gestionnaireRepository,
                      OffreDeStageRepository offreDeStageRepository) {
        this.employeurRepository = employeurRepository;
        this.etudiantRepository = etudiantRepository;
        this.gestionnaireRepository = gestionnaireRepository;
        this.offreDeStageRepository = offreDeStageRepository;
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

    public void createContratPdf(Gestionnaire gestionnaire, CandidatureDTO candidature, String langue) throws UtilisateurIntrouvableException, OffreIntrouvableException, FileNotFoundException {

        Etudiant etudiant = etudiantRepository.findById(candidature.etudiantId())
                .orElseThrow(UtilisateurIntrouvableException::new);

        OffreDeStage offre = offreDeStageRepository.findById(candidature.offreId())
                .orElseThrow(OffreIntrouvableException::new);

        Employeur employeur = offre.getEmployeur();

        Locale locale = new Locale(langue != null ? langue : FRANCAIS);
        ResourceBundle messages = ResourceBundle.getBundle("i18n.messages", locale);

        PdfWriter writer = new PdfWriter(STOCKAGE_CONTRAT);
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

            IElement[] elements = new IElement[] {
                    addTitre("contrat.titre", 20, messages),

                    new AreaBreak(AreaBreakType.NEXT_PAGE),
                    addTitre("contrat.entente", 14, messages),
                    new Paragraph(messages.getString("contrat.contexte")).setMarginBottom(10),

                    addCenteredParagraph("intro.gestionnaire", messages, gestionnaire.getFirstName() + " " + gestionnaire.getLastName()),
                    et,
                    addCenteredParagraph("intro.employeur", messages, employeur.getFirstName() + " " + employeur.getLastName()),
                    et,
                    addCenteredParagraph("intro.etudiant", messages, etudiant.getFirstName() + " " + etudiant.getLastName()),

                    new Paragraph(messages.getString("contrat.conviennent")).setMarginTop(10).setMarginBottom(10),

                    addSectionTitle("section.endroit", messages),
                    new Paragraph(MessageFormat.format(messages.getString("label.adresse"), employeur.getTown())),

                    addSectionTitle("section.duree", messages),
                    new Paragraph(MessageFormat.format(messages.getString("label.dateDebut"), offre.getStartDate())),
                    new Paragraph(MessageFormat.format(messages.getString("label.dateFin"), offre.getEndDate())),
                    new Paragraph(MessageFormat.format(
                            messages.getString("label.nbSemaines"),
                            Math.abs(ChronoUnit.WEEKS.between(offre.getStartDate(), offre.getEndDate()))
                    )),
                    addSectionTitle("section.horaire", messages),
                    new Paragraph(MessageFormat.format(messages.getString("label.horaireTravail"), "9h - 17h")),
                    new Paragraph(MessageFormat.format(messages.getString("label.nbHeures"), "35")),

                    addSectionTitle("section.salaire", messages),
                    new Paragraph(MessageFormat.format(messages.getString("label.salaireHoraire"), offre.getSalary() + " $")),

                    new AreaBreak(AreaBreakType.NEXT_PAGE),
                    addTitre("section.taches", 14, messages),
                    cadreDescription,

                    addSectionTitle("section.responsabilites", messages),
                    new Paragraph(messages.getString("engagement.college")).setBold(),
                    new Paragraph(messages.getString("texte.college")),

                    new Paragraph(messages.getString("engagement.entreprise")).setBold(),
                    new Paragraph(messages.getString("texte.entreprise")),

                    new Paragraph(messages.getString("engagement.etudiant")).setBold(),
                    new Paragraph(messages.getString("texte.etudiant")),

                    new Paragraph(messages.getString("contrat.signatures")).setBold().setMarginTop(15),
                    new Paragraph(messages.getString("contrat.respect")),
                    new Paragraph(messages.getString("contrat.foi")).setMarginBottom(15),

                    new Paragraph(messages.getString("signature.etudiant")).setBold(),
                    new Paragraph(messages.getString("signature.employeur")).setBold(),
                    new Paragraph(messages.getString("signature.gestionnaire")).setBold()
            };

            for (IElement element : elements) {
                document.add((IBlockElement) element);
            }
    }
}