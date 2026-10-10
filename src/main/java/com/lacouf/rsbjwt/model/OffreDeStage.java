package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Statut;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class OffreDeStage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double salary;

    @Column(nullable = false)
    private Departement domain;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalDate displayEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut;

    private String fileName;
    private String storagePath;

    @ManyToOne
    @JoinColumn(name = "employeur_id")
    private Employeur employeur;

    private String rejectionComment;

    @Builder
    public OffreDeStage(
            String title,
            String description,
            Double salary,
            Departement domain,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate displayEndDate,
            Statut statut,
            String fileName,
            String storagePath,
            Employeur employeur,
            String rejectionComment
    ) {
        this.title = title;
        this.description = description;
        this.salary = salary;
        this.domain = domain;
        this.startDate = startDate;
        this.endDate = endDate;
        this.displayEndDate = displayEndDate;
        this.statut = statut;
        this.fileName = fileName;
        this.employeur = employeur;
        this.storagePath = storagePath;
        this.rejectionComment = rejectionComment;
    }

}
