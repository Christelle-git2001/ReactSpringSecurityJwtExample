package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.Contrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

import java.util.Optional;
import java.util.List;

public interface ContratRepository extends JpaRepository<Contrat, Long> {
    boolean existsByCandidatureId(Long candidatureId);

    Optional<Contrat> findByCandidatureId(Long candidatureId);

    @Query("SELECT c FROM Contrat c WHERE c.dateSignatureEtudiant IS NULL " +
            "OR c.dateSignatureEmployeur IS NULL OR c.dateSignatureGestionnaire IS NULL")
    List<Contrat> findNonEntierementSignes();

    List<Contrat> findByDateSignatureGestionnaireIsNullAndDateSignatureEtudiantIsNotNullAndDateSignatureEmployeurIsNotNull();

    String SIGNATURE_DANS_PERIODE = " WHERE (:signataire = 'ETUDIANT' AND c.dateSignatureEtudiant >= :debut AND c.dateSignatureEtudiant < :fin)"
            + " OR (:signataire = 'EMPLOYEUR' AND c.dateSignatureEmployeur >= :debut AND c.dateSignatureEmployeur < :fin)"
            + " OR (:signataire = 'GESTIONNAIRE' AND c.dateSignatureGestionnaire >= :debut AND c.dateSignatureGestionnaire < :fin)";

    @Query("SELECT c FROM Contrat c" + SIGNATURE_DANS_PERIODE)
    List<Contrat> findSignesEntre(@Param("signataire") String signataire,
                                @Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("SELECT count(c) FROM Contrat c" + SIGNATURE_DANS_PERIODE)
    long countSignesEntre(@Param("signataire") String signataire,
                         @Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
