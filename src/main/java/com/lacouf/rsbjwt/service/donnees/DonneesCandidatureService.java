package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.service.SessionService;
import com.lacouf.rsbjwt.service.dto.candidature.CandidatureDTO;
import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException;
import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException.Code;

import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.repository.CandidatureRepository;
import com.lacouf.rsbjwt.repository.ContratRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DonneesCandidatureService {
    private final CandidatureRepository candidatureRepository;
    private final ContratRepository contratRepository;
    private final SessionService sessionService;

    public DonneesCandidatureService(CandidatureRepository candidatureRepository, ContratRepository contratRepository,
                                        SessionService sessionService) {
        this.candidatureRepository = candidatureRepository;
        this.contratRepository = contratRepository;
        this.sessionService = sessionService;
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

    public List<CandidatureDTO> obtenirCandidaturesEnAttenteEntrevue() {
        List<Candidature> candidatures =
                candidatureRepository.findByStatut(StatutCandidature.RETENUE_POUR_ENTREVUE);

        return sessionService.filtrerParSessionActuelle(
                        candidatures,
                        candidature -> candidature.getOffreDeStage().getCreatedAt()
                )
                .stream()
                .map(CandidatureDTO::of)
                .toList();
    }

    public List<CandidatureDTO> obtenirCandidaturesSansEvaluation() {
        List<Candidature> candidatures =
                candidatureRepository.findCandidaturesSansEvaluation(
                        StatutCandidature.STAGE_TROUVE
                );

        return sessionService.filtrerParSessionActuelle(
                        candidatures,
                        candidature -> candidature.getOffreDeStage().getCreatedAt()
                )
                .stream()
                .map(candidature -> CandidatureDTO.of(candidature, null))
                .toList();
    }

    public List<CandidatureDTO> obtenirCandidaturesSansEvaluationMilieuStage() {
        List<Candidature> candidatures =
                candidatureRepository.findCandidaturesSansEvaluationMilieuStage(
                        StatutCandidature.STAGE_TROUVE
                );

        return sessionService.filtrerParSessionActuelle(
                        candidatures,
                        candidature -> candidature.getOffreDeStage().getCreatedAt()
                )
                .stream()
                .map(candidature -> CandidatureDTO.of(candidature, null))
                .toList();
    }
}