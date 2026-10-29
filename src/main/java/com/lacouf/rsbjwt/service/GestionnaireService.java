package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Notification.ControllerPath;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.event.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.dto.*;
import com.lacouf.rsbjwt.utils.StockageFichierUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
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
    private final ApplicationEventPublisher applicationEventPublisher;
    private final EmployeurRepository employeurRepository;
    private final EtudiantRepository etudiantRepository;
    private final Path STORAGE_OFFRE = Paths.get("uploads", "offres");
    private final ProfesseurRepository professeurRepository;

    public GestionnaireService(PasswordEncoder passwordEncoder, OffreDeStageRepository offreDeStageRepository, UserAppRepository userAppRepository, GestionnaireRepository gestionnaireRepository, CvEtudiantRepository cvEtudiantRepository,
                               ApplicationEventPublisher applicationEventPublisher, EmployeurRepository employeurRepository, EtudiantRepository etudiantRepository,
                               ProfesseurRepository professeurRepository){
        this.passwordEncoder = passwordEncoder;
        this.offreDeStageRepository = offreDeStageRepository;

        this.userAppRepository = userAppRepository;
        this.gestionnaireRepository = gestionnaireRepository;
        this.cvEtudiantRepository = cvEtudiantRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.employeurRepository = employeurRepository;
        this.etudiantRepository = etudiantRepository;
        this.professeurRepository = professeurRepository;
    }

    @Transactional
    public GestionnaireDTO creerCompteGestionnaire(GestionnaireDTO gestionnaireDto) throws EmailExistantException, NumeroTelephoneExistantException {
        validerInscriptionGestionnaire(gestionnaireDto);
        Gestionnaire gestionnaire = Gestionnaire.builder()
                .firstName(gestionnaireDto.firstName())
                .lastName(gestionnaireDto.lastName())
                .email(gestionnaireDto.email())
                .phoneNumber(gestionnaireDto.phoneNumber())
                .password(passwordEncoder.encode(gestionnaireDto.password()))
                .build();
        return GestionnaireDTO.create(gestionnaireRepository.save(gestionnaire));
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

    public List<OffreDeStageDTO> getToutesLesOffres() {
        return offreDeStageRepository
                .findAll()
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

        OffreDeStage offreEnregistree = offreDeStageRepository.save(offre);

        applicationEventPublisher.publishEvent(
                new OffreDeStageApprouveeEvent(offreEnregistree)
        );

        applicationEventPublisher.publishEvent(
                new OffreSupprimerSoNotificationGestionnaireEvent(
                        ControllerPath.GESTIONNAIRE_OFFRE_ID_DETAIL.build(String.valueOf(offre.getId()))
                )
        );

        return OffreDeStageDTO.of(offreEnregistree);
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

        OffreDeStage offreEnregistree = offreDeStageRepository.save(offre);

        applicationEventPublisher.publishEvent(
                new OffreSupprimerSoNotificationGestionnaireEvent(
                        ControllerPath.GESTIONNAIRE_OFFRE_ID_DETAIL.build(String.valueOf(offre.getId()))
                )
        );

        return OffreDeStageDTO.of(offreEnregistree);
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
        CvEtudiantDTO cvEtudiantDTO = CvEtudiantDTO.of(cvEtudiantRepository.save(cvEtudiant));
        applicationEventPublisher.publishEvent(new CvStatutModifieEvent(cvEtudiant.getEtudiant(), cvEtudiant.getStatut()));
        applicationEventPublisher.publishEvent(new CvSupprimerSoNotificationGestionnaireEvent(
                ControllerPath.GESTIONNAIRE_CV_ID_DETAIL.build(String.valueOf(cvEtudiant.getId()))));
        return cvEtudiantDTO;
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
        CvEtudiantDTO cvEtudiantDTO = CvEtudiantDTO.of(cvEtudiantRepository.save(cvEtudiant));
        applicationEventPublisher.publishEvent(new CvStatutModifieEvent(cvEtudiant.getEtudiant(),cvEtudiant.getStatut()));
        applicationEventPublisher.publishEvent(new CvSupprimerSoNotificationGestionnaireEvent(
                ControllerPath.GESTIONNAIRE_CV_ID_DETAIL.build(String.valueOf(cvEtudiant.getId()))));
        return cvEtudiantDTO;
    }

    private void validerInscriptionGestionnaire(GestionnaireDTO gestionnaireDto)  throws EmailExistantException, NumeroTelephoneExistantException{
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

    public CvEtudiantDTO getCvParId(Long idCv) throws CurriculumVitaeIntrouvable {
        return CvEtudiantDTO.of(cvEtudiantRepository.findById(idCv).orElseThrow(CurriculumVitaeIntrouvable::new));
    }

    public Resource telechargerCvParId(Long id) throws CurriculumVitaeIntrouvable, FichierIntrouvableException {
        CvEtudiant cv = cvEtudiantRepository.findById(id)
                .orElseThrow(CurriculumVitaeIntrouvable::new);

        return StockageFichierUtils.chargerFichier(Path.of(cv.getStoragePath()));
    }

    public OffreDeStageDTO getOffreParId(Long id)
            throws OffreIntrouvableException {
        OffreDeStage offre = offreDeStageRepository.findById(id)
                .orElseThrow(OffreIntrouvableException::new);

        return OffreDeStageDTO.of(offre);
    }

    @Transactional
    public OffreDeStageDTO creerOffreCiblee(
            CreationOffreCibleeDTO creationOffreCibleeDTO,
            MultipartFile file
    ) throws FichierCorrompuException, FichierTypeInvalideException, IOException, FichierTropVolumineuxException, DateFinAffichageAvantAujourdhuiException, DateFinAffichageApresDateDebutException, DateFinAvantDateDebutException, DateDebutAvantAujourdhuiException, UtilisateurIntrouvableException {

        Employeur employeur = employeurRepository.findById(creationOffreCibleeDTO.employeurId())
                .orElseThrow(() -> new UtilisateurIntrouvableException());

        Etudiant etudiant = etudiantRepository.findById(creationOffreCibleeDTO.etudiantId())
                .orElseThrow(() -> new UtilisateurIntrouvableException());

        validerDates(creationOffreCibleeDTO);

        if (file == null || file.isEmpty()) {
            throw new FichierCorrompuException();
        }

        Path path = StockageFichierUtils.sauvegarderPdf(file, STORAGE_OFFRE);
        String storagePath = path.toString();
        String fileName = file.getOriginalFilename();

        OffreCiblee offre = OffreCiblee.builder()
                .title(creationOffreCibleeDTO.title())
                .description(creationOffreCibleeDTO.description())
                .salary(creationOffreCibleeDTO.salary())
                .domain(creationOffreCibleeDTO.domain())
                .startDate(creationOffreCibleeDTO.startDate())
                .endDate(creationOffreCibleeDTO.endDate())
                .displayEndDate(creationOffreCibleeDTO.displayEndDate())
                .statut(Statut.EN_ATTENTE)
                .fileName(fileName)
                .storagePath(storagePath)
                .employeur(employeur)
                .etudiant(etudiant)
                .build();

        OffreCiblee saved = offreDeStageRepository.save(offre);

        applicationEventPublisher.publishEvent(new NouvelleOffreTeleverseeEvent(saved));

        return OffreDeStageDTO.of(saved);
    }

    private void validerDates(CreationOffreCibleeDTO creationOffreCibleeDTO)
            throws DateFinAvantDateDebutException, DateFinAffichageAvantAujourdhuiException,
            DateFinAffichageApresDateDebutException, DateDebutAvantAujourdhuiException {

        if (creationOffreCibleeDTO.startDate().isBefore(LocalDate.now())) {
            throw new DateDebutAvantAujourdhuiException();
        }

        if (!creationOffreCibleeDTO.endDate().isAfter(creationOffreCibleeDTO.startDate())) {
            throw new DateFinAvantDateDebutException();
        }

        if (creationOffreCibleeDTO.displayEndDate().isBefore(LocalDate.now())) {
            throw new DateFinAffichageAvantAujourdhuiException();
        }

        if (creationOffreCibleeDTO.displayEndDate().isAfter(creationOffreCibleeDTO.startDate())) {
            throw new DateFinAffichageApresDateDebutException();
        }
    }

    public List<EtudiantDTO> getAllEtudiants() {
        return etudiantRepository.findAll()
                .stream()
                .map(EtudiantDTO::of)
                .toList();
    }

    public List<EmployeurDTO> getAllEmployeurs() {
        return employeurRepository.findAll()
                .stream()
                .map(EmployeurDTO::of)
                .toList();
    }


    public List<ProfesseurDTO> getAllProfesseurs() {
        return professeurRepository.findAll()
                .stream()
                .map(ProfesseurDTO::of)
                .toList();
    }


    @Transactional
    public EtudiantDTO assignerProfesseur(AssignationProfesseurDTO dto) throws UtilisateurIntrouvableException{
        Etudiant etudiant = etudiantRepository.findById(dto.etudiantId())
                .orElseThrow(UtilisateurIntrouvableException::new);

        Professeur professeur = professeurRepository.findById(dto.professeurId())
                .orElseThrow(UtilisateurIntrouvableException::new);

        etudiant.setProfesseur(professeur);

        return EtudiantDTO.of(etudiantRepository.save(etudiant));
    }




}