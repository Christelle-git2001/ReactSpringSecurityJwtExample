package com.lacouf.rsbjwt.Exception;

public class DateFinAvantDateDebutException extends Exception {
    public DateFinAvantDateDebutException() {
        super("add_offer.errors.endDate_invalid");
    }
}