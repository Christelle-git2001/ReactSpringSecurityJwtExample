package com.lacouf.rsbjwt.Exception;

public class NumeroTelephoneExistantException extends Exception {

    public NumeroTelephoneExistantException() {
        super("error.phone_exists");
    }
}