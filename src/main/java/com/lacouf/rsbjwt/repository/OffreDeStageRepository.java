package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.OffreDeStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OffreDeStageRepository extends JpaRepository<OffreDeStage, Long> {
    List<OffreDeStage> findByEmployeurId(Long employeurId);
}
