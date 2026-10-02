package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.StatutOffre;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.CreationOffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEmployeurDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
@Transactional(readOnly = true)
public class EmployeurService {
    private final EmployeurRepository employeurRepository;
    private final UserAppRepository userAppRepository;
    private final PasswordEncoder passwordEncoder;
    private final OffreDeStageRepository offreDeStageRepository;
    private final FileStorageService fileStorageService;

    public EmployeurService(
            EmployeurRepository employeurRepository,
            UserAppRepository userAppRepository,
            PasswordEncoder passwordEncoder,
            OffreDeStageRepository offreDeStageRepository,
            FileStorageService fileStorageService
    ) {
        this.employeurRepository = employeurRepository;
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
        this.offreDeStageRepository = offreDeStageRepository;
        this.fileStorageService = fileStorageService;
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
    public OffreDeStageDTO creerOffre(CreationOffreDeStageDTO dto,
                                      MultipartFile file,
                                      String email)
            throws DateFinAvantDateDebutException, IOException, FichierTypeInvalideException, FichierCorrompuException, FichierTropVolumineuxException {

        Employeur employeur = getEmployeurByEmail(email);

        validerDates(dto);

        String fileName = null;

        if (file != null && !file.isEmpty()) {
            fileStorageService.storeOfferFile(file);
            fileName = file.getOriginalFilename();
        }

        OffreDeStage offre = OffreDeStage.builder()
                .title(dto.title())
                .description(dto.description())
                .salary(dto.salary())
                .domain(dto.domain())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .statut(StatutOffre.EN_ATTENTE)
                .fileName(fileName)
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
            FichierTypeInvalideException, FichierCorrompuException, FichierTropVolumineuxException {


        Employeur employeur = getEmployeurByEmail(email);
    //TODO : EXCEPETIONS PERSONNALISÉES
        OffreDeStage offre = offreDeStageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        if (offre.getEmployeur() == null
                || !offre.getEmployeur().getId().equals(employeur.getId())) {
            throw new RuntimeException("Cette offre ne vous appartient pas");
        }

        if (offre.getStatut() != StatutOffre.EN_ATTENTE) {
            throw new RuntimeException(
                    "Une offre qui n'est pas en attente ne peut pas être modifiée"
            );
        }

        validerDates(dto);

        offre.setTitle(dto.title());
        offre.setDescription(dto.description());
        offre.setSalary(dto.salary());
        offre.setDomain(dto.domain());
        offre.setStartDate(dto.startDate());
        offre.setEndDate(dto.endDate());

        if (file != null && !file.isEmpty()) {
            fileStorageService.storeOfferFile(file);
            offre.setFileName(file.getOriginalFilename());
        }

        return OffreDeStageDTO.of(
                offreDeStageRepository.save(offre)
        );
    }
}
