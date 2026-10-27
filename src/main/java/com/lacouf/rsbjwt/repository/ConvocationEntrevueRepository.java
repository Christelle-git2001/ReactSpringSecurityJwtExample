
package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.ConvocationEntrevue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConvocationEntrevueRepository
        extends JpaRepository<ConvocationEntrevue, Long> {

    Optional<ConvocationEntrevue> findByCandidatureId(Long candidatureId);
    Optional<ConvocationEntrevue> findByIdAndCandidatureEtudiantId(
            Long convocationId,
            Long etudiantId
    );
}
