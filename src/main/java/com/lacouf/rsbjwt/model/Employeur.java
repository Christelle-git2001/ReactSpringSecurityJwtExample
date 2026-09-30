package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.model.auth.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@DiscriminatorValue("EMPLOYEUR")
@NoArgsConstructor
public class Employeur extends UserApp{

    @Column(nullable = false)
    private String town;
    @Column(nullable = false)
    private String businessName;
    @Column(nullable = false)
    private SecteurActivite businessSector;

    @OneToMany(mappedBy = "employeur", cascade = CascadeType.ALL)
    private List<OffreDeStage> offres = new ArrayList<>();


    @Builder
    public Employeur(String firstName, String lastName, String phone, String email, String town, String businessName, SecteurActivite businessSector, String password
    ) {
        super(
                firstName,
                lastName,
                phone,
                Credentials.builder()
                        .email(email)
                        .password(password)
                        .role(Role.EMPLOYEUR)
                        .build()
        );
        this.town = town;
        this.businessName = businessName;
        this.businessSector = businessSector;
    }
}
