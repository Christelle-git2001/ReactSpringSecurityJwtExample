package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.service.dto.SecteurEmployeurDTO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/gestionnaire")
@CrossOrigin(origins = "http://localhost:5173")
public class GestionnaireController {

    @GetMapping("/secteurEmployeur")
    public List<SecteurEmployeurDTO> recevoirTousSecteurs(){
        return Arrays.stream(SecteurActivite.values()).map(
                secteur -> new SecteurEmployeurDTO(secteur.name() , secteur.getLabel())
        ).toList();
    }
}
