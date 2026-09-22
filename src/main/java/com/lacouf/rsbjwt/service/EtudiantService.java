package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import org.springframework.stereotype.Service;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.lacouf.rsbjwt.model.Enum.Departement;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EtudiantService {
    private final EtudiantRepository etudiantRepository;
    private final UserAppRepository userAppRepository;
    private final PasswordEncoder passwordEncoder;

    public EtudiantService(EtudiantRepository etudiantRepository, UserAppRepository userAppRepository,PasswordEncoder passwordEncoder) {
        this.etudiantRepository = etudiantRepository;
        this.userAppRepository = userAppRepository;
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

}
