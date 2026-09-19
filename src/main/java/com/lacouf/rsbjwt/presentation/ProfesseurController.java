package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.DepartementInvalideException;
import com.lacouf.rsbjwt.Exception.EmailExistantException;
import com.lacouf.rsbjwt.Exception.MatriculeExistantException;
import com.lacouf.rsbjwt.Exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.service.ProfesseurService;
import com.lacouf.rsbjwt.service.dto.InscriptionProfesseurDTO;
import com.lacouf.rsbjwt.service.dto.ProfesseurDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public ResponseEntity<ProfesseurDTO> creerCompte(@RequestBody InscriptionProfesseurDTO newProfesseur){
        try {
            ProfesseurDTO createdProfesseur = professeurService.inscrireProfesseur(newProfesseur);

            logger.info("{}", newProfesseur);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProfesseur);

        } catch (EmailExistantException e) {
            logger.info("{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
         catch (MatriculeExistantException e){
            logger.info("{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        catch (MotDePasseNonCorrespondantException e){
            logger.info("{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        catch (DepartementInvalideException e){
            logger.info("{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


}
