package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.repository.CandidatureRepository;
import com.lacouf.rsbjwt.service.SessionService;
import com.lacouf.rsbjwt.service.dto.cv.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.session.SessionDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EtudiantDTO;
import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException;
import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException.Code;

import com.lacouf.rsbjwt.model.Enum.StatutEtudiant;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.repository.CvEtudiantRepository;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DonneesEtudiantService {
    private final EtudiantRepository etudiantRepository;
    private final CvEtudiantRepository cvEtudiantRepository;
    private final SessionService sessionService;
    private final CandidatureRepository candidatureRepository;

    public DonneesEtudiantService(EtudiantRepository etudiantRepository, CvEtudiantRepository cvEtudiantRepository, SessionService sessionService, CandidatureRepository candidatureRepository) {
        this.etudiantRepository = etudiantRepository;
        this.cvEtudiantRepository = cvEtudiantRepository;
        this.sessionService = sessionService;
        this.candidatureRepository = candidatureRepository;
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

    public List<EtudiantDTO> obtenirEtudiantsInscrits() {
        return etudiantRepository.findAll().stream().map(EtudiantDTO::of).toList();
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

    public long compterCvParStatut(Statut statut) throws ParametreDonneesInvalideException {
        if (statut == null) throw new ParametreDonneesInvalideException(Code.STATUT_CV_OBLIGATOIRE);
        return cvEtudiantRepository.countByStatut(statut);
    }

    public List<EtudiantDTO> obtenirEtudiantsAvecCvRefuse() {
        return etudiantRepository.findByCvStatut(Statut.REFUSEE).stream().map(EtudiantDTO::of).toList();
    }

    public long compterEtudiantsParDepartement(Departement departement) throws ParametreDonneesInvalideException {
        if (departement == null) throw new ParametreDonneesInvalideException(Code.DEPARTEMENT_OBLIGATOIRE);
        return etudiantRepository.countByDepartment(departement);
    }

    public List<EtudiantDTO> obtenirEtudiantsSansEntrevue() {
        return etudiantRepository.findSansEntrevue().stream().map(EtudiantDTO::of).toList();
    }

    public List<EtudiantDTO> obtenirEtudiantsParProfesseur(Long professeurId) throws ParametreDonneesInvalideException {
        if (professeurId == null || professeurId <= 0) {
            throw new ParametreDonneesInvalideException(Code.PROFESSEUR_INVALIDE);
        }
        return etudiantRepository.findAllByProfesseurId(professeurId).stream().map(EtudiantDTO::of).toList();
    }

    public Long countEtudiantsInscritsSessionActuelle() {
        List<Etudiant> etudiants = etudiantRepository.findAll();
        return (long) filtrerUtilisateurParSessionActuelle(etudiants).size();
    }

    public Long countEtudiantsCvEnAttenteSessionActuelle() {
        List<CvEtudiant> cvEtudiants = cvEtudiantRepository.findByStatut(Statut.EN_ATTENTE);

        SessionDTO sessionDTO = sessionService.getSessionActuelle();
        List<CvEtudiant> cvEtudiantsFiltre = sessionService.filtrerParSession(cvEtudiants,sessionDTO.saison(),sessionDTO.annee(), CvEtudiant::getUploadDate);
        return (long) cvEtudiantsFiltre.size();
    }

    public Long countEtudiantSansStageSessionActuelle() {
        Set<Long> idsEtudiantsSession = getIdsEtudiantsSessionActuelle();
        Set<Long> idsAvecStage = getIdsEtudiantsAvecStageSessionActuelle(idsEtudiantsSession);
        return idsEtudiantsSession.stream()
                .filter(id -> !idsAvecStage.contains(id))
                .count();
    }

    public Long countEtudiantAvecStageSessionActuelle() {
        Set<Long> idsEtudiantsSession = getIdsEtudiantsSessionActuelle();
        Set<Long> idsAvecStage = getIdsEtudiantsAvecStageSessionActuelle(idsEtudiantsSession);
        return (long) idsAvecStage.size();
    }

    private List<Etudiant> filtrerUtilisateurParSessionActuelle(List<Etudiant> etudiants){
        SessionDTO sessionDTO = sessionService.getSessionActuelle();
        return sessionService.filtrerUtilisateursParSession(etudiants,sessionDTO.saison(),sessionDTO.annee());
    }

    private List<Candidature> filtrerCandidaturesParSessionActuelle(List<Candidature> candidatures){
        SessionDTO sessionDTO = sessionService.getSessionActuelle();
        return sessionService.filtrerParSession(candidatures,sessionDTO.saison(),sessionDTO.annee(),Candidature::getDateCandidature);
    }

    private Set<Long> getIdsEtudiantsSessionActuelle() {
        return filtrerUtilisateurParSessionActuelle(etudiantRepository.findAll()).stream()
                .map(Etudiant::getId)
                .collect(Collectors.toSet());
    }

    private Set<Long> getIdsEtudiantsAvecStageSessionActuelle(Set<Long> idsEtudiantsSession) {
        return filtrerCandidaturesParSessionActuelle(candidatureRepository.findAll()).stream()
                .filter(c -> c.getEtudiant() != null && idsEtudiantsSession.contains(c.getEtudiant().getId()))
                .filter(c -> c.getStatut() == StatutCandidature.STAGE_TROUVE
                        || c.getStatut() == StatutCandidature.CONTRAT_CREE)
                .map(c -> c.getEtudiant().getId())
                .collect(Collectors.toSet());
    }
}