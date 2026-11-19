package com.lacouf.rsbjwt.exception.candidature;

public class ActionsCandidatureDesactiveesException extends Exception {
    public ActionsCandidatureDesactiveesException() { super("error.internship_found_locked"); }

    public ActionsCandidatureDesactiveesException(String cle) { super(cle); }
}