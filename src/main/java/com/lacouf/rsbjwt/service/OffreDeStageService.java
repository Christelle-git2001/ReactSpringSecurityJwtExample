package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.DateFinAvantDateDebutException;
import com.lacouf.rsbjwt.Exception.FichierCorrompuException;
import com.lacouf.rsbjwt.Exception.FichierTropVolumineuxException;
import com.lacouf.rsbjwt.Exception.FichierTypeInvalideException;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.StatutOffre;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import com.lacouf.rsbjwt.service.dto.CreationOffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class OffreDeStageService {
    private final static Path DOSSIER = Paths.get("pdf/pdfEmployeur/offresDeStage");
    private final static String TYPE_FICHIERS = "application/pdf";
    //private final static String EXTENSION_PDF = ".pdf";
    private final static long TAILLE_MAX = 5 * 1024 * 1024; // 5 Mo

    private final OffreDeStageRepository offreDeStageRepository;

    public OffreDeStageService(OffreDeStageRepository offreDeStageRepository) {
        this.offreDeStageRepository = offreDeStageRepository;
    }

    public OffreDeStageDTO creerOffre(CreationOffreDeStageDTO creationOffreDeStageDTO,
                                      MultipartFile multipartFile,
                                      Employeur employeur) throws DateFinAvantDateDebutException, IOException {

        if (creationOffreDeStageDTO.dateFin().isBefore(creationOffreDeStageDTO.dateDebut())){
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
                .build();

        return OffreDeStageDTO.of(offreDeStageRepository.save(offreDeStage));
    }

    private String validerFichier(MultipartFile multipartFile) throws FichierTypeInvalideException,
            FichierTropVolumineuxException, FichierCorrompuException, IOException {
        if (multipartFile.getSize() == 0){
            throw new FichierCorrompuException();
        }
        if (multipartFile.getSize() > TAILLE_MAX){
            throw  new FichierTropVolumineuxException();
        }
        if (!TYPE_FICHIERS.contains(multipartFile.getContentType())){
            throw  new FichierTypeInvalideException();
        }

        Files.createDirectories(DOSSIER);
        String nomFichier = multipartFile.getOriginalFilename();
        Path destination = DOSSIER.resolve(nomFichier);
        Files.copy(multipartFile.getInputStream(),destination , StandardCopyOption.REPLACE_EXISTING);

        return nomFichier;
    }

    private OffreDeStageDTO toDTO(OffreDeStage offreDeStage){
        return new OffreDeStageDTO(
                offreDeStage.getId()
                ,offreDeStage.getTitle(),
                offreDeStage.getPoste(),
                offreDeStage.getStatut(),
                offreDeStage.getSalary(),
                offreDeStage.getDescription(),
                offreDeStage.getFirstDate(),
                offreDeStage.getLastDate(),
                offreDeStage.getFilePath(),
                offreDeStage.getMessageRefus()
        );
    }
}
