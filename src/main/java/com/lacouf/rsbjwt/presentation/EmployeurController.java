package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.dto.CreationOffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEmployeurDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/employeur")
public class EmployeurController {
    private final EmployeurService employeurService;

    public EmployeurController(EmployeurService employeurService){
        this.employeurService = employeurService;
    }

    @PostMapping("/inscription")
    public ResponseEntity<EmployeurDTO> creerCompteEmployeur (@Valid @RequestBody InscriptionEmployeurDTO inscriptionEmployeurDTO) throws EmailExistantException, MotDePasseNonCorrespondantException, NumeroTelephoneExistantException {
        EmployeurDTO employeurDTO;
        employeurDTO = employeurService.creeCompteEmployeur(inscriptionEmployeurDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(employeurDTO);
    }

    @PostMapping("/offres/creation-offre")
    public ResponseEntity<OffreDeStageDTO> creerOffre(
            @Valid @RequestPart("offre") CreationOffreDeStageDTO creationOffreDeStageDTO,
            @RequestPart(value = "fichier", required = false) MultipartFile multipartFile,
            @AuthenticationPrincipal String email
    ) throws DateFinAvantDateDebutException, FichierCorrompuException,
            FichierTropVolumineuxException, FichierTypeInvalideException, IOException {
        OffreDeStageDTO offreDeStageDTO = employeurService.creerOffre(creationOffreDeStageDTO, multipartFile, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(offreDeStageDTO);
    }
}
