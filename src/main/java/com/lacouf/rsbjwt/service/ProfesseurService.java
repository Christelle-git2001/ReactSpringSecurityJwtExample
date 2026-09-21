package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.EmailExistantException;
import com.lacouf.rsbjwt.Exception.MatriculeExistantException;
import com.lacouf.rsbjwt.Exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Professeur;
import com.lacouf.rsbjwt.repository.ProfesseurRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.Exception.DepartementInvalideException;
import com.lacouf.rsbjwt.service.dto.InscriptionProfesseurDTO;
import com.lacouf.rsbjwt.service.dto.ProfesseurDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProfesseurService {

    private final ProfesseurRepository professeurRepository ;
    private final UserAppRepository userAppRepository ;
    private final PasswordEncoder passwordEncoder ;

    public ProfesseurService(ProfesseurRepository professeurRepository, UserAppRepository userAppRepository, PasswordEncoder passwordEncoder) {
        this.professeurRepository = professeurRepository;
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ProfesseurDTO creerCompteProfesseur(InscriptionProfesseurDTO inscriptionProfesseurDto) throws DepartementInvalideException, EmailExistantException, MatriculeExistantException, MotDePasseNonCorrespondantException
    {
        validerInscription(inscriptionProfesseurDto);

        Departement departement = normaliserDepartement(inscriptionProfesseurDto.department()) ;

        Professeur professeur = Professeur.builder()
                .firstName(inscriptionProfesseurDto.firstName())
                .lastName(inscriptionProfesseurDto.lastName())
                .email(inscriptionProfesseurDto.email())
                .password(passwordEncoder.encode(inscriptionProfesseurDto.password()))
                .matricule(inscriptionProfesseurDto.matricule())
                .phoneNumber(inscriptionProfesseurDto.phoneNumber())
                .department(departement)
                .build();

        return ProfesseurDTO.of(professeurRepository.save(professeur));

    }

    private void validerInscription(InscriptionProfesseurDTO inscriptionProfesseurDto) throws  EmailExistantException, MatriculeExistantException, MotDePasseNonCorrespondantException{
        if(userAppRepository.findUserAppByEmail(inscriptionProfesseurDto.email()).isPresent()){
            throw new EmailExistantException();
        }

        if(professeurRepository.findByMatricule(inscriptionProfesseurDto.matricule()).isPresent()){
            throw new MatriculeExistantException() ;
        }

        if(! inscriptionProfesseurDto.password().equals(inscriptionProfesseurDto.passwordConfirmation())){
            throw new MotDePasseNonCorrespondantException() ;
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
}
