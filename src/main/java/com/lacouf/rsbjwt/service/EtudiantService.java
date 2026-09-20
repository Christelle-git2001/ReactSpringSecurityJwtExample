package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.DepartementInvalideException;
import com.lacouf.rsbjwt.Exception.EmailExistantException;
import com.lacouf.rsbjwt.Exception.MatriculeExistantException;
import com.lacouf.rsbjwt.Exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import org.springframework.stereotype.Service;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.lacouf.rsbjwt.model.Enum.Departement;

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

    public EtudiantDTO creerCompteEtudiant (InscriptionEtudiantDTO inscriptionEtudiantDto) throws EmailExistantException, MatriculeExistantException, MotDePasseNonCorrespondantException, DepartementInvalideException {
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

    private void validerInscriptionEtudiant(InscriptionEtudiantDTO inscriptionEtudiantDto)  throws EmailExistantException, MatriculeExistantException, MotDePasseNonCorrespondantException {

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
