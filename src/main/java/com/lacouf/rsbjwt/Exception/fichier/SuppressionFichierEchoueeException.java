package com.lacouf.rsbjwt.exception.fichier;

public class SuppressionFichierEchoueeException extends RuntimeException {
    public SuppressionFichierEchoueeException() { super("error.file_delete_failed"); }
}