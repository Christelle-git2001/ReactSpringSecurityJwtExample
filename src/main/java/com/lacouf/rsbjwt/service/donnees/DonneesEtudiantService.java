package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Enum.StatutEtudiant;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DonneesEtudiantService {
    private final EtudiantRepository etudiantRepository;

    public DonneesEtudiantService(EtudiantRepository etudiantRepository) {
        this.etudiantRepository = etudiantRepository;
    }

    public long compterEtudiantsSansCv() {
        return etudiantRepository.countByCvIsNull();
    }

    public long compterEtudiantsSansStage() {
        return etudiantRepository.countByCandidatureStageIsNullAndStatutNot(StatutEtudiant.STAGE_TROUVE);
    }
}