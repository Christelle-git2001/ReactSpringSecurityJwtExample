package com.lacouf.rsbjwt.Exception;

public class DateFinAvantDateDebutException extends Exception {
    public DateFinAvantDateDebutException() {
        super("La date de fin ne peut pas être antérieure à la date de début.");
    }
}