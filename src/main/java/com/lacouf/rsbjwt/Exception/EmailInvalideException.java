package com.lacouf.rsbjwt.Exception;

public class EmailInvalideException extends RuntimeException {
    public EmailInvalideException(String message) {
        super("error.email_invalid");
    }
}
