package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.CreationOffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEmployeurDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import com.lacouf.rsbjwt.utils.StockageFichierUtils;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Service
@Transactional(readOnly = true)
public class EmployeurService {
    private final EmployeurRepository employeurRepository;
    private final UserAppRepository userAppRepository;
    private final PasswordEncoder passwordEncoder;
    private final OffreDeStageRepository offreDeStageRepository;
    private final Path STORAGE_OFFRE = Paths.get("uploads", "offres");

    public EmployeurService(
            EmployeurRepository employeurRepository,
            UserAppRepository userAppRepository,
            PasswordEncoder passwordEncoder,
            OffreDeStageRepository offreDeStageRepository
    ) {
        this.employeurRepository = employeurRepository;
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
        this.offreDeStageRepository = offreDeStageRepository;
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
            throws DateFinAvantDateDebutException, IOException, FichierTypeInvalideException, FichierCorrompuException, FichierTropVolumineuxException {

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
                .statut(Statut.EN_ATTENTE)
                .fileName(fileName)
                .storagePath(storagePath)
                .employeur(employeur)
                .build();
        return OffreDeStageDTO.of(offreDeStageRepository.save(offre));
    }

    @Transactional(readOnly = true)
    public List<OffreDeStageDTO> obtenirOffres(String email) {

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
            throws DateFinAvantDateDebutException {

        if (!dto.endDate().isAfter(dto.startDate())) {
            throw new DateFinAvantDateDebutException();
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
            FichierTypeInvalideException, FichierCorrompuException, FichierTropVolumineuxException, OffreIntrouvableException, OffreNonAutoriseeException, OffreNonEnAttenteException {


        Employeur employeur = getEmployeurByEmail(email);
        OffreDeStage offre = offreDeStageRepository.findById(id)
                .orElseThrow(OffreIntrouvableException::new);

        if (offre.getEmployeur() == null
                || !offre.getEmployeur().getId().equals(employeur.getId())) {
            throw new OffreNonAutoriseeException();
        }

        if (offre.getStatut() != Statut.EN_ATTENTE) {
            throw new OffreNonEnAttenteException();
        }

        validerDates(dto);

        offre.setTitle(dto.title());
        offre.setDescription(dto.description());
        offre.setSalary(dto.salary());
        offre.setDomain(dto.domain());
        offre.setStartDate(dto.startDate());
        offre.setEndDate(dto.endDate());

        if (file != null && !file.isEmpty()) {
            Path path = StockageFichierUtils.sauvegarderPdf(file, STORAGE_OFFRE);
            offre.setStoragePath(path.toString());
            offre.setFileName(file.getOriginalFilename());
        }

        return OffreDeStageDTO.of(
                offreDeStageRepository.save(offre)
        );
    }
}
