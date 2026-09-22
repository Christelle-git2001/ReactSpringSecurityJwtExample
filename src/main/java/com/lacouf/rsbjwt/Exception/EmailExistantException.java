package com.lacouf.rsbjwt.Exception;

public class EmailExistantException extends Exception{
    public EmailExistantException(){
        super("Ce courriel existe déjà");
    }
}
