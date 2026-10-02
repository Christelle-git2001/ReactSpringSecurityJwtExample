package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.repository.CvEtudiantRepository;
import com.lacouf.rsbjwt.service.dto.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import com.lacouf.rsbjwt.utils.StockageFichierUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.lacouf.rsbjwt.model.Enum.Departement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class EtudiantService {
    private final EtudiantRepository etudiantRepository;
    private final UserAppRepository userAppRepository;
    private final CvEtudiantRepository cvEtudiantRepository;
    private final PasswordEncoder passwordEncoder;
    private final Path STORAGE_CV = Paths.get("uploads", "cvs");

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
    public CvEtudiantDTO televerserCv(MultipartFile file, String email)
            throws FichierTypeInvalideException, EtudiantIntrouvableException,
            SuppressionFichierEchoueeException, FichierCorrompuException,
            FichierTropVolumineuxException, IOException {
        Etudiant etudiant = trouverEtudiantParEmail(email);
        Path filePath = StockageFichierUtils.sauvegarderPdf(file, STORAGE_CV);

        CvEtudiant cv = cvEtudiantRepository.findByEtudiant(etudiant)
                .orElseGet(() -> CvEtudiant.builder()
                        .etudiant(etudiant)
                        .build());

        supprimerAncienCv(cv, filePath);

        remplirInformationsCv(cv, file, filePath);

        return CvEtudiantDTO.of(cvEtudiantRepository.save(cv));
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

    @Transactional
    public void supprimerCv(String email)
            throws EtudiantIntrouvableException, FichierIntrouvableException,
            SuppressionFichierEchoueeException {
        Etudiant etudiant = trouverEtudiantParEmail(email);
        CvEtudiant cv = trouverCvParEtudiant(etudiant);

        StockageFichierUtils.supprimerFichier(Paths.get(cv.getStoragePath()));
        cvEtudiantRepository.delete(cv);
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
    }
}
