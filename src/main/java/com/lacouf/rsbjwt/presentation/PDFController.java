package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.CandidatureIntrouvableException;
import com.lacouf.rsbjwt.Exception.ContratIntrouvableException;
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.PDFService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/pdf")
public class PDFController {

    private final PDFService pdfService ;

    public PDFController(PDFService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/contrat/{contratId}")
    public ResponseEntity<Resource> obtenirContratPdf(
            @PathVariable Long contratId,
            @RequestParam(defaultValue = "fr") String langue) throws ContratIntrouvableException {

        Resource pdfResource = pdfService.createContratPdf(contratId, langue);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"contrat.pdf\"")
                .body(pdfResource);
    }
}
