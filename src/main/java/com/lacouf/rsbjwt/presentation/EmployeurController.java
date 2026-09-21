package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.EmailExistantException;
import com.lacouf.rsbjwt.Exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.Exception.NumeroTelephoneExistantException;
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEmployeurDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employeur")
@CrossOrigin(origins = "http://localhost:5173")
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
}
