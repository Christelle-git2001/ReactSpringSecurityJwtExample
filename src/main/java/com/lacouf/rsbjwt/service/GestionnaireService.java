package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Gestionnaire;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.repository.CvEtudiantRepository;
import com.lacouf.rsbjwt.repository.GestionnaireRepository;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class GestionnaireService {
    private final PasswordEncoder passwordEncoder;
    private final OffreDeStageRepository offreDeStageRepository;
    private final UserAppRepository userAppRepository;
    private final GestionnaireRepository gestionnaireRepository;
    private final CvEtudiantRepository cvEtudiantRepository;

    public GestionnaireService(PasswordEncoder passwordEncoder, OffreDeStageRepository offreDeStageRepository, UserAppRepository userAppRepository, GestionnaireRepository gestionnaireRepository, CvEtudiantRepository cvEtudiantRepository){
        this.passwordEncoder = passwordEncoder;
        this.offreDeStageRepository = offreDeStageRepository;

        this.userAppRepository = userAppRepository;
        this.gestionnaireRepository = gestionnaireRepository;
        this.cvEtudiantRepository = cvEtudiantRepository;
    }

    @Transactional
    public GestionnaireDto creerCompteGestionnaire(GestionnaireDto gestionnaireDto) throws EmailExistantException, NumeroTelephoneExistantException {
        validerInscriptionGestionnaire(gestionnaireDto);
        Gestionnaire gestionnaire = Gestionnaire.builder()
                .firstName(gestionnaireDto.firstName())
                .lastName(gestionnaireDto.lastName())
                .email(gestionnaireDto.email())
                .phoneNumber(gestionnaireDto.phoneNumber())
                .password(passwordEncoder.encode(gestionnaireDto.password()))
                .build();
        return GestionnaireDto.create(gestionnaireRepository.save(gestionnaire));
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

    public List<OffreDeStageDTO> getOffresEnAttente() {
        return offreDeStageRepository
                .findByStatut(Statut.EN_ATTENTE)
                .stream()
                .map(OffreDeStageDTO::of)
                .toList();
    }

    @Transactional
    public OffreDeStageDTO approuverOffre (Long id)
            throws OffreIntrouvableException, OffreNonEnAttenteException {
        OffreDeStage offre = offreDeStageRepository.findById(id)
                .orElseThrow(OffreIntrouvableException::new);

        if (offre.getStatut() != Statut.EN_ATTENTE) {
            throw new OffreNonEnAttenteException();
        }

        offre.setStatut(Statut.ACCEPTEE);
        offre.setRejectionComment(null);

        return OffreDeStageDTO.of(
                offreDeStageRepository.save(offre)
        );

    }

    @Transactional
    public OffreDeStageDTO refuserOffre(Long id, String commentaire)
            throws OffreIntrouvableException, OffreNonEnAttenteException, CommentaireRefusObligatoireException {
        OffreDeStage offre = offreDeStageRepository.findById(id)
                .orElseThrow((OffreIntrouvableException::new));

        if (offre.getStatut() != Statut.EN_ATTENTE) {
            throw new OffreNonEnAttenteException();
        }

        if (commentaire == null || commentaire.isBlank()) {
            throw new CommentaireRefusObligatoireException();
        }

        offre.setStatut(Statut.REFUSEE);
        offre.setRejectionComment(commentaire);

        return OffreDeStageDTO.of(
                offreDeStageRepository.save(offre)
        );
    }

    public List<CvEtudiantDTO> getCurriculumVitaeEnAttente(){
        return cvEtudiantRepository.findByStatut(Statut.EN_ATTENTE)
                .stream()
                .map(CvEtudiantDTO::of)
                .toList();
    }

    @Transactional
    public CvEtudiantDTO approuverCurriculumVitae(Long id) throws CurriculumVitaeIntrouvable, CurriculumVitaeNonEnAttente {
        CvEtudiant cvEtudiant = cvEtudiantRepository.findById(id).orElseThrow(CurriculumVitaeIntrouvable::new);

        if (cvEtudiant.getStatut() != Statut.EN_ATTENTE)
            throw new CurriculumVitaeNonEnAttente();

        cvEtudiant.setStatut(Statut.ACCEPTEE);
        cvEtudiant.setRejectionComment(null);

        return CvEtudiantDTO.of(
                cvEtudiantRepository.save(cvEtudiant)
        );
    }

    @Transactional
    public CvEtudiantDTO refuserCurriculumVitae(Long id, String commentaire) throws CurriculumVitaeIntrouvable, CurriculumVitaeNonEnAttente, CommentaireRefusObligatoireException {
        CvEtudiant cvEtudiant = cvEtudiantRepository.findById(id).orElseThrow(CurriculumVitaeIntrouvable::new);

        if (cvEtudiant.getStatut() != Statut.EN_ATTENTE)
            throw new CurriculumVitaeNonEnAttente();

        if (commentaire == null || commentaire.isBlank()) {
            throw new CommentaireRefusObligatoireException();
        }

        cvEtudiant.setStatut(Statut.REFUSEE);
        cvEtudiant.setRejectionComment(commentaire);

        return CvEtudiantDTO.of(
                cvEtudiantRepository.save(cvEtudiant)
        );
    }

    private void validerInscriptionGestionnaire(GestionnaireDto gestionnaireDto)  throws EmailExistantException, NumeroTelephoneExistantException{
        if (userAppRepository.findUserAppByEmail(
                gestionnaireDto.email()).isPresent()) {
            throw new EmailExistantException();
        }

        if (userAppRepository.findByPhoneNumber(
                gestionnaireDto.phoneNumber()).isPresent()) {
            throw new NumeroTelephoneExistantException();
        }
    }

    public List<CvEtudiantDTO> getTousLesCvs() {
        return cvEtudiantRepository.findAll()
                .stream()
                .map(CvEtudiantDTO::of)
                .toList();
    }
}
