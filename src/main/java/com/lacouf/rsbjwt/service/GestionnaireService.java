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

    public List<OffreDeStageDTO> getOffresEnAttente(){}

    public OffreDeStageDTO approuverOffre(Long id){}

    public OffreDeStageDTO refuserOffre(Long id, String commentaire){}
}
