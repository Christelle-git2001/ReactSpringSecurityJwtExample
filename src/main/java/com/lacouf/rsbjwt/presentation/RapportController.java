package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.RapportService;
import com.lacouf.rsbjwt.service.dto.candidature.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.cv.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.EntrevueDTO;
import com.lacouf.rsbjwt.service.dto.offre.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EtudiantDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gestionnaire/rapports")
@CrossOrigin(origins = "http://localhost:5173")
public class RapportController {

    private final RapportService rapportService;

    public RapportController(RapportService rapportService) {
        this.rapportService = rapportService;
    }

    @GetMapping("/offres/en-attente")
    public ResponseEntity<List<OffreDeStageDTO>> obtenirOffresEnAttente() {
        return ResponseEntity.ok(rapportService.obtenirOffresEnAttente());
    }

    @GetMapping("/offres/validees")
    public ResponseEntity<List<OffreDeStageDTO>> obtenirOffresApprouvees() {
        return ResponseEntity.ok(rapportService.obtenirOffresApprouvees());
    }

    @GetMapping("/etudiants")
    public ResponseEntity<List<EtudiantDTO>> obtenirEtudiantsInscrits() {
        return ResponseEntity.ok(rapportService.obtenirEtudiantsInscrits());
    }

    @GetMapping("/etudiants/sans-cv")
    public ResponseEntity<List<EtudiantDTO>> obtenirEtudiantsSansCv() {
        return ResponseEntity.ok(rapportService.obtenirEtudiantsSansCv());
    }

    @GetMapping("/cvs/en-attente")
    public ResponseEntity<List<CvEtudiantDTO>> obtenirCvEnAttente() {
        return ResponseEntity.ok(rapportService.obtenirCvEnAttente());
    }

    @GetMapping("/etudiants/sans-entrevue")
    public ResponseEntity<List<EtudiantDTO>> obtenirEtudiantsSansEntrevue() {
        return ResponseEntity.ok(rapportService.obtenirEtudiantsSansEntrevue());
    }

    @GetMapping("/candidatures/en-attente-entrevue")
    public ResponseEntity<List<CandidatureDTO>> obtenirCandidaturesEnAttenteEntrevue() {
        return ResponseEntity.ok(
                rapportService.obtenirCandidaturesEnAttenteEntrevue()
        );
    }

    @GetMapping("/entrevues/en-attente-reponse")
    public ResponseEntity<List<EntrevueDTO>> obtenirEntrevuesEnAttenteReponse() {
        return ResponseEntity.ok(
                rapportService.obtenirEntrevuesEnAttenteReponse()
        );
    }

    @GetMapping("/etudiants/stage-trouve")
    public ResponseEntity<List<EtudiantDTO>> obtenirEtudiantsAyantTrouveStage() {
        return ResponseEntity.ok(
                rapportService.obtenirEtudiantsAyantTrouveStage()
        );
    }

    @GetMapping("/candidatures/sans-evaluation")
    public ResponseEntity<List<CandidatureDTO>> obtenirCandidaturesSansEvaluation() {
        return ResponseEntity.ok(
                rapportService.obtenirCandidaturesSansEvaluation()
        );
    }

}