package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.EvaluationMillieuStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationMillieuStageRepository extends JpaRepository<EvaluationMillieuStage, Long> {
    Optional<EvaluationMillieuStage> findEvaluationMillieuStageByCandidature_Id(Long candidatureId);
}
