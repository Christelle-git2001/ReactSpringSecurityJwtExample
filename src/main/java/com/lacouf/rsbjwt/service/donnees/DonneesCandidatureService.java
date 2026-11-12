package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.repository.CandidatureRepository;
import com.lacouf.rsbjwt.service.dto.CandidatureDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DonneesCandidatureService {
    private final CandidatureRepository candidatureRepository;

    public DonneesCandidatureService(CandidatureRepository candidatureRepository) {
        this.candidatureRepository = candidatureRepository;
    }

    public List<CandidatureDTO> obtenirStagesSansContrat() {
        return candidatureRepository.findByStatutSansContrat(StatutCandidature.STAGE_TROUVE)
                .stream().map(CandidatureDTO::of).toList();
    }
}