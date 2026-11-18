package com.lacouf.rsbjwt.exception.utilisateur;

public class DepartementInvalideException extends Exception {
    public DepartementInvalideException(String value) { super("error.department_invalid"); }
}