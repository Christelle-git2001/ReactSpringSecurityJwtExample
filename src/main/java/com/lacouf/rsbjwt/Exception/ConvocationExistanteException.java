
package com.lacouf.rsbjwt.Exception;

public class ConvocationExistanteException extends Exception {

    public ConvocationExistanteException() {
        super("error.convocation_already_exists");
    }
}
