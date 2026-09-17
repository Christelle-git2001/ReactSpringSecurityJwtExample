package com.lacouf.rsbjwt.Exceptions;

public class MotDePasseNonCorrespondantException extends Exception {
    public MotDePasseNonCorrespondantException() {
        super("Les mots de passe ne correspondent pas");
    }
}
