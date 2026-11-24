package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.service.dto.contrat.ResumeContratDTO;
import com.lacouf.rsbjwt.repository.ContratRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    public Long countContratsASignerGestionnaire() {
        return contratRepository
                .countByDateSignatureGestionnaireIsNullAndDateSignatureEtudiantIsNotNullAndDateSignatureEmployeurIsNotNull();
    }

    public Long countContratSignerGestionnaire() {
        return contratRepository
                .countByDateSignatureGestionnaireIsNotNullAndDateSignatureEtudiantIsNotNullAndDateSignatureEmployeurIsNotNull();
    }

    public Long countAll() {
        return contratRepository.countAll();
    }

    public Long contratBloque() {
        LocalDateTime limiteDixJours = LocalDateTime.now().minusDays(10);
        return contratRepository.countContratsBloquesOuEnRetard(limiteDixJours);
    }
}