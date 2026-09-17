package com.lacouf.rsbjwt.model.Exceptions;

public class EmailExistantException extends Exception{
    public EmailExistantException(){
        super("ce courriel existe déjà");
    }
}
