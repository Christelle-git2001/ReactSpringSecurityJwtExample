package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.OffreDeStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OffreDeStageRepository extends JpaRepository<OffreDeStage, Long> {
    List<OffreDeStage> findByStatut(Statut statut);
    List<OffreDeStage> findAllByStatutAndDomainAndDisplayEndDateGreaterThanEqual(Statut statut, Departement domain, LocalDate date);
}
