package com.lacouf.rsbjwt.Exception;

public class DepartementInvalideException extends Exception {
    public DepartementInvalideException(String value) {
        super("Département invalide : " + value);
    }
}
