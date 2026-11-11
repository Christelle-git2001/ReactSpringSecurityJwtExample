package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscriptionRepository extends JpaRepository< Inscription, Long> {
}
