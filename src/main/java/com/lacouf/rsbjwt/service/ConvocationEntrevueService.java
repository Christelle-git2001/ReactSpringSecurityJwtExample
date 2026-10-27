
package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.CandidatureDejaExistanteException;
import com.lacouf.rsbjwt.Exception.CandidatureIntrouvableException;
import com.lacouf.rsbjwt.Exception.ConvocationInvalideException;
import com.lacouf.rsbjwt.Exception.OffreNonAutoriseeException;
import com.lacouf.rsbjwt.model.Candidature;
import com.lacouf.rsbjwt.model.ConvocationEntrevue;
import com.lacouf.rsbjwt.model.PlageHoraire;
import com.lacouf.rsbjwt.model.Enum.ModaliteEntrevue;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.repository.CandidatureRepository;
import com.lacouf.rsbjwt.repository.ConvocationEntrevueRepository;
import com.lacouf.rsbjwt.service.dto.ConvocationEntrevueDTO;
import com.lacouf.rsbjwt.service.dto.PlageHoraireDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@Service
public class ConvocationEntrevueService {

    private final CandidatureRepository candidatureRepository;
    private final ConvocationEntrevueRepository convocationRepository;
    private final UserAppRepository userAppRepository;

    public ConvocationEntrevueService(
            CandidatureRepository candidatureRepository,
            ConvocationEntrevueRepository convocationRepository,
            UserAppRepository userAppRepository
    ) {
        this.candidatureRepository = candidatureRepository;
        this.convocationRepository = convocationRepository;
        this.userAppRepository = userAppRepository;
    }

    @Transactional
    public ConvocationEntrevueDTO creerConvocation(Long candidatureId, ConvocationEntrevueDTO dto) throws OffreNonAutoriseeException, CandidatureIntrouvableException,
            CandidatureDejaExistanteException, ConvocationInvalideException{
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(CandidatureIntrouvableException::new);

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Employeur employeur = userAppRepository.findUserAppByEmail(email)
                .filter(Employeur.class::isInstance)
                .map(Employeur.class::cast)
                .orElseThrow(UserNotFoundException::new);

        if (candidature.getOffreDeStage().getEmployeur() == null
                || !candidature.getOffreDeStage()
                .getEmployeur()
                .getId()
                .equals(employeur.getId())) {
            throw new OffreNonAutoriseeException();
        }

        if (convocationRepository.findByCandidatureId(candidatureId).isPresent()) {
            throw new CandidatureDejaExistanteException();
        }
        validerModalite(dto);

        ConvocationEntrevue convocation = new ConvocationEntrevue();
        convocation.setCandidature(candidature);
        convocation.setModalite(dto.modalite());
        convocation.setAdresse(dto.adresse());
        convocation.setNumeroBureau(dto.numeroBureau());
        convocation.setLienVisioconference(dto.lienVisioconference());
        convocation.setPlateformeReunion(dto.plateformeReunion());
        convocation.setInstructionsComplementaires(
                dto.instructionsComplementaires());
        convocation.setDateCreation(LocalDateTime.now());

        for (PlageHoraireDTO plageDTO : dto.plagesHoraires()) {
            PlageHoraire plage = new PlageHoraire();
            plage.setConvocation(convocation);
            plage.setDateHeureDebut(plageDTO.dateHeureDebut());
            plage.setDureeMinutes(plageDTO.dureeMinutes());
            convocation.getPlagesHoraires().add(plage);
        }

        candidature.setStatut(StatutCandidature.CONVOCATION_ENVOYEE);

        ConvocationEntrevue convocationEnregistree =
                convocationRepository.save(convocation);

        return ConvocationEntrevueDTO.of(convocationEnregistree);
    }

    private void validerModalite(ConvocationEntrevueDTO dto) throws ConvocationInvalideException {
        if (dto.modalite() == ModaliteEntrevue.PRESENTIEL) {
            if (estVide(dto.adresse()) || estVide(dto.numeroBureau())) {
                throw new ConvocationInvalideException();
            }
        } else if (dto.modalite() == ModaliteEntrevue.DISTANCIEL) {
            if (estVide(dto.lienVisioconference())
                    && estVide(dto.plateformeReunion())) {
                throw new ConvocationInvalideException();
            }
        }
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}
