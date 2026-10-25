package com.lacouf.rsbjwt.Exception;

public class CandidatureIntrouvableException extends Exception{
    public CandidatureIntrouvableException() {
        super("error.candidature_not_found");
    }
}
