package com.lacouf.rsbjwt.exception.utilisateur;

public class NumeroTelephoneExistantException extends Exception {
    public NumeroTelephoneExistantException() { super("error.phone_exists"); }
}