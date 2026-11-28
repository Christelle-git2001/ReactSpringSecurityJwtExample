package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.service.SessionService;
import com.lacouf.rsbjwt.service.dto.offre.OffreDeStageDTO;
import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException;
import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException.Code;

import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DonneesOffreService {
    private final OffreDeStageRepository offreDeStageRepository;
    private final SessionService sessionService;

    public DonneesOffreService(OffreDeStageRepository offreDeStageRepository, SessionService sessionService) {
        this.offreDeStageRepository = offreDeStageRepository;
        this.sessionService = sessionService;
    }

    public List<OffreDeStageDTO> obtenirOffresEnAttente() {
        List<OffreDeStage> offres =
                offreDeStageRepository.findByStatut(Statut.EN_ATTENTE);

        return sessionService.filtrerParSessionActuelle(
                        offres,
                        OffreDeStage::getCreatedAt
                )
                .stream()
                .map(OffreDeStageDTO::of)
                .toList();
    }

    public long compterOffresParStatut(Statut statut) throws ParametreDonneesInvalideException {
        if (statut == null) throw new ParametreDonneesInvalideException(Code.STATUT_OFFRE_OBLIGATOIRE);
        return offreDeStageRepository.countByStatut(statut);
    }

    public List<OffreDeStageDTO> obtenirOffresApprouvees() {
        List<OffreDeStage> offres =
                offreDeStageRepository.findByStatut(Statut.ACCEPTEE);

        return sessionService.filtrerParSessionActuelle(
                        offres,
                        OffreDeStage::getCreatedAt
                )
                .stream()
                .map(OffreDeStageDTO::of)
                .toList();
    }
}