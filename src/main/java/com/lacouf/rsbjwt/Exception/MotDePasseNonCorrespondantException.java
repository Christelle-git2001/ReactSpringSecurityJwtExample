package com.lacouf.rsbjwt.Exception;

public class MotDePasseNonCorrespondantException extends Exception {
    public MotDePasseNonCorrespondantException() {
        super("Les mots de passe ne correspondent pas");
    }
}
