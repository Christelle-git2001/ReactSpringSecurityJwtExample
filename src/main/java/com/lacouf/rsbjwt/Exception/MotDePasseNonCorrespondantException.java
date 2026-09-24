package com.lacouf.rsbjwt.Exception;

public class MotDePasseNonCorrespondantException extends Exception {
    public MotDePasseNonCorrespondantException() {
        super("error.password_mismatch");
    }
}
