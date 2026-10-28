
package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Entrevue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntrevueRepository
        extends JpaRepository<Entrevue, Long> {

    Optional<Entrevue> findByCandidatureId(Long candidatureId);

    List<Entrevue> findByEmployeurId(Long employeurId);
}
