package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Offre {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String domain;
    @Column(nullable = false)
    private String address;
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

    public Offre(String title, String domain, String address, String salary, String description, LocalDate firstDate, LocalDate lastDate) {
        this.title = title;
        this.domain = domain;
        this.address = address;
        this.salary = salary;
        this.description = description;
        this.firstDate = firstDate;
        this.lastDate = lastDate;
    }

    @ManyToOne
    @JoinColumn(name = "entreprise_id")
    private Employeur employeur;

}
