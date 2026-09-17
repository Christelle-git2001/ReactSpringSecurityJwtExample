package com.lacouf.rsbjwt.model.Exceptions;

public class MotDePasseNonCorrespondantException extends Exception {
    public MotDePasseNonCorrespondantException() {
        super("Les mots de passe ne correspondent pas");
    }
}
