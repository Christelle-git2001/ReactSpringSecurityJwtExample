package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.offre.OffreIntrouvableException;
import com.lacouf.rsbjwt.exception.utilisateur.DepartementInvalideException;
import com.lacouf.rsbjwt.exception.utilisateur.EmailExistantException;
import com.lacouf.rsbjwt.exception.utilisateur.MatriculeExistantException;
import com.lacouf.rsbjwt.exception.utilisateur.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.exception.utilisateur.NumeroTelephoneExistantException;
import com.lacouf.rsbjwt.exception.utilisateur.UtilisateurIntrouvableException;
import com.lacouf.rsbjwt.service.dto.candidature.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.candidature.ResumeStageEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.InscriptionProfesseurDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.ProfesseurDTO;
import com.lacouf.rsbjwt.service.ProfesseurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professeur")
public class ProfesseurController {

    private final ProfesseurService professeurService ;

    public ProfesseurController(ProfesseurService professeurService) {
        this.professeurService = professeurService;
    }

    @PostMapping("inscription")
    public ResponseEntity<ProfesseurDTO> creerCompteProfesseur(@Valid @RequestBody InscriptionProfesseurDTO newProfesseur)
            throws DepartementInvalideException,
            EmailExistantException,
            MatriculeExistantException,
            MotDePasseNonCorrespondantException, NumeroTelephoneExistantException {

            ProfesseurDTO createdProfesseur = professeurService.creerCompteProfesseur(newProfesseur);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProfesseur);
    }

    @GetMapping("/etudiants")
    public ResponseEntity<List<EtudiantDTO>> getEtudiantsAssignes()
            throws UtilisateurIntrouvableException {

        return ResponseEntity.ok(professeurService.getEtudiantsAssignes());
    }

    @GetMapping("/etudiants/{etudiantId}/candidatures")
    public ResponseEntity<List<CandidatureDTO>> getCandidaturesEtudiant(
            @PathVariable Long etudiantId) throws UtilisateurIntrouvableException{

        return ResponseEntity.ok(
                professeurService.getCandidaturesEtudiant(etudiantId)
        );
    }

    @GetMapping("/offre/{offreId}")
    public ResponseEntity<ResumeStageEtudiantDTO> getResumeOffre(@PathVariable Long offreId) throws OffreIntrouvableException {
        return ResponseEntity.ok(professeurService.getResumeOffre(offreId));
    }


}
