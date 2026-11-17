package com.lacouf.rsbjwt.exception.dates;

public class DateAvantAujourdhuiException extends Exception {
    public DateAvantAujourdhuiException() { super("add_offer.errors.start_date_before_today"); }
}