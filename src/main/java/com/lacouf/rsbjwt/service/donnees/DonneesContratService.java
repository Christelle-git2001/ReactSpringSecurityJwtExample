package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Contrat;
import com.lacouf.rsbjwt.service.SessionService;
import com.lacouf.rsbjwt.service.dto.contrat.ResumeContratDTO;
import com.lacouf.rsbjwt.repository.ContratRepository;
import com.lacouf.rsbjwt.service.dto.session.SessionDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DonneesContratService {
    private final ContratRepository contratRepository;
    private final SessionService sessionService;

    public DonneesContratService(ContratRepository contratRepository, SessionService sessionService) {
        this.contratRepository = contratRepository;
        this.sessionService = sessionService;
    }

    public List<ResumeContratDTO> obtenirContratsNonSignes() {
        return contratRepository.findNonEntierementSignes().stream().map(ResumeContratDTO::of).toList();
    }

    public List<ResumeContratDTO> obtenirContratsASignerGestionnaire() {
        return contratRepository
                .findByDateSignatureGestionnaireIsNullAndDateSignatureEtudiantIsNotNullAndDateSignatureEmployeurIsNotNull()
                .stream().map(ResumeContratDTO::of).toList();
    }

    public Long countContratsASignerGestionnaireSessionActuelle() {
        List<Contrat> contrats = contratRepository.findByDateSignatureGestionnaireIsNullAndDateSignatureEtudiantIsNotNullAndDateSignatureEmployeurIsNotNull();
        return (long) filtrerListeParSessionActuelle(contrats).size();
    }

    public Long countContratSignerGestionnaireSessionActuelle() {
        List<Contrat> contrats = contratRepository.findByDateSignatureGestionnaireIsNotNullAndDateSignatureEtudiantIsNotNullAndDateSignatureEmployeurIsNotNull();
        return (long) filtrerListeParSessionActuelle(contrats).size();
    }

    public Long countAllSessionActuelle() {
        List<Contrat> contrats = contratRepository.findAll();
        return (long) filtrerListeParSessionActuelle(contrats).size();
    }

    public Long contratBloque() {
        LocalDateTime limiteDixJours = LocalDateTime.now().minusDays(10);
        List<Contrat> contrats = contratRepository.findContratsBloquesOuEnRetard(limiteDixJours);
        return (long) filtrerListeParSessionActuelle(contrats).size();
    }

    private List<Contrat> filtrerListeParSessionActuelle(List<Contrat> contrats){
        SessionDTO sessionDTO = sessionService.getSessionActuelle();
        return sessionService.filtrerParSession(contrats,sessionDTO.saison(),sessionDTO.annee(), Contrat::getCreatedAt);
    }
}