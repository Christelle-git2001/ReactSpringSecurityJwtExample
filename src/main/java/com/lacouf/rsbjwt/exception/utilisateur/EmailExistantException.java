package com.lacouf.rsbjwt.exception.utilisateur;

public class EmailExistantException extends Exception{
    public EmailExistantException(){ super("error.email_exists"); }
}