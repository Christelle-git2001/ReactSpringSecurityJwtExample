package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CvEtudiantRepository extends JpaRepository<CvEtudiant, Long> {
    Optional<CvEtudiant> findByEtudiant(Etudiant etudiant);
    List<CvEtudiant> findByStatus(Statut statut);
}