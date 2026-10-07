package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.security.exception.APIException;
import com.lacouf.rsbjwt.security.exception.AuthenticationException;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.ErreurDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ErreurDTO> handleAuthenticationException(Exception e) {
        logger.warn("Échec d'authentification : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErreurDTO("login.credentialsWrong"));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErreurDTO> handleUserNotFound(UserNotFoundException e) {
        logger.warn("Utilisateur non trouvé : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErreurDTO("error.user_not_found"));
    }

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

    @ExceptionHandler(NumeroTelephoneExistantException.class)
    public ResponseEntity<ErreurDTO> handleNumeroTelephoneExistant(NumeroTelephoneExistantException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErreurDTO> handleAPIException(APIException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(FichierCorrompuException.class)
    public ResponseEntity<ErreurDTO> handleFichierCorrompuException(FichierCorrompuException e) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(FichierTropVolumineuxException.class)
    public ResponseEntity<ErreurDTO> handleFichierTropVolumineuxException(FichierTropVolumineuxException e) {
        return ResponseEntity
                .status(HttpStatus.CONTENT_TOO_LARGE)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(FichierTypeInvalideException.class)
    public ResponseEntity<ErreurDTO> handleFichierTypeInvalideException(FichierTypeInvalideException e) {
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(DateFinAvantDateDebutException.class)
    public ResponseEntity<ErreurDTO> handleDateFinAvantDateDebutException(DateFinAvantDateDebutException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(FichierIntrouvableException.class)
    public ResponseEntity<ErreurDTO> handleFichierIntrouvableException(FichierIntrouvableException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(OffreIntrouvableException.class)
    public ResponseEntity<ErreurDTO> handleOffreIntrouvableException(
            OffreIntrouvableException e) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(OffreNonEnAttenteException.class)
    public ResponseEntity<ErreurDTO> handleOffreNonEnAttenteException(
            OffreNonEnAttenteException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(CommentaireRefusObligatoireException.class)
    public ResponseEntity<ErreurDTO> handleCommentaireRefusObligatoireException(
            CommentaireRefusObligatoireException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(OffreNonAutoriseeException.class)
    public ResponseEntity<ErreurDTO> handleOffreNonAutoriseeException(
            OffreNonAutoriseeException e) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(CurriculumVitaeIntrouvable.class)
    public ResponseEntity<ErreurDTO> handleCurriculumVitaeIntrouvable(CurriculumVitaeIntrouvable e){
        logger.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErreurDTO(e.getMessage()));
    }

    @ExceptionHandler(CurriculumVitaeNonEnAttente.class)
    public ResponseEntity<ErreurDTO> handleCurriculumVitaeNonEnAttente(CurriculumVitaeNonEnAttente e){
        logger.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErreurDTO(e.getMessage()));
    }
}