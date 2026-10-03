package com.lacouf.rsbjwt.Exception;

public class DateFinAvantDateDebutException extends Exception {
    public DateFinAvantDateDebutException() {
        super("error.date_fin_anterieure_date_debut");
    }
}