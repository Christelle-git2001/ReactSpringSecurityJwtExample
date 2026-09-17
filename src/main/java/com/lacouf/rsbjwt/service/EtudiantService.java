package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Etudiant;
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

        if (!inscriptionEtudiantDto.password()
                .equals(inscriptionEtudiantDto.passwordConfirmation())) {
            //TODO : Exception personnalisée
            throw new IllegalArgumentException(
                    "Les mots de passe ne correspondent pas.");
        }

        if (userAppRepository.findUserAppByEmail(
                inscriptionEtudiantDto.email()).isPresent()) {
            throw new EmailExistantException(
                    "Cette adresse courriel est déjà utilisée.");
        }

        if (etudiantRepository.findByMatricule(
                inscriptionEtudiantDto.matricule()).isPresent()) {
            //TODO : Exception personnalisée (MatriculeDejaUtiliseException)//
            throw new IllegalArgumentException(
                    "Ce matricule est déjà utilisé.");
        }

        Etudiant etudiant = Etudiant.builder()
                .firstName(inscriptionEtudiantDto.firstName())
                .lastName(inscriptionEtudiantDto.lastName())
                .email(inscriptionEtudiantDto.email())
                .password(passwordEncoder.encode(
                        inscriptionEtudiantDto.password()))
                .matricule(inscriptionEtudiantDto.matricule())
                .phoneNumber(inscriptionEtudiantDto.phone())
                .build();

        return EtudiantDTO.of(etudiantRepository.save(etudiant));
    }





}
