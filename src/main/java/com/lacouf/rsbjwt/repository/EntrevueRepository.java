
package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Entrevue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EntrevueRepository
        extends JpaRepository<Entrevue, Long> {

    Optional<Entrevue> findByCandidatureId(Long candidatureId);

    @Query("SELECT e FROM Entrevue e WHERE e.candidature.offreDeStage.employeur.id = :employeurId")
    List<Entrevue> findByEmployeurId(@Param("employeurId") Long employeurId);
}
