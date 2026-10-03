package com.lacouf.rsbjwt.Exception;

public class CommentaireRefusObligatoireException extends Exception{
    public CommentaireRefusObligatoireException() {
        super("error.rejection_comment_required");
    }
}
