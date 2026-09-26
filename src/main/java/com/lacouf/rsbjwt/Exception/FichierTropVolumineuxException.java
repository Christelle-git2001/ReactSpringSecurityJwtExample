package com.lacouf.rsbjwt.Exception;

public class FichierTropVolumineuxException extends RuntimeException {
    public FichierTropVolumineuxException() {
        super("Le fichier est trop volumineux");
    }
}
