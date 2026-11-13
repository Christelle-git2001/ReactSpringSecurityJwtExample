package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface CvEtudiantRepository extends JpaRepository<CvEtudiant, Long> {
    Optional<CvEtudiant> findByEtudiant(Etudiant etudiant);
    List<CvEtudiant> findByStatut(Statut statut);
    long countByStatut(Statut statut);
    List<CvEtudiant> findByUploadDateGreaterThanEqualAndUploadDateLessThanOrderByUploadDateAsc(LocalDateTime debut, LocalDateTime fin);
    long countByUploadDateGreaterThanEqualAndUploadDateLessThan(LocalDateTime debut, LocalDateTime fin);
}