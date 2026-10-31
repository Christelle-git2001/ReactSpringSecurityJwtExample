package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.CandidatureIntrouvableException;
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.PDFService;
import com.lacouf.rsbjwt.service.dto.CandidatureDTO;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/pdf")
public class PDFController {

    private final PDFService pdfService ;
    private final EmployeurService employeurService;

    public PDFController(PDFService pdfService, EmployeurService employeurService) {
        this.pdfService = pdfService;
        this.employeurService = employeurService;
    }

    @GetMapping("/contrat/{candidatureId}/pdf")
    public ResponseEntity<Resource> obtenirContratPdf(
            @PathVariable Long candidatureId,
            @RequestParam(defaultValue = "fr") String langue) throws CandidatureIntrouvableException {

        Resource pdfResource = pdfService.createContratPdf(candidatureId, langue);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"contrat.pdf\"")
                .body(pdfResource);
    }
}
