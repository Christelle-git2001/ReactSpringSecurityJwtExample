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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Service
@Transactional(readOnly = true)
public class EmployeurService {
    private final static Path DOSSIER = Paths.get("pdf/pdfEmployeur/offresDeStage");
    private final static String TYPE_FICHIERS = "application/pdf";
    //private final static String EXTENSION_PDF = ".pdf";
    private final static long TAILLE_MAX = 5 * 1024 * 1024;
    // 5 Mo
    private final EmployeurRepository employeurRepository;
    private final UserAppRepository userAppRepository;
    private final PasswordEncoder passwordEncoder;
    private final OffreDeStageRepository offreDeStageRepository;


    public EmployeurService(EmployeurRepository employeurRepository, UserAppRepository userAppRepository, PasswordEncoder passwordEncoder,
                            OffreDeStageRepository offreDeStageRepository) {
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

    // Offre de stage

    @Transactional(rollbackFor = IOException.class)
    public OffreDeStageDTO creerOffre(CreationOffreDeStageDTO creationOffreDeStageDTO,
                                      MultipartFile multipartFile,
                                      String email) throws DateFinAvantDateDebutException, IOException {

        Employeur employeur = userAppRepository.findUserAppByEmail(email)
                .filter(user -> user instanceof Employeur)
                .map(user -> (Employeur) user)
                .orElseThrow(UserNotFoundException::new);

        if (!creationOffreDeStageDTO.dateFin().isAfter(creationOffreDeStageDTO.dateDebut())){
            throw new DateFinAvantDateDebutException();
        }
        String cheminFichier = null; // Au cas où l'employeur décide de ne soumettre aucun fichier
        if (multipartFile != null ){
            cheminFichier = validerFichier(multipartFile);
        }

        OffreDeStage offreDeStage = OffreDeStage.builder()
                .title(creationOffreDeStageDTO.titre())
                .salary(creationOffreDeStageDTO.salaire())
                .poste(creationOffreDeStageDTO.poste())
                .statut(StatutOffre.EN_ATTENTE)
                .description(creationOffreDeStageDTO.description())
                .firstDate(creationOffreDeStageDTO.dateDebut())
                .lastDate(creationOffreDeStageDTO.dateFin())
                .filePath(cheminFichier)
                .employeur(employeur)
                .build();

        OffreDeStage offreSauvegardee = offreDeStageRepository.save(offreDeStage);

        if (cheminFichier != null){
            sauvegarderFichier(multipartFile, cheminFichier);
        }

        return OffreDeStageDTO.of(offreSauvegardee);
    }

    private String validerFichier(MultipartFile multipartFile) throws FichierTypeInvalideException,
            FichierTropVolumineuxException, FichierCorrompuException {
        if (multipartFile.getSize() == 0){
            throw new FichierCorrompuException();
        }
        if (multipartFile.getSize() > TAILLE_MAX){
            throw  new FichierTropVolumineuxException();
        }
        if (!TYPE_FICHIERS.contains(multipartFile.getContentType())){
            throw  new FichierTypeInvalideException();
        }

        return multipartFile.getOriginalFilename();
    }

    private void sauvegarderFichier(MultipartFile multipartFile, String nomFichier) throws IOException {
        Files.createDirectories(DOSSIER);
        Path destination = DOSSIER.resolve(nomFichier);
        Files.copy(multipartFile.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
    }

}
