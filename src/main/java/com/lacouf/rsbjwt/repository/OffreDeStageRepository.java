package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.model.OffreCiblee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OffreDeStageRepository extends JpaRepository<OffreDeStage, Long> {

    List<OffreDeStage> findByStatut(Statut statut);

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
}