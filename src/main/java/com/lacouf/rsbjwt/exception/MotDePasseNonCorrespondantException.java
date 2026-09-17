package com.lacouf.rsbjwt.exception;

public class MotDePasseNonCorrespondantException extends Exception {
    public MotDePasseNonCorrespondantException() {
        super("Le mot de passe et sa confirmation diffèrent");
    }
}
