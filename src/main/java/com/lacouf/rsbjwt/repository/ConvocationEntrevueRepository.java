
package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.ConvocationEntrevue;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConvocationEntrevueRepository
        extends JpaRepository<ConvocationEntrevue, Long> {

    Optional<ConvocationEntrevue> findByCandidatureId(Long candidatureId);
}
