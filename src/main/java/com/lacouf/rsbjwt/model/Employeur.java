package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.model.auth.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@DiscriminatorValue("E")
@NoArgsConstructor
public class Employeur extends UserApp{

    @Column(nullable = false)
    private String town;
    @Column(nullable = false)
    private String businessName;
    @Column(nullable = false)
    private String businessType;
    @Column(nullable = false)
    private SecteurActivite businessSector;
    //TODO : Liste de personne responsable ?

    @Builder
    public Employeur(Long id, String firstName, String lastName, String town, String phone, String email, String password, String businessName, String businessType, SecteurActivite businessSector){
        super(
                id,
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
        this.businessType = businessType;
    }
}
