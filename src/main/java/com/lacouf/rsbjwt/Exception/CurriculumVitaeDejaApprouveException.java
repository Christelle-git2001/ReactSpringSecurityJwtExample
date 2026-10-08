package com.lacouf.rsbjwt.Exception;

public class CurriculumVitaeDejaApprouveException extends Exception {
    public CurriculumVitaeDejaApprouveException()  {
        super("cv.cv_already_approved");
    }
}
