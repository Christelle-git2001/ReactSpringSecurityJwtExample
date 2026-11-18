package com.lacouf.rsbjwt.exception.entrevue;

public class ConvocationExistanteException extends Exception {
    public ConvocationExistanteException() { super("error.convocation_already_exists"); }
}