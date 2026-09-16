package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.model.auth.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("PROFESSEUR")
@Getter
@NoArgsConstructor
public class Professeur extends UserApp{

    @Column(unique = true, nullable = false)
    private String matricule ;
    private String email;
    private String phoneNumber ;
    private String department ;

    @Builder
    public Professeur(Long id, String firstName, String lastName, String email, String password, String matricule, String phoneNumber, String department) {
        super(id, firstName, lastName, Credentials.builder().email(email).password(password).role(Role.PROFESSEUR).build());
        this.matricule = matricule;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.department = department;
    }
}
