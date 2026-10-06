package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.CommentaireRefusObligatoireException;
import com.lacouf.rsbjwt.Exception.OffreIntrouvableException;
import com.lacouf.rsbjwt.Exception.OffreNonEnAttenteException;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.dto.DepartementDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.RejectionCommentDTO;
import com.lacouf.rsbjwt.service.dto.SecteurEmployeurDTO;
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
    public ResponseEntity<List<OffreDeStageDTO>> getOffresEnAttente(){
        List<OffreDeStageDTO> offres = gestionnaireService.getOffresEnAttente();
        return ResponseEntity.ok(offres);
    }

    @PutMapping("/offres/{id}/approuver")
    public ResponseEntity<OffreDeStageDTO> approverOffre(
            @PathVariable Long id
    ) throws OffreIntrouvableException, OffreNonEnAttenteException {
        OffreDeStageDTO offre =
                gestionnaireService.approuverOffre(id);

        return ResponseEntity.ok(offre);
    }

    @PutMapping("/offres/{id}/refuser")
    public ResponseEntity<OffreDeStageDTO> refuserOffre(
            @PathVariable Long id,
            @RequestBody RejectionCommentDTO commentdto
    ) throws OffreIntrouvableException, OffreNonEnAttenteException, CommentaireRefusObligatoireException {
        OffreDeStageDTO offre =
                gestionnaireService.refuserOffre(id, commentdto.rejectionComment());

        return ResponseEntity.ok(offre);
    }

}
