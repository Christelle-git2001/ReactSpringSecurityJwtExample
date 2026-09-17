package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Professeur;
import com.lacouf.rsbjwt.repository.ProfesseurRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.InscriptionProfesseurDto;
import com.lacouf.rsbjwt.service.dto.ProfesseurDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfesseurService {

    private final ProfesseurRepository professeurRepository ;
    private final UserAppRepository userAppRepository ;
    private final PasswordEncoder passwordEncoder ;

    public ProfesseurDto creerCompte(InscriptionProfesseurDto inscriptionProfesseurDto) throws Exception /*EmailDejaUtiliseException, MatriculeDejaUtiliseException, MotDePasseNonCorrespondantException */
    {
        if(userAppRepository.findUserAppByEmail(inscriptionProfesseurDto.email()).isPresent()){
            // TODO Exception courriel Existant
            throw new Exception() ;
        }

        if(professeurRepository.findByMatricule(inscriptionProfesseurDto.matricule()).isPresent()){
            // TODO Exception matricule Existant
            throw new Exception() ;
        }

        if(! inscriptionProfesseurDto.password().equals(inscriptionProfesseurDto.confirmPassword())){
            // TODO Exception Mot de passe incorrect ??
            throw new Exception() ;
        }

        Professeur professeur = Professeur.builder()
                .firstName(inscriptionProfesseurDto.firstName())
                .lastName(inscriptionProfesseurDto.lastName())
                .email(inscriptionProfesseurDto.email())
                .password(passwordEncoder.encode(inscriptionProfesseurDto.password()))
                .matricule(inscriptionProfesseurDto.matricule())
                .phoneNumber(inscriptionProfesseurDto.phoneNumber())
                .department(inscriptionProfesseurDto.department())
                .build();

        return ProfesseurDto.of(professeur);

    }
}
