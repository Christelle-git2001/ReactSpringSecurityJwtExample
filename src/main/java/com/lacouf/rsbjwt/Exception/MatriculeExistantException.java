package com.lacouf.rsbjwt.Exception;

public class MatriculeExistantException extends Exception{
    public MatriculeExistantException(){
        super("Ce matricule existe déjà");
    }
}
