package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.contrat.ContratIntrouvableException;
import com.lacouf.rsbjwt.exception.evaluation.EvaluationMilieuStageIntrouvableException;
import com.lacouf.rsbjwt.exception.evaluation.EvaluationStagiaireIntrouvable;
import com.lacouf.rsbjwt.service.PDFService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

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
            @RequestParam(defaultValue = "fr") String langue) throws ContratIntrouvableException, IOException {

        Resource pdfResource = pdfService.createContratPdf(contratId, langue);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"contrat.pdf\"")
                .body(pdfResource);
    }

    @GetMapping("/evaluation/etudiant/{candidatureID}")
    public ResponseEntity<Resource> obtenirEvaluationStagiaire(
            @PathVariable Long candidatureID,
            @RequestParam(defaultValue = "fr") String langue) throws IOException, EvaluationStagiaireIntrouvable {

        Resource pdfResource = pdfService.createEvaluationStagiaire(langue,candidatureID);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"evaluation_" + candidatureID+".pdf\"")
                .body(pdfResource);
    }

    @GetMapping("/evaluation/stage/{candidatureID}")
    public ResponseEntity<Resource> obtenirEvaluationMilieuStage(@PathVariable Long candidatureID, @RequestParam(defaultValue = "fr") String langue) throws IOException, EvaluationMilieuStageIntrouvableException {
        Resource pdfResource = pdfService.createEvaluationMilieuStage(langue,candidatureID);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,"inline; filename=\"contrat.pdf\"")
                .body(pdfResource);
    }

    private ResponseEntity<Resource> retournerPdf(
            Resource pdfResource,
            String nomFichier) {

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + nomFichier + "\""
                )
                .body(pdfResource);
    }

    @GetMapping("/gestionnaire/rapports/offres/en-attente")
    public ResponseEntity<Resource> obtenirRapportOffresEnAttente(
            @RequestParam(defaultValue = "fr") String langue)
            throws IOException {

        return retournerPdf(
                pdfService.createRapportOffresEnAttentePdf(
                        rapportService.obtenirOffresEnAttente(),
                        langue
                ),
                "offres-en-attente.pdf"
        );
    }

    @GetMapping("/gestionnaire/rapports/offres/approuvees")
    public ResponseEntity<Resource> obtenirRapportOffresApprouvees(
            @RequestParam(defaultValue = "fr") String langue)
            throws IOException {

        return retournerPdf(
                pdfService.createRapportOffresApprouveesPdf(
                        rapportService.obtenirOffresApprouvees(),
                        langue
                ),
                "offres-approuvees.pdf"
        );
    }

    @GetMapping("/gestionnaire/rapports/etudiants")
    public ResponseEntity<Resource> obtenirRapportEtudiantsInscrits(
            @RequestParam(defaultValue = "fr") String langue)
            throws IOException {

        return retournerPdf(
                pdfService.createRapportEtudiantsInscritsPdf(
                        rapportService.obtenirEtudiantsInscrits(),
                        langue
                ),
                "etudiants-inscrits.pdf"
        );
    }

    @GetMapping("/gestionnaire/rapports/etudiants/sans-cv")
    public ResponseEntity<Resource> obtenirRapportEtudiantsSansCv(
            @RequestParam(defaultValue = "fr") String langue)
            throws IOException {

        return retournerPdf(
                pdfService.createRapportEtudiantsSansCvPdf(
                        rapportService.obtenirEtudiantsSansCv(),
                        langue
                ),
                "etudiants-sans-cv.pdf"
        );
    }

    @GetMapping("/gestionnaire/rapports/etudiants/sans-entrevue")
    public ResponseEntity<Resource> obtenirRapportEtudiantsSansEntrevue(
            @RequestParam(defaultValue = "fr") String langue)
            throws IOException {

        return retournerPdf(
                pdfService.createRapportEtudiantsSansEntrevuePdf(
                        rapportService.obtenirEtudiantsSansEntrevue(),
                        langue
                ),
                "etudiants-sans-entrevue.pdf"
        );
    }

    @GetMapping("/gestionnaire/rapports/etudiants/stage-trouve")
    public ResponseEntity<Resource> obtenirRapportEtudiantsAyantTrouveStage(
            @RequestParam(defaultValue = "fr") String langue)
            throws IOException {

        return retournerPdf(
                pdfService.createRapportEtudiantsAyantTrouveStagePdf(
                        rapportService.obtenirEtudiantsAyantTrouveStage(),
                        langue
                ),
                "etudiants-stage-trouve.pdf"
        );
    }

    @GetMapping("/gestionnaire/rapports/cv/en-attente")
    public ResponseEntity<Resource> obtenirRapportCvEnAttente(
            @RequestParam(defaultValue = "fr") String langue)
            throws IOException {

        return retournerPdf(
                pdfService.createRapportCvEnAttentePdf(
                        rapportService.obtenirCvEnAttente(),
                        langue
                ),
                "cv-en-attente.pdf"
        );
    }

    @GetMapping("/gestionnaire/rapports/candidatures/en-attente-entrevue")
    public ResponseEntity<Resource> obtenirRapportCandidaturesEnAttenteEntrevue(
            @RequestParam(defaultValue = "fr") String langue)
            throws IOException {

        return retournerPdf(
                pdfService.createRapportCandidaturesEnAttenteEntrevuePdf(
                        rapportService.obtenirCandidaturesEnAttenteEntrevue(),
                        langue
                ),
                "candidatures-en-attente-entrevue.pdf"
        );
    }

    @GetMapping("/gestionnaire/rapports/candidatures/sans-evaluation")
    public ResponseEntity<Resource> obtenirRapportCandidaturesSansEvaluation(
            @RequestParam(defaultValue = "fr") String langue)
            throws IOException {

        return retournerPdf(
                pdfService.createRapportCandidaturesSansEvaluationPdf(
                        rapportService.obtenirCandidaturesSansEvaluation(),
                        langue
                ),
                "candidatures-sans-evaluation.pdf"
        );
    }

    @GetMapping("/gestionnaire/rapports/entrevues/en-attente-reponse")
    public ResponseEntity<Resource> obtenirRapportEntrevuesEnAttenteReponse(
            @RequestParam(defaultValue = "fr") String langue)
            throws IOException {

        return retournerPdf(
                pdfService.createRapportEntrevuesEnAttenteReponsePdf(
                        rapportService.obtenirEntrevuesEnAttenteReponse(),
                        langue
                ),
                "entrevues-en-attente-reponse.pdf"
        );
    }


}
