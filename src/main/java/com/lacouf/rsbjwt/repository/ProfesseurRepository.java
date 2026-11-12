package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Professeur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ProfesseurRepository extends JpaRepository<Professeur, Long> {

    Optional<Professeur> findByMatricule(String matricule);
    List<Professeur> findByEtudiantsIsEmpty();
}
