package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.service.ProfesseurService;
import com.lacouf.rsbjwt.service.dto.InscriptionProfesseurDTO;
import com.lacouf.rsbjwt.service.dto.ProfesseurDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/professeur")
@CrossOrigin(origins = "http://localhost:5173")
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


}
