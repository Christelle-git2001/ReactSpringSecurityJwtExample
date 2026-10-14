package com.lacouf.rsbjwt.Exception;

public class UtilisateurIntrouvableException extends Exception {
    public UtilisateurIntrouvableException() {
        super("error.user_not_found");
    }
}
