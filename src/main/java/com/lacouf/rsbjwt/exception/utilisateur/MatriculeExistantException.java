package com.lacouf.rsbjwt.exception.utilisateur;

public class MatriculeExistantException extends Exception{
    public MatriculeExistantException(){ super("error.matricule_exists"); }
}