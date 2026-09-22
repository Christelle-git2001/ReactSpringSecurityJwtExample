package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Professeur;

public record ProfesseurDTO(long id, String firstName, String lastName, String email, String matricule, String phoneNumber, String department) implements UserDTO {

    public  static ProfesseurDTO of(Professeur professeur){
        return new ProfesseurDTO(professeur.getId(), professeur.getFirstName(),professeur.getLastName(),
                professeur.getEmail(),professeur.getMatricule(),professeur.getPhoneNumber(),professeur.getDepartment().getLabel());
    }



    @Override
    public RoleDTO role() {
        return RoleDTO.PROFESSEUR;
    }

    public static ProfesseurDTO empty() {
        return new ProfesseurDTO(-1,null,null,null,null,null,null);
    }
}
