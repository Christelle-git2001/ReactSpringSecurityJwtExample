package com.lacouf.rsbjwt.Exception;

public class EmailExistantException extends Exception{
    public EmailExistantException(){
        super("ce courriel existe déjà");
    }
}
