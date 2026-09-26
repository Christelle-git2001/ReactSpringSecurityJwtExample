package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OffreDeStage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String poste;
    @Column(nullable = false)
    private String statut; // "en attente", "acceptée", "refusée"
    @Column(nullable = false)
    private String salary;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private LocalDate firstDate;
    @Column(nullable = false)
    private LocalDate lastDate;
    private String filePath; // Optionnel
    private String messageRefus; // Rempli seulement si l'offre est refusée

    public OffreDeStage(String title, String salary, String description, LocalDate firstDate, LocalDate lastDate) {
        this.title = title;
        this.salary = salary;
        this.description = description;
        this.firstDate = firstDate;
        this.lastDate = lastDate;
    }

    @ManyToOne
    @JoinColumn(name = "employeur_id")
    private Employeur employeur;

}
