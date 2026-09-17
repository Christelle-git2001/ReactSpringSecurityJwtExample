package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Professeur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfesseurRepository extends JpaRepository<Professeur, Long> {
    Optional<Professeur> findByEmail(String email);

    Optional<Professeur> findByMatricule(String matricule);
}
