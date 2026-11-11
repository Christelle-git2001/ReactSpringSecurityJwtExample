package com.lacouf.rsbjwt;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;

@ExtendWith(MockitoExtension.class)
public class generateTestPdf {

    @Test
    public void generateTestPdf() throws FileNotFoundException {
        // Crée le PDF directement sur ton bureau pour un test ultra rapide
        String dest = System.getProperty("user.home") + "/Desktop/test-offre.pdf";

        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Teste ton code iText ici
        document.add(new Paragraph("Offre de stage – Test rapide").setFontSize(18).setBold());

        document.close();
        System.out.println("PDF généré sur le bureau : " + dest);
    }
}