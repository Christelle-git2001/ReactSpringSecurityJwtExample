package com.lacouf.rsbjwt.Exception;

public class FichierCorrompuException extends RuntimeException {
    public FichierCorrompuException() {
        super("error.fichier_corrompu");
    }
}
