package com.lacouf.rsbjwt.Exceptions;

public class EmailExistantException extends Exception{
    public EmailExistantException(){
        super("ce courriel existe déjà");
    }
}
