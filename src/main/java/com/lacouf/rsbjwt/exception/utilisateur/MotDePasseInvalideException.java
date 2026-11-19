package com.lacouf.rsbjwt.exception.utilisateur;

public class MotDePasseInvalideException extends RuntimeException {
    public MotDePasseInvalideException() { super("error.password_invalid"); }
}