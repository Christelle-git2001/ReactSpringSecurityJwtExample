package com.lacouf.rsbjwt.service.dto;


public record InscriptionProfesseurDto(String firstName, String lastName, String email, String password, String confirmPassword, String matricule, String phoneNumber, String department) {
}
