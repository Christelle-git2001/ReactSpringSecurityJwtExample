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
    private String nomEntreprise;
    @Column(nullable = false)
    private String typeEntreprise;
    @Column(nullable = false)
    private SecteurActivite secteurActivite;
    //TODO : Liste de personne responsable ?

    @Builder
    public Employeur(Long id,String prenom, String nom, String ville, String telephone, String email, String password, String nomEntreprise, String typeEntreprise, SecteurActivite secteurActivite){
        super(
                id,
                prenom,
                nom,
                ville,
                telephone,
                Credentials.builder()
                        .email(email)
                        .password(password)
                        .role(Role.EMPLOYEUR)
                        .build()
        );
        this.nomEntreprise = nomEntreprise;
        this.secteurActivite = secteurActivite;
        this.typeEntreprise = typeEntreprise;
    }
}
