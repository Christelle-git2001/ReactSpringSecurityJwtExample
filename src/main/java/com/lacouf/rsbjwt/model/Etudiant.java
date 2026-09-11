package com.lacouf.rsbjwt.model;

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
@DiscriminatorValue("E")
@Getter
@Setter
@NoArgsConstructor
public class Etudiant extends UserApp {

    @Column(unique = true, nullable = false)
    private String matricule;

    @Builder
    public Etudiant(Long id, String firstName, String lastName, String email, String password, String matricule){
        super(id,
                firstName,
                lastName,
                Credentials.builder()
                        .email(email)
                        .password(password)
                        .role(Role.ETUDIANT)
                        .build());
        this.matricule = matricule;

    }

}