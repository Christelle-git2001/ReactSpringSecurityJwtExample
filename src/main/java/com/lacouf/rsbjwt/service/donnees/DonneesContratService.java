package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.repository.ContratRepository;
import com.lacouf.rsbjwt.service.dto.ResumeContratDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DonneesContratService {
    private final ContratRepository contratRepository;

    public DonneesContratService(ContratRepository contratRepository) {
        this.contratRepository = contratRepository;
    }

    public List<ResumeContratDTO> obtenirContratsNonSignes() {
        return contratRepository.findNonEntierementSignes().stream().map(ResumeContratDTO::of).toList();
    }

    public List<ResumeContratDTO> obtenirContratsASignerGestionnaire() {
        return contratRepository
                .findByDateSignatureGestionnaireIsNullAndDateSignatureEtudiantIsNotNullAndDateSignatureEmployeurIsNotNull()
                .stream().map(ResumeContratDTO::of).toList();
    }
}