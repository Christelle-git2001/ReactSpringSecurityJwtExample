package com.lacouf.rsbjwt.service.dto;

public class ErreurDTO {
    private final String message;

    public ErreurDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
