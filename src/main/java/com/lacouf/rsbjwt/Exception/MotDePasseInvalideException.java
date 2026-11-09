package com.lacouf.rsbjwt.Exception;

public class MotDePasseInvalideException extends RuntimeException {
    public MotDePasseInvalideException() {
        super("error.password.incorrect");
    }
}