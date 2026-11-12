package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.repository.CandidatureRepository;
import com.lacouf.rsbjwt.repository.ContratRepository;
import com.lacouf.rsbjwt.service.dto.CandidatureDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DonneesCandidatureService {
    private final CandidatureRepository candidatureRepository;
    private final ContratRepository contratRepository;

    public DonneesCandidatureService(CandidatureRepository candidatureRepository, ContratRepository contratRepository) {
        this.candidatureRepository = candidatureRepository;
        this.contratRepository = contratRepository;
    }

    public List<CandidatureDTO> obtenirStagesSansContrat() {
        return candidatureRepository.findByStatutSansContrat(StatutCandidature.STAGE_TROUVE)
                .stream().map(CandidatureDTO::of).toList();
    }

    public long compterCandidaturesParStatut(StatutCandidature statut) {
        if (statut == null) throw new IllegalArgumentException("Le statut de candidature est obligatoire.");
        return candidatureRepository.countByStatut(statut);
    }

    public List<CandidatureDTO> obtenirCandidaturesParStatut(StatutCandidature statut) {
        if (statut == null) throw new IllegalArgumentException("Le statut de candidature est obligatoire.");
        return candidatureRepository.findByStatut(statut).stream()
                .map(candidature -> CandidatureDTO.of(candidature,
                        statut == StatutCandidature.CONTRAT_CREE
                                ? contratRepository.findByCandidatureId(candidature.getId()).orElse(null)
                                : null))
                .toList();
    }
}