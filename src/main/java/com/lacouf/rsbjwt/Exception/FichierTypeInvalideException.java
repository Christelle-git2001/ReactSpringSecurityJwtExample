package com.lacouf.rsbjwt.Exception;

public class FichierTypeInvalideException extends RuntimeException {
    public FichierTypeInvalideException(String message) {
        super("Le type de fichier est invalide");
    }
}
