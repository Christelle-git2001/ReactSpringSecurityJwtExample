package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.CommentaireRefusObligatoireException;
import com.lacouf.rsbjwt.Exception.OffreIntrouvableException;
import com.lacouf.rsbjwt.Exception.OffreNonEnAttenteException;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Enum.StatutOffre;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import com.lacouf.rsbjwt.service.dto.DepartementDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.SecteurEmployeurDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class GestionnaireService {
    private final OffreDeStageRepository offreDeStageRepository;

    public GestionnaireService(OffreDeStageRepository offreDeStageRepository){
        this.offreDeStageRepository = offreDeStageRepository;

    }
    public List<SecteurEmployeurDTO> getAllSecteurs() {
        return Arrays.stream(SecteurActivite.values())
                .map(secteur -> new SecteurEmployeurDTO(
                        secteur.name(),
                        secteur.getLabel()
                ))
                .toList();
    }

    public List<DepartementDTO> getAllDepartements() {
        return Arrays.stream(Departement.values())
                .map(departement -> new DepartementDTO(
                        departement.name(),
                        departement.getLabel()
                ))
                .toList();
    }

    public List<OffreDeStageDTO> getOffresEnAttente() {
        return offreDeStageRepository
                .findByStatut(StatutOffre.EN_ATTENTE)
                .stream()
                .map(OffreDeStageDTO::of)
                .toList();
    }

    @Transactional
    public OffreDeStageDTO approuverOffre (Long id)
            throws OffreIntrouvableException, OffreNonEnAttenteException {
        OffreDeStage offre = offreDeStageRepository.findById(id)
                .orElseThrow(OffreIntrouvableException::new);

        if (offre.getStatut() != StatutOffre.EN_ATTENTE) {
            throw new OffreNonEnAttenteException();
        }

        offre.setStatut(StatutOffre.ACCEPTEE);
        offre.setRejectionComment(null);

        return OffreDeStageDTO.of(
                offreDeStageRepository.save(offre)
        );

    }

    @Transactional
    public OffreDeStageDTO refuserOffre(Long id, String commentaire)
            throws OffreIntrouvableException, OffreNonEnAttenteException, CommentaireRefusObligatoireException {
        OffreDeStage offre = offreDeStageRepository.findById(id)
                .orElseThrow((OffreIntrouvableException::new));

        if (offre.getStatut() != StatutOffre.EN_ATTENTE) {
            throw new OffreNonEnAttenteException();
        }

        if (commentaire == null || commentaire.isBlank()) {
            throw new CommentaireRefusObligatoireException();
        }

        offre.setStatut(StatutOffre.REFUSEE);
        offre.setRejectionComment(commentaire);

        return OffreDeStageDTO.of(
                offreDeStageRepository.save(offre)
        );
    }

}
