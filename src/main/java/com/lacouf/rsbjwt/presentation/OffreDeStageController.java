package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.DateFinAvantDateDebutException;
import com.lacouf.rsbjwt.Exception.FichierCorrompuException;
import com.lacouf.rsbjwt.Exception.FichierTropVolumineuxException;
import com.lacouf.rsbjwt.Exception.FichierTypeInvalideException;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.service.OffreDeStageService;
import com.lacouf.rsbjwt.service.dto.CreationOffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/employeur/offres")
public class OffreDeStageController {
    private final OffreDeStageService offreDeStageService;

    public OffreDeStageController(OffreDeStageService offreDeStageService){
        this.offreDeStageService = offreDeStageService;
    }

    @PostMapping("/creation-offre")
    public ResponseEntity<OffreDeStageDTO> creerOffre(
            @Valid @RequestPart("offre")CreationOffreDeStageDTO creationOffreDeStageDTO,
            @RequestPart(value = "fichier", required = false)MultipartFile multipartFile,
            @AuthenticationPrincipal Employeur employeur
            ) throws DateFinAvantDateDebutException, FichierCorrompuException,
            FichierTropVolumineuxException, FichierTypeInvalideException, IOException {
        OffreDeStageDTO offreDeStageDTO = offreDeStageService.creerOffre(creationOffreDeStageDTO,multipartFile,employeur);
        return ResponseEntity.status(HttpStatus.CREATED).body(offreDeStageDTO);
    }

}
