package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.offre.OffreIntrouvableException;
import com.lacouf.rsbjwt.exception.utilisateur.DepartementInvalideException;
import com.lacouf.rsbjwt.exception.utilisateur.EmailExistantException;
import com.lacouf.rsbjwt.exception.utilisateur.MatriculeExistantException;
import com.lacouf.rsbjwt.exception.utilisateur.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.exception.utilisateur.NumeroTelephoneExistantException;
import com.lacouf.rsbjwt.exception.utilisateur.UtilisateurIntrouvableException;
import com.lacouf.rsbjwt.service.dto.evaluation_stagiaire.StageEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.candidature.CandidatureDTO;
import com.lacouf.rsbjwt.service.dto.candidature.ResumeStageEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.EntrevueDTO;
import com.lacouf.rsbjwt.service.dto.session.SessionDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.InscriptionProfesseurDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.ProfesseurDTO;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.utils.StockageFichierUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProfesseurService {

    private final ProfesseurRepository professeurRepository;
    private final UserAppRepository userAppRepository;
    private final PasswordEncoder passwordEncoder;
    private final EtudiantRepository etudiantRepository;
    private final CandidatureRepository candidatureRepository;
    private final OffreDeStageRepository offreDeStageRepository;
    private final SessionService sessionService;
    private final InscriptionRepository inscriptionRepository;
    private final EntrevueRepository entrevueRepository;

    public ProfesseurService(ProfesseurRepository professeurRepository, UserAppRepository userAppRepository, PasswordEncoder passwordEncoder,
                             EtudiantRepository etudiantRepository, CandidatureRepository candidatureRepository, OffreDeStageRepository offreDeStageRepository,
                             SessionService sessionService, InscriptionRepository inscriptionRepository, EntrevueRepository entrevueRepository) {
        this.professeurRepository = professeurRepository;
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
        this.etudiantRepository = etudiantRepository;
        this.candidatureRepository = candidatureRepository;
        this.offreDeStageRepository = offreDeStageRepository;
        this.sessionService = sessionService;
        this.inscriptionRepository = inscriptionRepository;
        this.entrevueRepository = entrevueRepository;
    }

    @Transactional
    public ProfesseurDTO creerCompteProfesseur(InscriptionProfesseurDTO inscriptionProfesseurDto) throws DepartementInvalideException, EmailExistantException, MatriculeExistantException, MotDePasseNonCorrespondantException, NumeroTelephoneExistantException {
        validerInscription(inscriptionProfesseurDto);

        Departement departement = StockageFichierUtils.normaliserDepartement(inscriptionProfesseurDto.department());

        Professeur professeur = Professeur.builder()
                .firstName(inscriptionProfesseurDto.firstName())
                .lastName(inscriptionProfesseurDto.lastName())
                .email(inscriptionProfesseurDto.email())
                .password(passwordEncoder.encode(inscriptionProfesseurDto.password()))
                .matricule(inscriptionProfesseurDto.matricule())
                .phoneNumber(inscriptionProfesseurDto.phone())
                .department(departement)
                .build();

        Professeur professeurCreer = professeurRepository.save(professeur);
        SessionDTO sessionDto = sessionService.getSessionActuelle();
        Inscription inscription = new Inscription(
                professeurCreer,
                sessionDto.saison(),
                sessionDto.annee()
        );
        inscriptionRepository.save(inscription);
        professeurCreer.getInscriptions().add(inscription);

        return ProfesseurDTO.of(professeurCreer);
    }

    private void validerInscription(InscriptionProfesseurDTO inscriptionProfesseurDto) throws EmailExistantException, MatriculeExistantException, MotDePasseNonCorrespondantException, NumeroTelephoneExistantException {
        if(userAppRepository.findUserAppByEmail(inscriptionProfesseurDto.email()).isPresent()){
            throw new EmailExistantException();
        }

        if(professeurRepository.findByMatricule(inscriptionProfesseurDto.matricule()).isPresent()){
            throw new MatriculeExistantException();
        }

        if(!inscriptionProfesseurDto.password().equals(inscriptionProfesseurDto.passwordConfirmation())){
            throw new MotDePasseNonCorrespondantException();
        }

        if (userAppRepository.findByPhoneNumber(
                inscriptionProfesseurDto.phone()).isPresent()) {
            throw new NumeroTelephoneExistantException();
        }
    }

    public List<EtudiantDTO> getEtudiantsAssignes()
            throws UtilisateurIntrouvableException {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Professeur professeur = userAppRepository.findUserAppByEmail(email)
                .filter(Professeur.class::isInstance)
                .map(Professeur.class::cast)
                .orElseThrow(UtilisateurIntrouvableException::new);

        List<Etudiant> etudiantsAssignes = etudiantRepository.findAllByProfesseurId(professeur.getId());

        List<Etudiant> etudiantsInscritsSession = sessionService.filtrerUtilisateursParSessionActuelle(etudiantsAssignes);

        List<Etudiant> etudiantsFiltres = sessionService.filtrerParSessionActuelle(
                etudiantsInscritsSession,
                Etudiant::getProfessorAssignedAt
        );

        return etudiantsFiltres.stream()
                .map(EtudiantDTO::of)
                .toList();
    }

    public List<CandidatureDTO> getCandidaturesEtudiant(Long etudiantId) throws UtilisateurIntrouvableException {

        etudiantRepository.findById(etudiantId)
                .orElseThrow(UtilisateurIntrouvableException::new);

        List<Candidature> candidatures = candidatureRepository.findAllByEtudiantId(etudiantId);

        return sessionService.filtrerParSessionActuelle(
                        candidatures,
                        candidature -> candidature.getOffreDeStage().getCreatedAt()
                )
                .stream()
                .map(CandidatureDTO::of)
                .toList();
    }

    public ResumeStageEtudiantDTO getResumeOffre(Long offreId) throws OffreIntrouvableException {
        OffreDeStage offreDeStage = offreDeStageRepository.findById(offreId).orElseThrow(OffreIntrouvableException::new);
        return ResumeStageEtudiantDTO.of(offreDeStage);
    }


    private List<Etudiant> obtenirEntitesEtudiantsAssignes()
            throws UtilisateurIntrouvableException {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Professeur professeur = userAppRepository.findUserAppByEmail(email)
                .filter(Professeur.class::isInstance)
                .map(Professeur.class::cast)
                .orElseThrow(UtilisateurIntrouvableException::new);

        List<Etudiant> etudiants = etudiantRepository
                .findAllByProfesseurId(professeur.getId());

        List<Etudiant> inscritsSession =
                sessionService.filtrerUtilisateursParSessionActuelle(etudiants);

        return sessionService.filtrerParSessionActuelle(
                inscritsSession,
                Etudiant::getProfessorAssignedAt
        );
    }

    public List<CandidatureDTO> getCandidaturesDeMesEtudiants()
            throws UtilisateurIntrouvableException {

        List<Long> ids = obtenirEntitesEtudiantsAssignes().stream()
                .map(Etudiant::getId)
                .toList();

        if (ids.isEmpty()) {
            return List.of();
        }

        return sessionService.filtrerParSessionActuelle(
                        candidatureRepository.findAllByEtudiantIdIn(ids),
                        candidature -> candidature.getOffreDeStage().getCreatedAt()
                ).stream()
                .map(CandidatureDTO::of)
                .toList();
    }

    public List<EntrevueDTO> getEntrevuesDeMesEtudiants()
            throws UtilisateurIntrouvableException {

        List<Long> ids = obtenirEntitesEtudiantsAssignes().stream()
                .map(Etudiant::getId)
                .toList();

        if (ids.isEmpty()) {
            return List.of();
        }

        return sessionService.filtrerParSessionActuelle(
                        entrevueRepository.findByCandidatureEtudiantIdInOrderByDateHeureEntrevueAsc(ids),
                        entrevue -> entrevue.getCandidature()
                                .getOffreDeStage().getCreatedAt()
                ).stream()
                .map(EntrevueDTO::of)
                .toList();
    }

    public List<StageEtudiantDTO> getStagesDeMesEtudiants()
            throws UtilisateurIntrouvableException {

        return obtenirEntitesEtudiantsAssignes().stream()
                .filter(etudiant -> etudiant.getCandidatureStage() != null)
                .map(etudiant -> new StageEtudiantDTO(
                        EtudiantDTO.of(etudiant),
                        ResumeStageEtudiantDTO.of(
                                etudiant.getCandidatureStage().getOffreDeStage()
                        )
                ))
                .toList();
    }

}