package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DonneesOffreService {
    private final OffreDeStageRepository offreDeStageRepository;

    public DonneesOffreService(OffreDeStageRepository offreDeStageRepository) {
        this.offreDeStageRepository = offreDeStageRepository;
    }

    public List<OffreDeStageDTO> obtenirOffresEnAttente() {
        return offreDeStageRepository.findByStatut(Statut.EN_ATTENTE)
                .stream().map(OffreDeStageDTO::of).toList();
    }

    public long compterOffresParStatut(Statut statut) {
        if (statut == null) throw new IllegalArgumentException("Le statut de l'offre est obligatoire.");
        return offreDeStageRepository.countByStatut(statut);
    }

    public List<OffreDeStageDTO> obtenirOffresApprouvees() {
        return offreDeStageRepository.findByStatut(Statut.ACCEPTEE)
                .stream().map(OffreDeStageDTO::of).toList();
    }
}