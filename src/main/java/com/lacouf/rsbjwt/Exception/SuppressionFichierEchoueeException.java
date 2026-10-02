package com.lacouf.rsbjwt.Exception;

public class SuppressionFichierEchoueeException extends RuntimeException {
    public SuppressionFichierEchoueeException() { super("error.file_delete_failed"); }
}
