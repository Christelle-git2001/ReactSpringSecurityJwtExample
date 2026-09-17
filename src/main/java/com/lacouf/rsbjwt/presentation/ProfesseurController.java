package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.ProfesseurService;
import com.lacouf.rsbjwt.service.dto.InscriptionProfesseurDto;
import com.lacouf.rsbjwt.service.dto.ProfesseurDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/professeurs")
@CrossOrigin(origins = "http://localhost:5173")
public class ProfesseurController {

    private static  final Logger logger = LoggerFactory.getLogger(ProfesseurController.class);

    private final ProfesseurService professeurService ;

    public ProfesseurController(ProfesseurService professeurService) {
        this.professeurService = professeurService;
    }

    @PostMapping("inscription")
    public ResponseEntity<ProfesseurDto> creerCompte(@RequestBody InscriptionProfesseurDto newProfesseur){
        try {
            ProfesseurDto createdProfesseur = professeurService.creerCompte(newProfesseur);

            logger.info("{}", newProfesseur);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProfesseur);

        // TODO Exception Courriel Existant
        } catch (Exception e) {
            logger.info("{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        // TODO Exception Matricule Existant
       /* } catch (Exception e){
            logger.info("{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // TODO Exception le mot de passe et sa confirmation ne matchent pas
        } catch (Exception e){
            logger.info("{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } */
    }


}
