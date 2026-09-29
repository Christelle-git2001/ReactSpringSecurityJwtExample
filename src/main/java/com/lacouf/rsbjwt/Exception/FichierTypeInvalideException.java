package com.lacouf.rsbjwt.Exception;

public class FichierTypeInvalideException extends RuntimeException {
    public FichierTypeInvalideException() {
        super("Le type de fichier est invalide");
    }
}
