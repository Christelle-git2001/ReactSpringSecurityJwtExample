package com.lacouf.rsbjwt.presentation;


import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.service.EtudiantService;
import com.lacouf.rsbjwt.service.dto.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.ErreurDTO;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

@RestController
@RequestMapping("/etudiant")
public class EtudiantController {
    private final EtudiantService etudiantService;

    public EtudiantController(EtudiantService etudiantService){
        this.etudiantService = etudiantService;
    }

    @PostMapping("/inscription")
    public ResponseEntity<EtudiantDTO> creerCompteEtudiant(@Valid @RequestBody InscriptionEtudiantDTO inscriptionEtudiantDto) throws EmailExistantException, MotDePasseNonCorrespondantException,MatriculeExistantException, DepartementInvalideException, NumeroTelephoneExistantException {
            EtudiantDTO etudiantDto = etudiantService.creerCompteEtudiant(inscriptionEtudiantDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(etudiantDto);
    }

    @PostMapping("/cv")
    public ResponseEntity<CvEtudiantDTO> televerserCv(@RequestParam("file") MultipartFile file, Authentication authentication)
            throws FichierCorrompuException, FichierTropVolumineuxException, FichierTypeInvalideException,
            EtudiantIntrouvableException, SuppressionEchoueeFichierException, IOException{
        CvEtudiantDTO cvEtudiantDto = etudiantService.uploadCv(file, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(cvEtudiantDto);
    }

    @GetMapping("/cv")
    public ResponseEntity<List<CvEtudiantDTO>> obtenirTousLesCv(Authentication authentication)
            throws EtudiantIntrouvableException {
        return ResponseEntity.ok(etudiantService.getTousLesCv(authentication.getName()));
    }

    @GetMapping("/cv/{cvId}")
    public ResponseEntity<Resource> telechargerCv(
            @PathVariable Long cvId,
            Authentication authetication
        ) throws EtudiantIntrouvableException, FichierIntrouvableException {

        Resource resource = etudiantService.telechargerCv(cvId, authetication.getName());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cv.pdf\"")
                .body(resource);
    }
}
