package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.model.OffreCiblee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OffreDeStageRepository extends JpaRepository<OffreDeStage, Long> {
    List<OffreDeStage> findByStatut(Statut statut);
    long countByStatut(Statut statut);
    List<OffreDeStage> findByApprovedAtGreaterThanEqualAndApprovedAtLessThanOrderByApprovedAtAsc(LocalDateTime debut, LocalDateTime fin);
    long countByApprovedAtGreaterThanEqualAndApprovedAtLessThan(LocalDateTime debut, LocalDateTime fin);
    List<OffreDeStage> findByDisplayEndDateAfterOrderByDisplayEndDateAsc(LocalDate date);
    long countByDisplayEndDateAfter(LocalDate date);
    List<OffreDeStage> findByDisplayEndDateBetweenOrderByDisplayEndDateAsc(LocalDate debut, LocalDate fin);
    List<OffreDeStage> findByStartDateBetweenOrderByStartDateAsc(LocalDate debut, LocalDate fin);
    long countByCandidaturesIsEmptyAndDisplayEndDateIsAfter(LocalDate fin);

    @Query("SELECT COUNT(DISTINCT o) FROM OffreDeStage o JOIN o.candidatures c WHERE c.statut = com.lacouf.rsbjwt.model.Enum.StatutCandidature.STAGE_TROUVE")
    long countOffresComblees();

    @Query("SELECT o FROM OffreDeStage o WHERE o.statut = :statut " +
            "AND o.domain = :domain " +
            "AND o.displayEndDate >= :today " +
            "AND TYPE(o) = OffreDeStage")
    List<OffreDeStage> findOffresGeneralesDisponibles(
            @Param("statut") Statut statut,
            @Param("domain") Departement domain,
            @Param("today") LocalDate date
    );

    @Query("SELECT o FROM OffreCiblee o WHERE o.statut = :statut " +
            "AND o.domain = :domain " +
            "AND o.displayEndDate >= :today " +
            "AND o.etudiant = :etudiant")
    List<OffreCiblee> findOffresCibleesPourEtudiant(
            @Param("statut") Statut statut,
            @Param("domain") Departement domain,
            @Param("today") LocalDate today,
            @Param("etudiant") Etudiant etudiant
    );

    @Query("""
    SELECT COUNT(o) FROM OffreDeStage o
    WHERE o.displayEndDate > :date
      AND o.statut = com.lacouf.rsbjwt.model.Enum.Statut.ACCEPTEE
""")
    long countByDisplayEndDateAfterAndStatutAcceptee(@Param("date") LocalDate date);
}