package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exceptions.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.Exceptions.EmailExistantException;
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurInscriptionDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employeur")
@CrossOrigin
public class EmployeurControlleur {
    private final EmployeurService employeurService;

    public EmployeurControlleur(EmployeurService employeurService){
        this.employeurService = employeurService;
    }

    @PostMapping("/inscription")
    public ResponseEntity<EmployeurDTO> inscription(@Valid @RequestBody EmployeurInscriptionDTO employeurInscriptionDTO) throws Exception{
        EmployeurDTO employeurDTO;
        employeurDTO = employeurService.creeCompteEmployeur(employeurInscriptionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(employeurDTO);
    }
}
