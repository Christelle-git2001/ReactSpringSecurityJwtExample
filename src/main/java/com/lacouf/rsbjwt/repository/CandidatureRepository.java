package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.OffreDeStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    boolean existsByEtudiantAndOffreDeStage(Etudiant etudiant, OffreDeStage offreDeStage);

    List<Candidature> findByOffreDeStage(OffreDeStage offreDeStage);

    List<Candidature> findByEtudiant(Etudiant etudiant);
}
