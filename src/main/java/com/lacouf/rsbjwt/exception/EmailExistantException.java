package com.lacouf.rsbjwt.exception;

public class EmailExistantException extends Exception {
    public EmailExistantException() {
        super("ce courriel existe déjà");
    }
}
