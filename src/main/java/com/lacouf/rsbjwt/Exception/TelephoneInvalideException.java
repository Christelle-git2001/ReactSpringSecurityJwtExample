package com.lacouf.rsbjwt.Exception;

public class TelephoneInvalideException extends Exception {
    public TelephoneInvalideException(String message) {
        super("error.phone_invalid");
    }
}
