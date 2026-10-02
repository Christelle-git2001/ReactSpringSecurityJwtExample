package com.lacouf.rsbjwt.service;

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

    public List<OffreDeStageDTO> getOffresEnAttente(){
        return offreDeStageRepository
                .findByStatut(StatutOffre.EN_ATTENTE)
                .stream()
                .map(OffreDeStageDTO::of)
                .toList();
    }
//TODO : exception personnalisée
    @Transactional
    public OffreDeStageDTO approuverOffre(Long id){
        OffreDeStage offre = offreDeStageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        if (offre.getStatut() != StatutOffre.EN_ATTENTE) {
            throw new RuntimeException(
                    "Seule une offre en attente peut être approuvée"
            );
        }

        offre.setStatut(StatutOffre.ACCEPTEE);
        offre.setRejectionComment(null);

        return OffreDeStageDTO.of(
                offreDeStageRepository.save(offre)
        );

    }

    @Transactional
    public OffreDeStageDTO refuserOffre(Long id, String commentaire){
        OffreDeStage offre = offreDeStageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        if (offre.getStatut() != StatutOffre.EN_ATTENTE) {
            throw new RuntimeException(
                    "Seule une offre en attente peut être approuvée"
            );
        }

        if (commentaire == null || commentaire.isBlank()) {
            throw new IllegalArgumentException(
                    "Le commentaire de refus est obligatoire"
            );
        }

        offre.setStatut(StatutOffre.REFUSEE);
        offre.setRejectionComment(commentaire);

        return OffreDeStageDTO.of(
                offreDeStageRepository.save(offre)
        );
    }

}
