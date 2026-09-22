package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.model.auth.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ETUDIANT")
@Getter
@Setter
@NoArgsConstructor
public class Etudiant extends UserApp {

    @Column(unique = true, nullable = false)
    private String matricule;
    private String phoneNumber;
    private Departement department ;

    @Builder
    public Etudiant(String firstName, String lastName, String email, String phoneNumber,Departement department, String matricule,String password){
        super(
                firstName,
                lastName, phoneNumber,
                Credentials.builder()
                        .email(email)
                        .password(password)
                        .role(Role.ETUDIANT)
                        .build());
        this.matricule = matricule;
        this.department = department;

    }

}