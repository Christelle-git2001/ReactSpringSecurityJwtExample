package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Professeur;

public record ProfesseurDto(long id, String firstName, String lastName, String email, String matricule, String phoneNumber, String department) {

    public  static ProfesseurDto of(Professeur professeur){
        return new ProfesseurDto(professeur.getId(), professeur.getFirstName(),professeur.getLastName(),
                professeur.getEmail(),professeur.getMatricule(),professeur.getPhoneNumber(),professeur.getDepartment());
    }
}
