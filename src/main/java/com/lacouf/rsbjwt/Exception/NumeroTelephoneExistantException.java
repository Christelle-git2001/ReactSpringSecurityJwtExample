package com.lacouf.rsbjwt.Exception;

public class NumeroTelephoneExistantException extends Exception {

    public NumeroTelephoneExistantException() {
        super("Le numéro de téléphone existe déjà.");
    }
}