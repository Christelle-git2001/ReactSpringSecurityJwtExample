package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Enum.Saison;
import com.lacouf.rsbjwt.model.Inscription;
import com.lacouf.rsbjwt.model.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InscriptionRepository extends JpaRepository< Inscription, Long> {

    @Query("SELECT DISTINCT i.saison, i.annee FROM Inscription i ORDER BY i.annee DESC, i.saison DESC")
    List<Object[]> findSessionsDisponibles();

    boolean existsByUtilisateurAndSaisonAndAnnee(UserApp utilisateur, Saison saison, int annee);
}
