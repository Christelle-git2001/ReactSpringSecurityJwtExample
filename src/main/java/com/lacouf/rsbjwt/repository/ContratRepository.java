package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.Contrat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContratRepository extends JpaRepository<Contrat, Long> {
    boolean existsByCandidatureId(Long candidatureId);

    Optional<Contrat> findByCandidatureId(Long candidatureId);
}
