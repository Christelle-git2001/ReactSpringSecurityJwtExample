package com.lacouf.rsbjwt.presentation;


import com.lacouf.rsbjwt.Exception.EmailExistantException;
import com.lacouf.rsbjwt.Exception.MatriculeExistantException;
import com.lacouf.rsbjwt.Exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.service.EtudiantService;
import com.lacouf.rsbjwt.service.dto.ErreurDTO;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/etudiant")
public class EtudiantController {
    private final EtudiantService etudiantService;

    public EtudiantController(EtudiantService etudiantService){
        this.etudiantService = etudiantService;
    }

    @PostMapping("/inscription")
    public ResponseEntity<EtudiantDTO> creerCompteEtudiant(@Valid @RequestBody InscriptionEtudiantDTO inscriptionEtudiantDto) throws EmailExistantException, MotDePasseNonCorrespondantException,MatriculeExistantException {
            EtudiantDTO etudiantDto = etudiantService.creerCompteEtudiant(inscriptionEtudiantDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(etudiantDto);
    }

}
