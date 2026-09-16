package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.model.Exceptions.ConfirmationMotDePasseEchouer;
import com.lacouf.rsbjwt.model.Exceptions.EmployeurExistant;
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurInscriptionDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class EmployeurControlleur {
    private final EmployeurService employeurService;

    public EmployeurControlleur(EmployeurService employeurService){
        this.employeurService = employeurService;
    }

    @PostMapping("/inscription/employeur")
    public ResponseEntity<EmployeurDTO> inscription(@Valid @RequestBody EmployeurInscriptionDTO employeurInscriptionDTO){
        EmployeurDTO employeurDTO;
        try{
            employeurDTO = employeurService.inscription(employeurInscriptionDTO);
        } catch (EmployeurExistant | ConfirmationMotDePasseEchouer e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(employeurDTO,HttpStatus.CREATED);
    }
}
