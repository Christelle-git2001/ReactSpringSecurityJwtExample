package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.model.event.NouvelleOffreTeleverseeEvent;
import com.lacouf.rsbjwt.model.event.OffreModifierSoNotificationEvent;
import com.lacouf.rsbjwt.repository.CandidatureRepository;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.*;
import com.lacouf.rsbjwt.utils.StockageFichierUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
public class EmployeurService {
    private final EmployeurRepository employeurRepository;
    private final UserAppRepository userAppRepository;
    private final PasswordEncoder passwordEncoder;
    private final OffreDeStageRepository offreDeStageRepository;
    private final CandidatureRepository candidatureRepository ;
    private final Path STORAGE_OFFRE = Paths.get("uploads", "offres");
    private final ApplicationEventPublisher eventPublisher;

    public EmployeurService(
            EmployeurRepository employeurRepository,
            UserAppRepository userAppRepository,
            PasswordEncoder passwordEncoder,
            OffreDeStageRepository offreDeStageRepository, CandidatureRepository candidatureRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.employeurRepository = employeurRepository;
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
        this.offreDeStageRepository = offreDeStageRepository;
        this.candidatureRepository = candidatureRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public EmployeurDTO creeCompteEmployeur(InscriptionEmployeurDTO inscriptionEmployeurDTO) throws EmailExistantException, MotDePasseNonCorrespondantException, NumeroTelephoneExistantException {
       validerInscriptionEmployeur(inscriptionEmployeurDTO);
        String encodedPassword = passwordEncoder.encode(inscriptionEmployeurDTO.password());
        Employeur employeur = inscriptionEmployeurDTO.toEntity(encodedPassword);
        Employeur employeurCreer =  employeurRepository.save(employeur);
       return EmployeurDTO.of(employeurCreer);
    }


    private void validerInscriptionEmployeur(InscriptionEmployeurDTO inscriptionEmployeurDTO) throws EmailExistantException, MotDePasseNonCorrespondantException, NumeroTelephoneExistantException {
        if (!inscriptionEmployeurDTO.password().equals(inscriptionEmployeurDTO.passwordConfirmation()))
            throw new MotDePasseNonCorrespondantException();
        if (employeurExiste(inscriptionEmployeurDTO.email()))
            throw new EmailExistantException();
        if (userAppRepository.findByPhoneNumber(inscriptionEmployeurDTO.phone()).isPresent())
            throw new NumeroTelephoneExistantException();
    }

    private boolean employeurExiste(String email) {
        return userAppRepository.findUserAppByEmail(email.toLowerCase()).isPresent();
    }

    @Transactional
    public OffreDeStageDTO creerOffre(CreationOffreDeStageDTO creationOffreDeStageDTO,
                                      MultipartFile file,
                                      String email)
            throws DateFinAvantDateDebutException, IOException, FichierTypeInvalideException,
            FichierCorrompuException, FichierTropVolumineuxException, DateFinAffichageAvantAujourdhuiException,
            DateFinAffichageApresDateDebutException, DateDebutAvantAujourdhuiException {
        Employeur employeur = getEmployeurByEmail(email);

        validerDates(creationOffreDeStageDTO);

        String fileName = null;
        String storagePath = null ;

        if (file == null || file.isEmpty()) {
            throw new FichierCorrompuException();
        }


        Path path = StockageFichierUtils.sauvegarderPdf(file, STORAGE_OFFRE);
        storagePath = path.toString();
        fileName = file.getOriginalFilename();


        OffreDeStage offre = OffreDeStage.builder()
                .title(creationOffreDeStageDTO.title())
                .description(creationOffreDeStageDTO.description())
                .salary(creationOffreDeStageDTO.salary())
                .domain(creationOffreDeStageDTO.domain())
                .startDate(creationOffreDeStageDTO.startDate())
                .endDate(creationOffreDeStageDTO.endDate())
                .displayEndDate(creationOffreDeStageDTO.displayEndDate())
                .statut(Statut.EN_ATTENTE)
                .fileName(fileName)
                .storagePath(storagePath)
                .employeur(employeur)
                .build();
        OffreDeStage offreEnregistree = offreDeStageRepository.save(offre);

        eventPublisher.publishEvent(
                new NouvelleOffreTeleverseeEvent(offreEnregistree)
        );

        return OffreDeStageDTO.of(offreEnregistree);
    }

    @Transactional(readOnly = true)
    public List<OffreDeStageDTO> obtenirOffres() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Employeur employeur = userAppRepository.findUserAppByEmail(email)
                .filter(Employeur.class::isInstance)
                .map(Employeur.class::cast)
                .orElseThrow(UserNotFoundException::new);

        return employeur.getOffres()
                .stream()
                .map(OffreDeStageDTO::of)
                .toList();
    }


    public Resource telechargerOffre(Long id, String email)
            throws OffreIntrouvableException, FichierIntrouvableException {

        OffreDeStage offre = offreDeStageRepository.findById(id)
                .orElseThrow(OffreIntrouvableException::new);

        if (offre.getStoragePath() == null || offre.getStoragePath().isEmpty()) {
            throw new FichierIntrouvableException();
        }

        return StockageFichierUtils.chargerFichier(Paths.get(offre.getStoragePath()));
    }


    private Employeur getEmployeurByEmail(String email) {
        return userAppRepository.findUserAppByEmail(email)
                .filter(Employeur.class::isInstance)
                .map(Employeur.class::cast)
                .orElseThrow(UserNotFoundException::new);
    }

    private void validerDates(CreationOffreDeStageDTO dto)
            throws DateFinAvantDateDebutException, DateFinAffichageAvantAujourdhuiException,
            DateFinAffichageApresDateDebutException, DateDebutAvantAujourdhuiException {

        if (dto.startDate().isBefore(LocalDate.now())) {
            throw new DateDebutAvantAujourdhuiException();
        }

        if (!dto.endDate().isAfter(dto.startDate())) {
            throw new DateFinAvantDateDebutException();
        }

        if (dto.displayEndDate().isBefore(LocalDate.now())) {
            throw new DateFinAffichageAvantAujourdhuiException();
        }

        if (dto.displayEndDate().isAfter(dto.startDate())) {
            throw new DateFinAffichageApresDateDebutException();
        }
    }

    @Transactional
    public OffreDeStageDTO modifierOffre(
            Long id,
            CreationOffreDeStageDTO dto,
            MultipartFile file,
            String email
    ) throws DateFinAvantDateDebutException,
            IOException,
            FichierTypeInvalideException, FichierCorrompuException, FichierTropVolumineuxException, OffreIntrouvableException,
            OffreNonAutoriseeException, OffreNonEnAttenteException, DateFinAffichageAvantAujourdhuiException, DateFinAffichageApresDateDebutException, DateDebutAvantAujourdhuiException {


        Employeur employeur = getEmployeurByEmail(email);
        OffreDeStage ancienneOffre = offreDeStageRepository.findById(id)
                .orElseThrow(OffreIntrouvableException::new);

        if (ancienneOffre.getEmployeur() == null
                || !ancienneOffre.getEmployeur().getId().equals(employeur.getId())) {
            throw new OffreNonAutoriseeException();
        }

        if (ancienneOffre.getStatut() != Statut.EN_ATTENTE) {
            throw new OffreNonEnAttenteException();
        }

        validerDates(dto);

        ancienneOffre.setTitle(dto.title());
        ancienneOffre.setDescription(dto.description());
        ancienneOffre.setSalary(dto.salary());
        ancienneOffre.setDomain(dto.domain());
        ancienneOffre.setStartDate(dto.startDate());
        ancienneOffre.setEndDate(dto.endDate());
        ancienneOffre.setDisplayEndDate(dto.displayEndDate());

        if (file != null && !file.isEmpty()) {
            Path path = StockageFichierUtils.sauvegarderPdf(file, STORAGE_OFFRE);
            ancienneOffre.setStoragePath(path.toString());
            ancienneOffre.setFileName(file.getOriginalFilename());
        }
        OffreDeStage nouvelleOffre = offreDeStageRepository.save(ancienneOffre);
        eventPublisher.publishEvent(new OffreModifierSoNotificationEvent(ancienneOffre,nouvelleOffre));
        return OffreDeStageDTO.of(nouvelleOffre);
    }

    public List<CandidatureDTO> getCandidaturesParOffre(Long offreId) throws OffreIntrouvableException, OffreNonAutoriseeException, UtilisateurIntrouvableException {

        OffreDeStage offre = offreDeStageRepository.findById(offreId)
                .orElseThrow(OffreIntrouvableException::new);

        String email = SecurityContextHolder.getContext().getAuthentication().getName() ;

        Employeur employeur = userAppRepository.findUserAppByEmail(email)
                .filter(Employeur.class::isInstance)
                .map(Employeur.class::cast)
                .orElseThrow(UtilisateurIntrouvableException::new);

        if(!offre.getEmployeur().getId().equals(employeur.getId())){
            throw new OffreNonAutoriseeException();
        }

        return candidatureRepository.findByOffreDeStage(offre)
                .stream()
                .map(CandidatureDTO::of)
                .toList();
    }

    public List<CandidatureDTO> obtenirCandidatures() throws UtilisateurIntrouvableException {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Employeur employeur = userAppRepository.findUserAppByEmail(email)
                .filter(Employeur.class::isInstance)
                .map(Employeur.class::cast)
                .orElseThrow(UtilisateurIntrouvableException::new);

        List<Candidature> candidatures =
                candidatureRepository.findByOffreDeStage_Employeur(employeur);

        return candidatures
                .stream()
                .map(CandidatureDTO::of)
                .toList();
    }


}
