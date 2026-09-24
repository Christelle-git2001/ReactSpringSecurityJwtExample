package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.ErreurDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



import com.lacouf.rsbjwt.security.exception.APIException;
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmailExistantException.class)
    public ResponseEntity<ErreurDTO> handleEmailExistantException(EmailExistantException e) {
        logger.warn("Email déjà existant : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErreurDTO(e.getMessage())) ;
    }

    @ExceptionHandler(MatriculeExistantException.class)
    public ResponseEntity<ErreurDTO> handleMatriculeExistantException(MatriculeExistantException e) {
        logger.warn("Matricule déjà existant : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(MotDePasseNonCorrespondantException.class)
    public ResponseEntity<ErreurDTO> handleMotDePasseNonCorrespondantException(MotDePasseNonCorrespondantException e) {
        logger.warn("Mot de passe non correspondant : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(DepartementInvalideException.class)
    public ResponseEntity<ErreurDTO> handleDepartementInvalideException(DepartementInvalideException e) {
        logger.warn("Département invalide : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(NumeroTelephoneExistantException.class)
    public ResponseEntity<ErreurDTO> handleNumeroTelephoneExistant(
            NumeroTelephoneExistantException e) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErreurDTO(
                        e.getMessage()));
    }



    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErreurDTO> handleAPIException(APIException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErreurDTO> handleUserNotFound(Exception e){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErreurDTO(
                        e.getMessage()));
    }
}

