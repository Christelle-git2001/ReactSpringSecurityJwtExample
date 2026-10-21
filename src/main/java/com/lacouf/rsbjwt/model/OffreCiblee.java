package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Statut;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder; // Import SuperBuilder

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("OFFRE_CIBLEE")
@Getter
@NoArgsConstructor
@SuperBuilder // <-- Change @Builder to @SuperBuilder
public class OffreCiblee extends OffreDeStage {

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = true)
    private Etudiant etudiant;

    public OffreCiblee(
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
            String rejectionComment,
            LocalDateTime createdAt,
            Etudiant etudiant
    ) {
        super(title, description, salary, domain, startDate, endDate, displayEndDate,
                statut, fileName, storagePath, employeur, rejectionComment, createdAt);

        this.etudiant = etudiant;
    }
}