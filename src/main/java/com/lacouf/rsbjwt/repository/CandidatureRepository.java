package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.OffreDeStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    Optional<Candidature> findByIdAndEtudiantId(Long id, Long etudiantId);

    boolean existsByEtudiantAndOffreDeStage(Etudiant etudiant, OffreDeStage offreDeStage);

    List<Candidature> findByOffreDeStage(OffreDeStage offreDeStage);

    List<Candidature> findByEtudiant(Etudiant etudiant);

    List<Candidature> findByOffreDeStage_Employeur(Employeur employeur);
    List<Candidature> findByStatutAndOffreDeStageEmployeurId(StatutCandidature statut, Long emplyeurId);

    List<Candidature> findByStatut(StatutCandidature statut);
    long countByStatut(StatutCandidature statut);

    @Query("SELECT c FROM Candidature c WHERE c.statut = :statut " +
            "AND NOT EXISTS (SELECT contrat.id FROM Contrat contrat WHERE contrat.candidature = c)")
    List<Candidature> findByStatutSansContrat(@Param("statut") StatutCandidature statut);

    List<Candidature> findAllByEtudiantId(Long etudiantId);


}
