package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.security.exception.CourrielExistantException;
import com.lacouf.rsbjwt.security.exception.MatriculeExistantException;
import com.lacouf.rsbjwt.security.exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import org.springframework.stereotype.Service;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class EtudiantService {
    private final EtudiantRepository etudiantRepository;
    private final UserAppRepository userAppRepository;
    private final PasswordEncoder passwordEncoder;

    public EtudiantService(EtudiantRepository etudiantRepository, UserAppRepository userAppRepository,PasswordEncoder passwordEncoder) {
        this.etudiantRepository = etudiantRepository;
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public EtudiantDTO inscrireEtudiant (InscriptionEtudiantDTO inscriptionEtudiantDto) throws CourrielExistantException, MatriculeExistantException, MotDePasseNonCorrespondantException {

        if (!inscriptionEtudiantDto.getPassword()
                .equals(inscriptionEtudiantDto.getConfirmPassword())) {
            //TODO : Exception personnalisée
            throw new IllegalArgumentException(
                    "Les mots de passe ne correspondent pas.");
        }

        if (userAppRepository.findUserAppByEmail(
                inscriptionEtudiantDto.getEmail()).isPresent()) {
            throw new CourrielExistantException(
                    "Cette adresse courriel est déjà utilisée.");
        }

        if (etudiantRepository.findByMatricule(
                inscriptionEtudiantDto.getMatricule()).isPresent()) {
            //TODO : Exception personnalisée (MatriculeDejaUtiliseException)//
            throw new IllegalArgumentException(
                    "Ce matricule est déjà utilisé.");
        }

        Etudiant etudiant = Etudiant.builder()
                .firstName(inscriptionEtudiantDto.getFirstName())
                .lastName(inscriptionEtudiantDto.getLastName())
                .email(inscriptionEtudiantDto.getEmail())
                .password(passwordEncoder.encode(
                        inscriptionEtudiantDto.getPassword()))
                .matricule(inscriptionEtudiantDto.getMatricule())
                .build();

        return EtudiantDTO.create(etudiantRepository.save(etudiant));
    }





}
