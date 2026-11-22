package com.lacouf.rsbjwt.exception.utilisateur;

public class UtilisateurIntrouvableException extends Exception {
    public UtilisateurIntrouvableException() { super("error.user_not_found"); }
}