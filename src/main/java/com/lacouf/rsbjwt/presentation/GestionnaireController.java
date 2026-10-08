package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.dto.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gestionnaire")
@CrossOrigin(origins = "http://localhost:5173")
public class GestionnaireController {


    private final GestionnaireService gestionnaireService;

    public GestionnaireController(GestionnaireService gestionnaireService) {
        this.gestionnaireService = gestionnaireService;
    }


    @GetMapping("/secteurEmployeur")
    public ResponseEntity<List<SecteurEmployeurDTO>> getSecteurs() {
        List<SecteurEmployeurDTO> secteurs = gestionnaireService.getAllSecteurs();
        return ResponseEntity.ok(secteurs);
    }

    @GetMapping("/departement")
    public ResponseEntity<List<DepartementDTO>> getDepartements() {
        List<DepartementDTO> departements = gestionnaireService.getAllDepartements();
        return ResponseEntity.ok(departements);
    }

    @GetMapping("/offres")
    public ResponseEntity<List<OffreDeStageDTO>> getOffresEnAttente() {
        List<OffreDeStageDTO> offres = gestionnaireService.getOffresEnAttente();
        return ResponseEntity.ok(offres);
    }

    @PutMapping("/offres/{id}/approuver")
    public ResponseEntity<OffreDeStageDTO> approveOffer(
            @PathVariable Long id
    ) throws OffreIntrouvableException, OffreNonEnAttenteException {
        OffreDeStageDTO offre =
                gestionnaireService.approuverOffre(id);

        return ResponseEntity.ok(offre);
    }

    @PutMapping("/offres/{id}/refuser")
    public ResponseEntity<OffreDeStageDTO> rejectOffer(
            @PathVariable Long id,
            @RequestBody RejectionCommentDTO commentdto
    ) throws OffreIntrouvableException, OffreNonEnAttenteException, CommentaireRefusObligatoireException {
        OffreDeStageDTO offre =
                gestionnaireService.refuserOffre(id, commentdto.rejectionComment());

        return ResponseEntity.ok(offre);
    }

    @GetMapping("/cvs/attente")
    public ResponseEntity<List<CvEtudiantDTO>> getCurriculumVitaeEnAttente() {
        List<CvEtudiantDTO> cvEtudiantDTOS = gestionnaireService.getCurriculumVitaeEnAttente();
        return ResponseEntity.status(HttpStatus.OK).body(cvEtudiantDTOS);
    }

    @GetMapping("/cvs")
    public ResponseEntity<List<CvEtudiantDTO>> getTousLesCvs() {
        return ResponseEntity.ok(gestionnaireService.getTousLesCvs());
    }

    @PutMapping("/cv/{id}/approuver")
    public ResponseEntity<CvEtudiantDTO> approuverCurriculumVitae(@PathVariable Long id) throws CurriculumVitaeIntrouvable, CurriculumVitaeNonEnAttente {
        CvEtudiantDTO cvEtudiantDTO = gestionnaireService.approuverCurriculumVitae(id);
        return ResponseEntity.status(HttpStatus.OK).body(cvEtudiantDTO);
    }

    @PutMapping("/cv/{id}/refuser")
    public ResponseEntity<CvEtudiantDTO> refuserCurriculumVitae(@PathVariable Long id, @RequestBody RejectionCommentDTO rejectionCommentDTO) throws CurriculumVitaeNonEnAttente, CurriculumVitaeIntrouvable, CommentaireRefusObligatoireException {
        CvEtudiantDTO cvEtudiantDTO = gestionnaireService.refuserCurriculumVitae(id, rejectionCommentDTO.rejectionComment());
        return ResponseEntity.status(HttpStatus.OK).body(cvEtudiantDTO);
    }

    @GetMapping("/cv/{id}/download")
    public ResponseEntity<Resource> telechargerCvGestionnaire(@PathVariable Long id)
            throws CurriculumVitaeIntrouvable, FichierIntrouvableException {

        Resource resource = gestionnaireService.telechargerCvParId(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"cv.pdf\"")
                .body(resource);


    }
}
