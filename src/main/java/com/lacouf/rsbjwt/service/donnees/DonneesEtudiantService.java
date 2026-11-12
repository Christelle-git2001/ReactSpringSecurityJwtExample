package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Enum.StatutEtudiant;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.repository.CvEtudiantRepository;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.service.dto.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DonneesEtudiantService {
    private final EtudiantRepository etudiantRepository;
    private final CvEtudiantRepository cvEtudiantRepository;

    public DonneesEtudiantService(EtudiantRepository etudiantRepository, CvEtudiantRepository cvEtudiantRepository) {
        this.etudiantRepository = etudiantRepository;
        this.cvEtudiantRepository = cvEtudiantRepository;
    }

    public long compterEtudiantsSansCv() {
        return etudiantRepository.countByCvIsNull();
    }

    public long compterEtudiantsSansStage() {
        return etudiantRepository.countByCandidatureStageIsNullAndStatutNot(StatutEtudiant.STAGE_TROUVE);
    }

    public long compterEtudiantsInscrits() {
        return etudiantRepository.count();
    }

    public List<EtudiantDTO> obtenirEtudiantsSansCv() {
        return etudiantRepository.findByCvIsNull().stream().map(EtudiantDTO::of).toList();
    }

    public List<EtudiantDTO> obtenirEtudiantsSansStage() {
        return etudiantRepository.findByCandidatureStageIsNullAndStatutNot(StatutEtudiant.STAGE_TROUVE)
                .stream().map(EtudiantDTO::of).toList();
    }

    public long compterEtudiantsAvecStage() {
        return etudiantRepository.countByCandidatureStageIsNotNullOrStatut(StatutEtudiant.STAGE_TROUVE);
    }

    public List<EtudiantDTO> obtenirEtudiantsAvecStage() {
        return etudiantRepository.findByCandidatureStageIsNotNullOrStatut(StatutEtudiant.STAGE_TROUVE)
                .stream().map(EtudiantDTO::of).toList();
    }

    public List<EtudiantDTO> obtenirEtudiantsSansProfesseur() {
        return etudiantRepository.findByProfesseurIsNull().stream().map(EtudiantDTO::of).toList();
    }

    public List<CvEtudiantDTO> obtenirCvEnAttente() {
        return cvEtudiantRepository.findByStatut(Statut.EN_ATTENTE).stream().map(CvEtudiantDTO::of).toList();
    }
}