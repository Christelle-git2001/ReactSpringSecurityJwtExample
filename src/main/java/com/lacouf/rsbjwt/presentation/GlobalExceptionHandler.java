package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.DepartementInvalideException;
import com.lacouf.rsbjwt.Exception.EmailExistantException;
import com.lacouf.rsbjwt.Exception.MatriculeExistantException;
import com.lacouf.rsbjwt.Exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.service.dto.ErreurDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailExistantException.class)
    public ResponseEntity<ErreurDTO> handleEmailExistantException(EmailExistantException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(MatriculeExistantException.class)
    public ResponseEntity<ErreurDTO> handleMatriculeExistantException(MatriculeExistantException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(MotDePasseNonCorrespondantException.class)
    public ResponseEntity<ErreurDTO> handleMotDePasseNonCorrespondantException(MotDePasseNonCorrespondantException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(DepartementInvalideException.class)
    public ResponseEntity<ErreurDTO> handleDepartementInvalideException(DepartementInvalideException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErreurDTO(e.getMessage()));
    }
}

