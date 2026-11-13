package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.Exception.ParametreDonneesInvalideException;
import com.lacouf.rsbjwt.Exception.ParametreDonneesInvalideException.Code;

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

    public long compterCandidaturesParStatut(StatutCandidature statut) throws ParametreDonneesInvalideException {
        if (statut == null) throw new ParametreDonneesInvalideException(Code.STATUT_CANDIDATURE_OBLIGATOIRE);
        return candidatureRepository.countByStatut(statut);
    }

    public List<CandidatureDTO> obtenirCandidaturesParStatut(StatutCandidature statut) throws ParametreDonneesInvalideException {
        if (statut == null) throw new ParametreDonneesInvalideException(Code.STATUT_CANDIDATURE_OBLIGATOIRE);
        return candidatureRepository.findByStatut(statut).stream()
                .map(candidature -> CandidatureDTO.of(candidature,
                        statut == StatutCandidature.CONTRAT_CREE
                                ? contratRepository.findByCandidatureId(candidature.getId()).orElse(null)
                                : null))
                .toList();
    }
}