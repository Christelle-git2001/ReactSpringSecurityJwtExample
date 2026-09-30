package com.lacouf.rsbjwt.Exception;

public class FichierTropVolumineuxException extends RuntimeException {
    public FichierTropVolumineuxException() {
        super("error.fichier_volumineux");
    }
}
