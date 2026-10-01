package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.model.auth.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("ETUDIANT")
@Getter
@Setter
@NoArgsConstructor
public class Etudiant extends UserApp {

    @Column(unique = true, nullable = false)
    private String matricule;
    private Departement department ;

    @OneToMany(mappedBy = "etudiant")
    private List<CvEtudiant> cvs = new ArrayList<>();

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