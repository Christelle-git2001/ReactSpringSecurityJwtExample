package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.dto.SecteurEmployeurDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gestionnaire")
@CrossOrigin(origins = "http://localhost:5173")
public class GestionnaireController {


    private final GestionnaireService gestionnaireService;

    public GestionnaireController(GestionnaireService gestionnaireService) {
        this.gestionnaireService = gestionnaireService;
    }


    @GetMapping("/secteurEmployeur")
    public ResponseEntity<List<SecteurEmployeurDTO>> getSecteurs() {
        List<SecteurEmployeurDTO> secteurs = gestionnaireService.getAllSecteurs();
        return ResponseEntity.ok(secteurs);
    }
}
