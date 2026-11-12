package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InscriptionRepository extends JpaRepository< Inscription, Long> {

    @Query("SELECT DISTINCT i.saison, i.annee FROM Inscription i ORDER BY i.annee DESC, i.saison DESC")
    List<Object[]> findSessionsDisponibles();
}
