package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Statut;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("OFFRE_CIBLEE")
@Getter
@NoArgsConstructor
public class OffreCiblee extends OffreDeStage {

    private Boolean isTarget;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @Builder
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
            Boolean isTarget,
            Etudiant etudiant
    ) {
        super(title, description, salary, domain, startDate, endDate, displayEndDate,
                statut, fileName, storagePath, employeur, rejectionComment, createdAt);

        this.isTarget = isTarget;
        this.etudiant = etudiant;
    }
}

