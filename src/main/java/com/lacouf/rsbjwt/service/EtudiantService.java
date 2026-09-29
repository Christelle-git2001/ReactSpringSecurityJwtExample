package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.repository.CvEtudiantRepository;
import com.lacouf.rsbjwt.service.dto.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import org.springframework.stereotype.Service;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.lacouf.rsbjwt.model.Enum.Departement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EtudiantService {
    private final EtudiantRepository etudiantRepository;
    private final UserAppRepository userAppRepository;
    private final CvEtudiantRepository cvEtudiantRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String TYPE_PDF = "application/pdf";
    private final Path storageDirectory = Paths.get("uploads", "cvs");

    public EtudiantService(EtudiantRepository etudiantRepository, UserAppRepository userAppRepository,
                           CvEtudiantRepository cvEtudiantRepository, PasswordEncoder passwordEncoder) {
        this.etudiantRepository = etudiantRepository;
        this.userAppRepository = userAppRepository;
        this.cvEtudiantRepository = cvEtudiantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public EtudiantDTO creerCompteEtudiant (InscriptionEtudiantDTO inscriptionEtudiantDto) throws EmailExistantException, MatriculeExistantException, MotDePasseNonCorrespondantException, DepartementInvalideException, NumeroTelephoneExistantException {
        validerInscriptionEtudiant(inscriptionEtudiantDto);
        Departement departement = normaliserDepartement(inscriptionEtudiantDto.department()) ;

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

    private void validerInscriptionEtudiant(InscriptionEtudiantDTO inscriptionEtudiantDto)  throws EmailExistantException, MatriculeExistantException, MotDePasseNonCorrespondantException, NumeroTelephoneExistantException{

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

    private Departement normaliserDepartement(String value) throws DepartementInvalideException {
        if(value == null){
            throw new DepartementInvalideException(value);
        }

        String normalized = value.trim()
                .toUpperCase()
                .replaceAll("\\s+", "_");

        for (Departement departement : Departement.values()) {
            if (departement.name().equals(normalized)) {
                return departement;
            }
        }

        throw new DepartementInvalideException(value);
    }

    @Transactional
    public CvEtudiantDTO uploadCv(MultipartFile file, String email)
        throws FichierInvalideException, EtudiantIntrouvableException,
        SuppressionEchoueeFichierException {
        if (!estFichierPdf(file)) {
            throw new FichierInvalideException();
        }

        Etudiant etudiant = trouverEtudiantParEmail(email);
        String fileName = genererNomFichier(file);
        Path filePath = storageDirectory.resolve(fileName).normalize();

        sauvegarderFichier(file, filePath);
        CvEtudiant cv = cvEtudiantRepository.findByEtudiant(etudiant)
                .orElseGet(() -> CvEtudiant.builder().etudiant(etudiant).build());

        supprimerAncienCv(cv, filePath);
        remplirInformationsCv(cv, file, filePath);

        return CvEtudiantDTO.of(cvEtudiantRepository.save(cv));
    }

    private Etudiant trouverEtudiantParEmail(String email) throws EtudiantIntrouvableException {
        return etudiantRepository.findByCredentialsEmail(email)
                .orElseThrow(EtudiantIntrouvableException::new);
    }

    private String genererNomFichier(MultipartFile file) {
        return UUID.randomUUID() + ".pdf";
    }

    private void sauvegarderFichier(MultipartFile file, Path cheminFichier)
            throws FichierInvalideException {
        try {
            Files.createDirectories(storageDirectory);
            Files.copy(file.getInputStream(), cheminFichier);
        } catch (IOException e) {
            throw new FichierInvalideException();
        }
    }

    private void supprimerAncienCv(CvEtudiant cv, Path newPath)
            throws SuppressionEchoueeFichierException {
        if (cv.getStoragePath() == null) { return; }
        Path oldPathCv = Paths.get(cv.getStoragePath());
        if(!oldPathCv.equals(newPath)){
            try{
                Files.deleteIfExists(oldPathCv);
            }catch(IOException e){
                throw new SuppressionEchoueeFichierException();
            }
        }
    }

    private void remplirInformationsCv(CvEtudiant cv, MultipartFile file, Path filePath){
        cv.setFileName(file.getOriginalFilename());
        cv.setContentType(file.getContentType());
        cv.setFileSize(file.getSize());
        cv.setStoragePath(filePath.toString());
        cv.setUploadDate(LocalDateTime.now());
    }

    private boolean estFichierPdf(MultipartFile file) {
        return file != null
                && !file.isEmpty()
                && TYPE_PDF.equals(file.getContentType());
    }
}
