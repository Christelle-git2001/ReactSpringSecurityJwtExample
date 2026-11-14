package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.Saison;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inscription",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_utilisateur_saison_annee",
                columnNames = {"utilisateur_id", "saison", "annee"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private UserApp utilisateur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Saison saison;

    @Column(name = "annee", nullable = false)
    private int annee;

    @Column(nullable = false)
    private LocalDateTime dateInscription;

    public Inscription(UserApp utilisateur, Saison saison, int annee) {
        this.utilisateur = utilisateur;
        this.saison = saison;
        this.annee = annee;
        this.dateInscription = LocalDateTime.now();
    }
}