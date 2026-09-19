package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Professeur;

public record ProfesseurDTO(long id, String firstName, String lastName, String email, String matricule, String phoneNumber, String department) {

    public  static ProfesseurDTO of(Professeur professeur){
        return new ProfesseurDTO(professeur.getId(), professeur.getFirstName(),professeur.getLastName(),
                professeur.getEmail(),professeur.getMatricule(),professeur.getPhoneNumber(),professeur.getDepartment().getLabel());
    }
}
