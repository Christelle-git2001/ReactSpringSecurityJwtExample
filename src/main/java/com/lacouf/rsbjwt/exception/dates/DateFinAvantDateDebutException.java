package com.lacouf.rsbjwt.exception.dates;

public class DateFinAvantDateDebutException extends Exception {
    public DateFinAvantDateDebutException() { super("add_offer.errors.endDate_invalid"); }
}