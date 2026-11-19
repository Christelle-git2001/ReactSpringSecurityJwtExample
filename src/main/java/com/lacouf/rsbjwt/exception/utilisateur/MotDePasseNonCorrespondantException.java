package com.lacouf.rsbjwt.exception.utilisateur;

public class MotDePasseNonCorrespondantException extends Exception {
    public MotDePasseNonCorrespondantException() { super("error.password_mismatch"); }
}