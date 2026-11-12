package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.Contrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface ContratRepository extends JpaRepository<Contrat, Long> {
    boolean existsByCandidatureId(Long candidatureId);

    Optional<Contrat> findByCandidatureId(Long candidatureId);

    @Query("SELECT c FROM Contrat c WHERE c.dateSignatureEtudiant IS NULL " +
            "OR c.dateSignatureEmployeur IS NULL OR c.dateSignatureGestionnaire IS NULL")
    List<Contrat> findNonEntierementSignes();

    List<Contrat> findByDateSignatureGestionnaireIsNullAndDateSignatureEtudiantIsNotNullAndDateSignatureEmployeurIsNotNull();
}
