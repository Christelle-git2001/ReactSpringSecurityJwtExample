package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Enum.StatutOffre;
import com.lacouf.rsbjwt.model.Gestionnaire;
import com.lacouf.rsbjwt.model.OffreDeStage;
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

    public GestionnaireService(PasswordEncoder passwordEncoder, OffreDeStageRepository offreDeStageRepository, UserAppRepository userAppRepository, GestionnaireRepository gestionnaireRepository){
        this.passwordEncoder = passwordEncoder;
        this.offreDeStageRepository = offreDeStageRepository;

        this.userAppRepository = userAppRepository;
        this.gestionnaireRepository = gestionnaireRepository;
    }

    @Transactional
    public void creerCompteGestionnaire(GestionnaireDto gestionnaireDto) throws EmailExistantException, NumeroTelephoneExistantException {
        validerInscriptionGestionnaire(gestionnaireDto);
        Gestionnaire gestionnaire = Gestionnaire.builder()
                .firstName(gestionnaireDto.firstName())
                .lastName(gestionnaireDto.lastName())
                .email(gestionnaireDto.email())
                .phoneNumber(gestionnaireDto.phoneNumber())
                .password(passwordEncoder.encode(gestionnaireDto.password()))
                .build();
        gestionnaireRepository.save(gestionnaire);
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
                .findByStatut(StatutOffre.EN_ATTENTE)
                .stream()
                .map(OffreDeStageDTO::of)
                .toList();
    }

    @Transactional
    public OffreDeStageDTO approuverOffre (Long id)
            throws OffreIntrouvableException, OffreNonEnAttenteException {
        OffreDeStage offre = offreDeStageRepository.findById(id)
                .orElseThrow(OffreIntrouvableException::new);

        if (offre.getStatut() != StatutOffre.EN_ATTENTE) {
            throw new OffreNonEnAttenteException();
        }

        offre.setStatut(StatutOffre.ACCEPTEE);
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

        if (offre.getStatut() != StatutOffre.EN_ATTENTE) {
            throw new OffreNonEnAttenteException();
        }

        if (commentaire == null || commentaire.isBlank()) {
            throw new CommentaireRefusObligatoireException();
        }

        offre.setStatut(StatutOffre.REFUSEE);
        offre.setRejectionComment(commentaire);

        return OffreDeStageDTO.of(
                offreDeStageRepository.save(offre)
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

}
