package com.lacouf.rsbjwt.exception.commun;

public class CommentaireRefusObligatoireException extends Exception{
    public CommentaireRefusObligatoireException() { super("error.rejection_comment_required"); }
}