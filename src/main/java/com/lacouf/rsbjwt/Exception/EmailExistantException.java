package com.lacouf.rsbjwt.Exception;

public class EmailExistantException extends Exception{
    public EmailExistantException(){
        super("error.email_exists");
    }
}
