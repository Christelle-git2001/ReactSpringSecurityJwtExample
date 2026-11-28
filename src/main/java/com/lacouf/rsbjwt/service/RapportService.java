package com.lacouf.rsbjwt.service;
import com.lacouf.rsbjwt.service.donnees.DonneesCandidatureService;
import com.lacouf.rsbjwt.service.donnees.DonneesEntrevueService;
import com.lacouf.rsbjwt.service.donnees.DonneesEtudiantService;
import com.lacouf.rsbjwt.service.donnees.DonneesOffreService;
import com.lacouf.rsbjwt.service.dto.candidature.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.cv.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.EntrevueDTO;
import com.lacouf.rsbjwt.service.dto.offre.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EtudiantDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RapportService {

    private final DonneesOffreService donneesOffreService;
    private final DonneesEtudiantService donneesEtudiantService;
    private final DonneesCandidatureService donneesCandidatureService;
    private final DonneesEntrevueService donneesEntrevueService;

    public RapportService(DonneesOffreService donneesOffreService, DonneesEtudiantService donneesEtudiantService,
                          DonneesCandidatureService donneesCandidatureService,
                          DonneesEntrevueService donneesEntrevueService) {
        this.donneesOffreService = donneesOffreService;
        this.donneesEtudiantService = donneesEtudiantService;
        this.donneesCandidatureService = donneesCandidatureService;
        this.donneesEntrevueService = donneesEntrevueService;
    }

    public List<OffreDeStageDTO> obtenirOffresEnAttente() {
        return donneesOffreService.obtenirOffresEnAttente();
    }

    public List<OffreDeStageDTO> obtenirOffresApprouvees() {
        return donneesOffreService.obtenirOffresApprouvees();
    }

    public List<EtudiantDTO> obtenirEtudiantsInscrits() {
        return donneesEtudiantService.obtenirEtudiantsInscrits();
    }

    public List<EtudiantDTO> obtenirEtudiantsSansCv() {
        return donneesEtudiantService.obtenirEtudiantsSansCv();
    }

    public List<CvEtudiantDTO> obtenirCvEnAttente() {
        return donneesEtudiantService.obtenirCvEnAttente();
    }

    public List<EtudiantDTO> obtenirEtudiantsSansEntrevue() {
        return donneesEtudiantService.obtenirEtudiantsSansEntrevue();
    }

    public List<CandidatureDTO> obtenirCandidaturesEnAttenteEntrevue() {
        return donneesCandidatureService.obtenirCandidaturesEnAttenteEntrevue();
    }

    public List<EntrevueDTO> obtenirEntrevuesEnAttenteReponse() {
        return donneesEntrevueService.obtenirEntrevuesEnAttenteReponse();
    }

    public List<EtudiantDTO> obtenirEtudiantsAyantTrouveStage() {
        return donneesEtudiantService.obtenirEtudiantsAyantTrouveStage();
    }

    public List<CandidatureDTO> obtenirCandidaturesSansEvaluation() {
        return donneesCandidatureService.obtenirCandidaturesSansEvaluation();
    }

    public List<CandidatureDTO> obtenirCandidaturesSansEvaluationMilieuStage() {
        return donneesCandidatureService.obtenirCandidaturesSansEvaluationMilieuStage();
    }

}