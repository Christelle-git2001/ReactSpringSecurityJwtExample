package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.StatutOffre;
import jakarta.persistence.*;
import lombok.*;

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
    private StatutOffre statut; // "en attente", "acceptée", "refusée"
    @Column(nullable = false)
    private Double salary;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private LocalDate firstDate;
    @Column(nullable = false)
    private LocalDate lastDate;
    private String filePath; // Optionnel
    private String messageRefus; // Rempli seulement si l'offre est refusée

    @Builder
    public OffreDeStage(String title, Double salary,String poste,StatutOffre statut, String description, LocalDate firstDate, LocalDate lastDate,String filePath) {
        this.title = title;
        this.salary = salary;
        this.poste = poste;
        this.statut = statut;
        this.description = description;
        this.firstDate = firstDate;
        this.lastDate = lastDate;
        this.filePath = filePath;
    }

    @ManyToOne
    @JoinColumn(name = "employeur_id")
    private Employeur employeur;

}
