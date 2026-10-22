package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.Notification.ControllerPath;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.event.CvSupprimerSoNotificationGestionnaireEvent;
import com.lacouf.rsbjwt.model.event.NouveauCvTeleverserEvent;
import com.lacouf.rsbjwt.model.event.NouvelleCandidatureEvent;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.dto.*;
import com.lacouf.rsbjwt.utils.StockageFichierUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.lacouf.rsbjwt.model.Enum.Departement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.lacouf.rsbjwt.repository.NotificationRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EtudiantService {
    private final EtudiantRepository etudiantRepository;
    private final UserAppRepository userAppRepository;
    private final CvEtudiantRepository cvEtudiantRepository;
    private final PasswordEncoder passwordEncoder;
    private final Path STORAGE_CV = Paths.get("uploads", "cvs");
    private final OffreDeStageRepository offreDeStageRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CandidatureRepository candidatureRepository;

    public EtudiantService(EtudiantRepository etudiantRepository, UserAppRepository userAppRepository,
                           CvEtudiantRepository cvEtudiantRepository, PasswordEncoder passwordEncoder,
                           OffreDeStageRepository offreDeStageRepository, ApplicationEventPublisher eventPublisher,
                           NotificationRepository notificationRepository, CandidatureRepository candidatureRepository) {
        this.etudiantRepository = etudiantRepository;
        this.userAppRepository = userAppRepository;
        this.cvEtudiantRepository = cvEtudiantRepository;
        this.passwordEncoder = passwordEncoder;
        this.offreDeStageRepository = offreDeStageRepository;
        this.eventPublisher = eventPublisher;
        this.candidatureRepository = candidatureRepository;
    }

    @Transactional
    public EtudiantDTO creerCompteEtudiant (InscriptionEtudiantDTO inscriptionEtudiantDto) throws EmailExistantException, MatriculeExistantException, MotDePasseNonCorrespondantException, DepartementInvalideException, NumeroTelephoneExistantException, MatriculeNonConforme {
        validerInscriptionEtudiant(inscriptionEtudiantDto);
        Departement departement = StockageFichierUtils.normaliserDepartement(inscriptionEtudiantDto.department()) ;

        Etudiant etudiant = Etudiant.builder()
                .firstName(inscriptionEtudiantDto.firstName())
                .lastName(inscriptionEtudiantDto.lastName())
                .email(inscriptionEtudiantDto.email())
                .phoneNumber(inscriptionEtudiantDto.phone())
                .matricule(inscriptionEtudiantDto.matricule())
                .department(departement)
                .password(passwordEncoder.encode(inscriptionEtudiantDto.password()))
                .build();

        return EtudiantDTO.of(etudiantRepository.save(etudiant));
    }

    private void validerInscriptionEtudiant(InscriptionEtudiantDTO inscriptionEtudiantDto) throws EmailExistantException, MatriculeExistantException, MotDePasseNonCorrespondantException, NumeroTelephoneExistantException, MatriculeNonConforme {

        if(inscriptionEtudiantDto.matricule().length() != 7){
            throw new MatriculeNonConforme();
        }

        if (!inscriptionEtudiantDto.password()
                .equals(inscriptionEtudiantDto.passwordConfirmation())) {
            throw new MotDePasseNonCorrespondantException();
        }

        if (userAppRepository.findUserAppByEmail(
                inscriptionEtudiantDto.email()).isPresent()) {
            throw new EmailExistantException();
        }

        if (etudiantRepository.findByMatricule(
                inscriptionEtudiantDto.matricule()).isPresent()) {
            throw new MatriculeExistantException();
        }
        if (userAppRepository.findByPhoneNumber(
                inscriptionEtudiantDto.phone()).isPresent()) {
            throw new NumeroTelephoneExistantException();
        }
    }

    @Transactional
    public CvEtudiantDTO televerserCv(MultipartFile file, String email)
            throws FichierTypeInvalideException, EtudiantIntrouvableException,
            SuppressionFichierEchoueeException, FichierCorrompuException,
            FichierTropVolumineuxException, IOException, CurriculumVitaeDejaApprouveException {
        Etudiant etudiant = trouverEtudiantParEmail(email);
        Path filePath = StockageFichierUtils.sauvegarderPdf(file, STORAGE_CV);

        CvEtudiant cv = cvEtudiantRepository.findByEtudiant(etudiant)
                .orElseGet(() -> CvEtudiant.builder()
                        .etudiant(etudiant)
                        .build());

        if (cv.getId() != null && cv.getStatut() == Statut.ACCEPTEE) {
            throw new CurriculumVitaeDejaApprouveException();
        }

        supprimerAncienCv(cv, filePath);

        remplirInformationsCv(cv, file, filePath);
        CvEtudiantDTO cvEtudiantDTO = CvEtudiantDTO.of(cvEtudiantRepository.save(cv));
        eventPublisher.publishEvent(new NouveauCvTeleverserEvent(cv));
        return cvEtudiantDTO;
    }

    public CvEtudiantDTO getCv(String email)
            throws EtudiantIntrouvableException, FichierIntrouvableException {
        Etudiant etudiant = trouverEtudiantParEmail(email);
        CvEtudiant cv = trouverCvParEtudiant(etudiant);

        return CvEtudiantDTO.of(cv);
    }

    public Resource telechargerCv(String email)
            throws EtudiantIntrouvableException, FichierIntrouvableException {
        Etudiant etudiant = trouverEtudiantParEmail(email);
        CvEtudiant cv = trouverCvParEtudiant(etudiant);

        return StockageFichierUtils.chargerFichier(Paths.get(cv.getStoragePath()));
    }

    @Transactional(rollbackFor = SuppressionFichierEchoueeException.class)
    public void supprimerCv(String email)
            throws EtudiantIntrouvableException, FichierIntrouvableException,
            SuppressionFichierEchoueeException {
        Etudiant etudiant = trouverEtudiantParEmail(email);
        CvEtudiant cv = trouverCvParEtudiant(etudiant);

       eventPublisher.publishEvent(new CvSupprimerSoNotificationGestionnaireEvent(ControllerPath.GESTIONNAIRE_CV_ID_DETAIL.build(String.valueOf(cv.getId()))));


        etudiant.setCv(null);
        cvEtudiantRepository.delete(cv);
        cvEtudiantRepository.flush();

        StockageFichierUtils.supprimerFichier(Paths.get(cv.getStoragePath()));
    }

    private void supprimerAncienCv(CvEtudiant cv, Path newPath)
            throws SuppressionFichierEchoueeException {
        if (cv.getStoragePath() == null) {
            return;
        }

        Path oldPathCv = Paths.get(cv.getStoragePath());

        if (!oldPathCv.equals(newPath)) {
            StockageFichierUtils.supprimerFichier(oldPathCv);
        }
    }

    private Etudiant trouverEtudiantParEmail(String email) throws EtudiantIntrouvableException {
        return etudiantRepository.findByCredentialsEmail(email)
                .orElseThrow(EtudiantIntrouvableException::new);
    }

    private CvEtudiant trouverCvParEtudiant(Etudiant etudiant)
            throws FichierIntrouvableException {
        return cvEtudiantRepository.findByEtudiant(etudiant)
                .orElseThrow(FichierIntrouvableException::new);
    }

    private void remplirInformationsCv(CvEtudiant cv, MultipartFile file, Path filePath){
        cv.setFileName(file.getOriginalFilename());
        cv.setContentType(file.getContentType());
        cv.setFileSize(file.getSize());
        cv.setStoragePath(filePath.toString());
        cv.setUploadDate(LocalDateTime.now());
        cv.setStatut(Statut.EN_ATTENTE);
        cv.setRejectionComment(null);
    }

    public List<OffreDeStageDTO> getOffresDisponibles(Departement departement, String email)
            throws EtudiantIntrouvableException,
            CurriculumVitaeNonApprouveException, CurriculumVitaeIntrouvable {

        Etudiant etudiant = trouverEtudiantParEmail(email);

        CvEtudiant cv = cvEtudiantRepository.findByEtudiant(etudiant)
                .orElseThrow(CurriculumVitaeIntrouvable::new);

        if (cv.getStatut() != Statut.ACCEPTEE) {
            throw new CurriculumVitaeNonApprouveException();
        }

        LocalDate aujourdHui = LocalDate.now();

        List<OffreDeStage> offresGenerales = offreDeStageRepository
                .findOffresGeneralesDisponibles(Statut.ACCEPTEE, departement, aujourdHui);

        List<OffreCiblee> offresCiblees = offreDeStageRepository
                .findOffresCibleesPourEtudiant(Statut.ACCEPTEE, departement, aujourdHui, etudiant);

        List<OffreDeStage> toutesLesOffres = new ArrayList<>();
        toutesLesOffres.addAll(offresGenerales);
        toutesLesOffres.addAll(offresCiblees);
        
        return toutesLesOffres.stream()
                .map(OffreDeStageDTO::of)
                .toList();
    }

    @Transactional
    public CandidatureDTO postuler(Long offreId, String etudiantEmail) throws EtudiantIntrouvableException, OffreIntrouvableException,
            OffreDeStageIndisponibleException, CandidatureDejaExistanteException, CurriculumVitaeIntrouvable, CurriculumVitaeNonApprouveException, OffreNonAutoriseeException {

        Etudiant etudiant = trouverEtudiantParEmail(etudiantEmail);

        CvEtudiant cv = cvEtudiantRepository.findByEtudiant(etudiant)
                .orElseThrow(CurriculumVitaeIntrouvable::new);

        if (cv.getStatut() != Statut.ACCEPTEE) {
            throw new CurriculumVitaeNonApprouveException();
        }

        OffreDeStage offre = offreDeStageRepository.findById(offreId)
                .orElseThrow(OffreIntrouvableException::new);

        if (offre.getStatut() != Statut.ACCEPTEE
                || offre.getDisplayEndDate().isBefore(LocalDate.now())) {
            throw new OffreDeStageIndisponibleException();
        }

        if (offre instanceof OffreCiblee offreCiblee) {
            if (offreCiblee.getEtudiant() == null || !offreCiblee.getEtudiant().getId().equals(etudiant.getId())) {
                throw new OffreNonAutoriseeException();
            }
        }

        if (candidatureRepository.existsByEtudiantAndOffreDeStage(
                etudiant, offre)) {
            throw new CandidatureDejaExistanteException();
        }

        Candidature candidature = new Candidature(etudiant, offre);

        Candidature candidatureEnregistree =
                candidatureRepository.save(candidature);

        eventPublisher.publishEvent(new NouvelleCandidatureEvent(candidatureEnregistree));

        return CandidatureDTO.of(candidatureEnregistree);
    }

    public List<CandidatureDTO> getCandidaturesEtudiant(String email)
            throws EtudiantIntrouvableException {
        Etudiant etudiant = trouverEtudiantParEmail(email);

        return candidatureRepository.findByEtudiant(etudiant)
                .stream()
                .map(CandidatureDTO::of)
                .toList();
    }

}
