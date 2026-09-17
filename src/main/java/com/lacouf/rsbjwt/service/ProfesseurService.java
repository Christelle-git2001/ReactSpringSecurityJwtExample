package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Departement;
import com.lacouf.rsbjwt.model.Professeur;
import com.lacouf.rsbjwt.repository.ProfesseurRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.exception.DepartementInvalideException;
import com.lacouf.rsbjwt.exception.EmailExistantException;
import com.lacouf.rsbjwt.exception.MatriculeExistantException;
import com.lacouf.rsbjwt.exception.MotDePasseNonCorrespondantException;
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

    public ProfesseurDto creerCompte(InscriptionProfesseurDto inscriptionProfesseurDto) throws DepartementInvalideException, EmailExistantException, MatriculeExistantException, MotDePasseNonCorrespondantException
    {
        if(userAppRepository.findUserAppByEmail(inscriptionProfesseurDto.email()).isPresent()){
            throw new EmailExistantException();
        }

        if(professeurRepository.findByMatricule(inscriptionProfesseurDto.matricule()).isPresent()){
            throw new MatriculeExistantException() ;
        }

        if(! inscriptionProfesseurDto.password().equals(inscriptionProfesseurDto.confirmPassword())){
            throw new MotDePasseNonCorrespondantException() ;
        }

        Departement departement = validerDepartement(inscriptionProfesseurDto.department()) ;

        Professeur professeur = Professeur.builder()
                .firstName(inscriptionProfesseurDto.firstName())
                .lastName(inscriptionProfesseurDto.lastName())
                .email(inscriptionProfesseurDto.email())
                .password(passwordEncoder.encode(inscriptionProfesseurDto.password()))
                .matricule(inscriptionProfesseurDto.matricule())
                .phoneNumber(inscriptionProfesseurDto.phoneNumber())
                .department(departement)
                .build();

        return ProfesseurDto.of(professeurRepository.save(professeur));

    }

    private Departement validerDepartement(String value) throws DepartementInvalideException {
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
}
