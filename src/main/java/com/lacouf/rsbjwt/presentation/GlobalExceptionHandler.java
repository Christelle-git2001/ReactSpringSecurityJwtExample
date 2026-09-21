package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.service.dto.ErreurDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmailExistantException.class)
    public ResponseEntity<ErreurDTO> handleEmailExistantException(EmailExistantException e) {
        logger.warn("Email déjà existant : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErreurDTO(
                        "Cet email est déjà utilisé.",
                        "This email is already in use."
                ));
    }

    @ExceptionHandler(MatriculeExistantException.class)
    public ResponseEntity<ErreurDTO> handleMatriculeExistantException(MatriculeExistantException e) {
        logger.warn("Matricule déjà existant : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body( new ErreurDTO(
                        "Ce matricule est déjà utilisé.",
                        "This registration number is already in use."
                ));
    }

    @ExceptionHandler(MotDePasseNonCorrespondantException.class)
    public ResponseEntity<ErreurDTO> handleMotDePasseNonCorrespondantException(MotDePasseNonCorrespondantException e) {
        logger.warn("Mot de passe non correspondant : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErreurDTO(
                        "Les mots de passe ne correspondent pas.",
                        "Passwords do not match."
                ));
    }

    @ExceptionHandler(DepartementInvalideException.class)
    public ResponseEntity<ErreurDTO> handleDepartementInvalideException(DepartementInvalideException e) {
        logger.warn("Département invalide : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErreurDTO(
                        "Le département fourni est invalide.",
                        "The provided department is invalid."
                ));
    }

    @ExceptionHandler(NumeroTelephoneExistantException.class)
    public ResponseEntity<ErreurDTO> handleNumeroTelephoneExistant(
            NumeroTelephoneExistantException e) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErreurDTO(
                        e.getMessage(),
                        "Phone number already exists."
                ));
    }
}

