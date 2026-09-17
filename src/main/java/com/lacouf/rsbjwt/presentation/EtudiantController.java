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

    @PostMapping("/register")
    public ResponseEntity<?> inscription(@Valid @RequestBody InscriptionEtudiantDTO inscriptionEtudiantDto) {
        try {
            EtudiantDTO etudiantDto = etudiantService.inscrireEtudiant(inscriptionEtudiantDto);
            return ResponseEntity.ok(etudiantDto);
        } catch (EmailExistantException |
                 MatriculeExistantException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErreurDTO(e.getMessage()));

        } catch (MotDePasseNonCorrespondantException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErreurDTO(e.getMessage()));

        }
    }

}
