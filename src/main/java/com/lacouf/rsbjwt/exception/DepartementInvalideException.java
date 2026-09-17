package com.lacouf.rsbjwt.exception;

public class DepartementInvalideException extends Exception {
    public DepartementInvalideException(String value) {
        super("Département invalide : " + value);
    }
}
