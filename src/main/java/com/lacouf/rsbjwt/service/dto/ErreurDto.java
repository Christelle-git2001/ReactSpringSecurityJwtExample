package com.lacouf.rsbjwt.service.dto;

public class ErreurDto {
    private String message;

    public ErreurDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
