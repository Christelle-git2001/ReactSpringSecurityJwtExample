package com.lacouf.rsbjwt.Exception;

public class OffreIntrouvableException extends Exception{
    public OffreIntrouvableException() {
        super("error.offre_not_found");
    }
}
