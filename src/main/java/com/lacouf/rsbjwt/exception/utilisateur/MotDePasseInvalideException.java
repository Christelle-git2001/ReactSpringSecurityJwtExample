package com.lacouf.rsbjwt.exception.utilisateur;

public class MotDePasseInvalideException extends Exception {
    public MotDePasseInvalideException() { super("error.password_invalid"); }
}