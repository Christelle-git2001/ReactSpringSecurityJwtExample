package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.StatutEtudiant;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.model.auth.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'EN_RECHERCHE'")
    private StatutEtudiant statut = StatutEtudiant.EN_RECHERCHE;

    @OneToOne(mappedBy = "etudiant")
    private CvEtudiant cv;

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OffreCiblee> offresCiblees = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professeur_id")
    private Professeur professeur;


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
