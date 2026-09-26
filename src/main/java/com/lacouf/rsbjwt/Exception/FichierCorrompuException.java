package com.lacouf.rsbjwt.Exception;

public class FichierCorrompuException extends RuntimeException {
    public FichierCorrompuException() {
        super("Le fichier est corrompu ou illisible.");
    }
}
