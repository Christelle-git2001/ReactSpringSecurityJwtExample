package com.lacouf.rsbjwt.Exception;

public class OffreNonAutoriseeException extends Exception {

    public OffreNonAutoriseeException() {
        super("error.offre_not_owned");
    }
}