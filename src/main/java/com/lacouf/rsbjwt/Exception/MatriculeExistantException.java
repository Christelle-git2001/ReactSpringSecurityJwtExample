package com.lacouf.rsbjwt.Exception;

public class MatriculeExistantException extends Exception{
    public MatriculeExistantException(){
        super("error.matricule_exists");
    }
}
