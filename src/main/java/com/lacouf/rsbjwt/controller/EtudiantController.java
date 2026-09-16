package com.lacouf.rsbjwt.controller;


import com.lacouf.rsbjwt.security.exception.EmailDejaUtiliseException;
import com.lacouf.rsbjwt.security.exception.MatriculeDejaUtiliseException;
import com.lacouf.rsbjwt.security.exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.service.EtudiantService;
import com.lacouf.rsbjwt.service.dto.ErreurDto;
import com.lacouf.rsbjwt.service.dto.EtudiantDto;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDto;
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
    public ResponseEntity<?> inscription(@Valid @RequestBody InscriptionEtudiantDto inscriptionEtudiantDto) {
        try {
            EtudiantDto etudiantDto = etudiantService.inscrire(inscriptionEtudiantDto);
            return ResponseEntity.ok(etudiantDto);
        } catch (EmailDejaUtiliseException |
                 MatriculeDejaUtiliseException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErreurDto(e.getMessage()));

        } catch (MotDePasseNonCorrespondantException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErreurDto(e.getMessage()));

        }
    }

}
