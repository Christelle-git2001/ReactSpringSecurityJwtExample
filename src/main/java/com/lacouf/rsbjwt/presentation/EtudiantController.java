package com.lacouf.rsbjwt.presentation;


import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.service.EtudiantService;
import com.lacouf.rsbjwt.service.dto.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
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
            EtudiantIntrouvableException, SuppressionFichierEchoueeException, IOException{
        CvEtudiantDTO cvEtudiantDto = etudiantService.televerserCv(file, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(cvEtudiantDto);
    }

    @GetMapping("/cv")
    public ResponseEntity<CvEtudiantDTO> obtenirCv(Authentication authentication)
            throws EtudiantIntrouvableException, FichierIntrouvableException {
        return ResponseEntity.ok(etudiantService.getCv(authentication.getName()));
    }

    @GetMapping("/cv/download")
    public ResponseEntity<Resource> telechargerCv( Authentication authentication )
        throws EtudiantIntrouvableException, FichierIntrouvableException {

        Resource resource = etudiantService.telechargerCv(authentication.getName());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cv.pdf\"")
                .body(resource);
    }

    @DeleteMapping("/cv")
    public ResponseEntity<Void> supprimerCv( Authentication authentication )
        throws EtudiantIntrouvableException, FichierIntrouvableException,
            SuppressionFichierEchoueeException {

        etudiantService.supprimerCv(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/offres")
    public ResponseEntity<List<OffreDeStageDTO>> getOffresDisponibles(){
        return ResponseEntity.ok(etudiantService.getOffresDisponibles());
    }

}
