package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.service.SessionService;
import com.lacouf.rsbjwt.service.dto.offre.OffreDeStageDTO;
import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException;
import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException.Code;

import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import com.lacouf.rsbjwt.service.dto.session.SessionDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        return offreDeStageRepository.findByStatut(Statut.EN_ATTENTE)
                .stream().map(OffreDeStageDTO::of).toList();
    }

    public long compterOffresParStatut(Statut statut) throws ParametreDonneesInvalideException {
        if (statut == null) throw new ParametreDonneesInvalideException(Code.STATUT_OFFRE_OBLIGATOIRE);
        return offreDeStageRepository.countByStatut(statut);
    }

    public List<OffreDeStageDTO> obtenirOffresApprouvees() {
        return offreDeStageRepository.findByStatut(Statut.ACCEPTEE)
                .stream().map(OffreDeStageDTO::of).toList();
    }

    public Long compterOffreActiveSessionActuelle(){
        List<OffreDeStage> offreDeStages = offreDeStageRepository.findByDisplayEndDateAfterAndStatutAcceptee(LocalDate.now());
        return (long) filtrerOffreSessionActuelle(offreDeStages).size();
    }

    public Long compterOffreSansStagiaireSessionActuelle(){
        List<OffreDeStage> offreDeStages = offreDeStageRepository.findByCandidaturesIsEmptyAndDisplayEndDateIsAfter(LocalDate.now());
        return (long) filtrerOffreSessionActuelle(offreDeStages).size();
    }

    public Long compterOffreAvecAuMoinsUnStagiaireSessionActuelle(){
        List<OffreDeStage> offreDeStages = offreDeStageRepository.countOffresComblees();
        return (long) filtrerOffreSessionActuelle(offreDeStages).size();
    }

    public Long getOffres(){
        List<OffreDeStage> offreDeStages = offreDeStageRepository.findAll();
        return (long) filtrerOffreSessionActuelle(offreDeStages).size();
    }

    private List<OffreDeStage> filtrerOffreSessionActuelle(List<OffreDeStage> offreDeStages){
        SessionDTO sessionDTO = sessionService.getSessionActuelle();
        return sessionService.filtrerParSession(offreDeStages,sessionDTO.saison(),sessionDTO.annee(),OffreDeStage::getCreatedAt);
    }
}