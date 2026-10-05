package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.dto.CreationOffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEmployeurDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

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

    @PostMapping(value = "/offres", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OffreDeStageDTO> creerOffre(
            @Valid @RequestPart("offre") CreationOffreDeStageDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal
    ) throws  IOException, DateFinAvantDateDebutException, FichierTypeInvalideException, FichierCorrompuException, FichierTropVolumineuxException {
        OffreDeStageDTO offreDTO = employeurService.creerOffre(dto, file, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(offreDTO);
    }

    @GetMapping("/offres")
    public ResponseEntity<List<OffreDeStageDTO>> obtenirOffresEmployeur(
            @AuthenticationPrincipal String email
    ) {
        List<OffreDeStageDTO> offres = employeurService.obtenirOffres(email);
        return ResponseEntity.ok(offres);
    }

    @PutMapping("/offres/{id}")
    public ResponseEntity<OffreDeStageDTO> modifierOffre(
            @PathVariable Long id,
            @Valid @RequestPart("offre") CreationOffreDeStageDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal
    ) throws DateFinAvantDateDebutException,
            IOException,
            FichierTypeInvalideException, FichierCorrompuException, FichierTropVolumineuxException, OffreIntrouvableException, OffreNonAutoriseeException, OffreNonEnAttenteException {

        OffreDeStageDTO offreDTO =
                employeurService.modifierOffre(id, dto, file, principal.getName());

        return ResponseEntity.ok(offreDTO);
    }

    @GetMapping("/offres/{id}/download")
    public ResponseEntity<Resource> telechargerDocumentOffre(
            @PathVariable Long id,
            Authentication authentication
    ) throws FichierIntrouvableException, OffreIntrouvableException {

        Resource resource = employeurService.telechargerOffre(id, authentication.getName());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"offre.pdf\"")
                .body(resource);
    }
}
