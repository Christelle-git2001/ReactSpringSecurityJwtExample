package com.lacouf.rsbjwt.Exceptions;

import com.lacouf.rsbjwt.service.dto.ErreurDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailExistantException.class)
    public ResponseEntity<ErreurDTO> handleConflit(Exception e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(MotDePasseNonCorrespondantException.class)
    public ResponseEntity<ErreurDTO> handleBadRequest(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErreurDTO(e.getMessage()));
    }
}
