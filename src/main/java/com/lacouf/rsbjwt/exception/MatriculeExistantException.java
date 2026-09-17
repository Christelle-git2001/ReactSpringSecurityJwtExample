package com.lacouf.rsbjwt.exception;

public class MatriculeExistantException extends Exception {
    public MatriculeExistantException() {
        super("Matricule existant");
    }
}
